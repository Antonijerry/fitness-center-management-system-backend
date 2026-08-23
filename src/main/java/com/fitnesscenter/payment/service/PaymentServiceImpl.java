
        package com.fitnesscenter.payment.service;

import com.fitnesscenter.common.exception.ResourceNotFoundException;
import com.fitnesscenter.membership.entity.Membership;
import com.fitnesscenter.membership.entity.MembershipStatus;
import com.fitnesscenter.membership.repository.MembershipRepository;
import com.fitnesscenter.notification.entity.NotificationType;
import com.fitnesscenter.notification.service.NotificationService;
import com.fitnesscenter.payment.client.PaystackClient;
import com.fitnesscenter.payment.client.PaystackInitializeRequest;
import com.fitnesscenter.payment.client.PaystackInitializeResponse;
import com.fitnesscenter.payment.client.PaystackVerifyResponse;
import com.fitnesscenter.payment.dto.PaymentInitializeRequest;
import com.fitnesscenter.payment.dto.PaymentInitializeResponse;
import com.fitnesscenter.payment.dto.PaymentVerificationResponse;
import com.fitnesscenter.payment.entity.Payment;
import com.fitnesscenter.payment.entity.PaymentMethod;
import com.fitnesscenter.payment.entity.PaymentStatus;
import com.fitnesscenter.payment.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;

    private final MembershipRepository membershipRepository;

    private final PaystackClient paystackClient;

    private final NotificationService notificationService;


    // =========================================================
    // INITIALIZE PAYMENT
    // =========================================================

    @Override
    @Transactional
    public PaymentInitializeResponse initializePayment(
            PaymentInitializeRequest request
    ) {

        Membership membership =
                membershipRepository.findById(
                        request.membershipId()
                ).orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Membership not found with id: "
                                        + request.membershipId()
                        )
                );


        validateMembershipForPayment(membership);


        BigDecimal amount =
                getMembershipAmount(membership);


        String currency =
                getMembershipCurrency(membership);


        String reference =
                generateReference();


        /*
         * Create our internal payment record
         * before contacting Paystack.
         */
        Payment payment =
                Payment.builder()
                        .membership(membership)
                        .amount(amount)
                        .currency(currency)
                        .reference(reference)
                        .status(PaymentStatus.PENDING)
                        .paymentMethod(PaymentMethod.PAYSTACK)
                        .build();


        Payment savedPayment =
                paymentRepository.save(payment);


        String email =
                getMemberEmail(membership);


        /*
         * Paystack requires the amount in the
         * smallest currency unit.
         *
         * NGN 50,000.00
         * becomes
         * 5,000,000 kobo
         */
        long gatewayAmount =
                toMinorUnit(amount);


        PaystackInitializeRequest paystackRequest =
                new PaystackInitializeRequest(
                        email,
                        gatewayAmount,
                        reference,
                        currency,
                        null
                );


        PaystackInitializeResponse paystackResponse =
                paystackClient.initializeTransaction(
                        paystackRequest
                );


        /*
         * Paystack initialization failed.
         */
        if (paystackResponse == null
                || !paystackResponse.status()
                || paystackResponse.data() == null) {

            savedPayment.setStatus(
                    PaymentStatus.FAILED
            );

            savedPayment.setGatewayResponse(
                    paystackResponse != null
                            ? paystackResponse.message()
                            : "Empty Paystack response"
            );

            paymentRepository.save(savedPayment);

            throw new IllegalStateException(
                    "Unable to initialize Paystack payment"
            );
        }


        /*
         * Save Paystack transaction reference.
         */
        savedPayment.setGatewayReference(
                paystackResponse.data().reference()
        );


        savedPayment.setGatewayResponse(
                paystackResponse.message()
        );


        paymentRepository.save(savedPayment);


        /*
         * Return payment information to frontend.
         */
        return new PaymentInitializeResponse(
                savedPayment.getId(),
                savedPayment.getReference(),
                paystackResponse.data().authorizationUrl(),
                paystackResponse.data().accessCode(),
                gatewayAmount,
                currency
        );
    }


    // =========================================================
    // VERIFY PAYMENT
    // =========================================================

    @Override
    @Transactional
    public PaymentVerificationResponse verifyPayment(
            String reference
    ) {

        /*
         * First find our internal payment.
         */
        Payment payment =
                paymentRepository
                        .findByReference(reference)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Payment not found for reference: "
                                                + reference
                                )
                        );


        /*
         * Idempotency.
         *
         * If this payment has already been successfully
         * processed, do not process it again.
         */
        if (payment.getStatus()
                == PaymentStatus.SUCCESSFUL) {

            return new PaymentVerificationResponse(
                    payment.getId(),
                    payment.getReference(),
                    payment.getStatus(),
                    toMinorUnit(
                            payment.getAmount()
                    ),
                    payment.getCurrency(),
                    true
            );
        }


        /*
         * Ask Paystack for the authoritative
         * transaction status.
         */
        PaystackVerifyResponse response =
                paystackClient.verifyTransaction(
                        reference
                );


        /*
         * Paystack verification failed.
         */
        if (response == null
                || !response.status()
                || response.data() == null) {

            payment.setStatus(
                    PaymentStatus.FAILED
            );

            payment.setGatewayResponse(
                    response != null
                            ? response.message()
                            : "Empty Paystack verification response"
            );

            paymentRepository.save(payment);

            throw new IllegalStateException(
                    "Unable to verify Paystack payment"
            );
        }


        PaystackVerifyResponse.Data data =
                response.data();


        /*
         * Never trust the Paystack status alone.
         *
         * Validate:
         * 1. Reference
         * 2. Amount
         * 3. Currency
         */
        validateGatewayPayment(
                payment,
                data
        );


        /*
         * Convert Paystack status to our
         * internal PaymentStatus.
         */
        PaymentStatus status =
                mapPaystackStatus(
                        data.status()
                );


        /*
         * Save gateway information.
         */
        payment.setStatus(status);

        payment.setGatewayReference(
                data.reference()
        );

        payment.setGatewayResponse(
                response.message()
        );


        /*
         * IMPORTANT:
         *
         * Only activate the membership and send
         * notification when Paystack confirms
         * successful payment.
         */
        if (status == PaymentStatus.SUCCESSFUL) {

            payment.setPaidAt(
                    LocalDateTime.now()
            );


            /*
             * Activate membership.
             */
            activateMembership(
                    payment.getMembership()
            );


            /*
             * Save payment before notification.
             */
            paymentRepository.save(payment);


            /*
             * Create payment-success notification.
             *
             * Membership -> User -> ID
             */
            createPaymentSuccessfulNotification(
                    payment
            );

        } else {

            /*
             * Save PENDING / FAILED / ABANDONED
             * payment statuses.
             */
            paymentRepository.save(payment);
        }


        /*
         * Return verification response.
         */
        return new PaymentVerificationResponse(
                payment.getId(),
                payment.getReference(),
                payment.getStatus(),
                data.amount(),
                data.currency(),
                payment.getStatus()
                        == PaymentStatus.SUCCESSFUL
        );
    }


    // =========================================================
    // CHECK SUCCESSFUL PAYMENT
    // =========================================================

    @Override
    @Transactional(readOnly = true)
    public boolean hasSuccessfulPaymentForMembership(
            Long membershipId
    ) {

        return paymentRepository
                .existsByMembershipIdAndStatus(
                        membershipId,
                        PaymentStatus.SUCCESSFUL
                );
    }


    // =========================================================
    // MEMBERSHIP VALIDATION
    // =========================================================

    private void validateMembershipForPayment(
            Membership membership
    ) {

        if (membership.getStatus()
                == MembershipStatus.CANCELLED) {

            throw new IllegalStateException(
                    "Cancelled membership cannot be paid for"
            );
        }


        if (membership.getStatus()
                == MembershipStatus.ACTIVE) {

            throw new IllegalStateException(
                    "Membership is already active"
            );
        }
    }


    // =========================================================
    // GET MEMBERSHIP AMOUNT
    // =========================================================

    private BigDecimal getMembershipAmount(
            Membership membership
    ) {

        BigDecimal price =
                membership.getPrice();


        if (price == null) {

            throw new IllegalStateException(
                    "Membership price cannot be null"
            );
        }


        if (price.compareTo(BigDecimal.ZERO) <= 0) {

            throw new IllegalStateException(
                    "Membership price must be greater than zero"
            );
        }


        return price;
    }


    // =========================================================
    // GET CURRENCY
    // =========================================================

    private String getMembershipCurrency(
            Membership membership
    ) {

        /*
         * This system currently uses Nigerian Naira.
         */
        return "NGN";
    }


    // =========================================================
    // GET MEMBER EMAIL
    // =========================================================

    private String getMemberEmail(
            Membership membership
    ) {

        if (membership.getUser() == null) {

            throw new IllegalStateException(
                    "Membership does not have an associated user"
            );
        }


        if (membership.getUser().getEmail() == null
                || membership.getUser().getEmail().isBlank()) {

            throw new IllegalStateException(
                    "Member email is required for payment"
            );
        }


        return membership
                .getUser()
                .getEmail();
    }


    // =========================================================
    // ACTIVATE MEMBERSHIP
    // =========================================================

    private void activateMembership(
            Membership membership
    ) {

        if (membership == null) {

            throw new IllegalStateException(
                    "Payment has no associated membership"
            );
        }


        if (membership.getStatus()
                == MembershipStatus.CANCELLED) {

            throw new IllegalStateException(
                    "Cancelled membership cannot be activated"
            );
        }


        /*
         * Already active means nothing needs
         * to be changed.
         */
        if (membership.getStatus()
                == MembershipStatus.ACTIVE) {

            return;
        }


        membership.setStatus(
                MembershipStatus.ACTIVE
        );


        membershipRepository.save(
                membership
        );
    }


    // =========================================================
    // CREATE PAYMENT SUCCESS NOTIFICATION
    // =========================================================

    private void createPaymentSuccessfulNotification(
            Payment payment
    ) {

        Membership membership =
                payment.getMembership();


        if (membership == null) {

            return;
        }


        if (membership.getUser() == null) {

            return;
        }


        if (membership.getUser().getId() == null) {

            return;
        }


        notificationService.createNotification(

                membership
                        .getUser()
                        .getId(),

                NotificationType.PAYMENT_SUCCESSFUL,

                "Payment Successful",

                "Your membership payment was successfully processed.",

                payment.getId()
        );
    }


    // =========================================================
    // CONVERT TO MINOR CURRENCY UNIT
    // =========================================================

    private long toMinorUnit(
            BigDecimal amount
    ) {

        if (amount == null) {

            throw new IllegalArgumentException(
                    "Amount cannot be null"
            );
        }


        return amount
                .movePointRight(2)
                .longValueExact();
    }


    // =========================================================
    // GENERATE PAYMENT REFERENCE
    // =========================================================

    private String generateReference() {

        return "FIT-"
                + System.currentTimeMillis()
                + "-"
                + UUID.randomUUID()
                .toString()
                .replace("-", "")
                .substring(0, 8)
                .toUpperCase();
    }


    // =========================================================
    // VALIDATE PAYSTACK PAYMENT
    // =========================================================

    private void validateGatewayPayment(
            Payment payment,
            PaystackVerifyResponse.Data data
    ) {

        /*
         * Reference validation.
         */
        if (data.reference() == null
                || !payment.getReference()
                .equals(data.reference())) {

            throw new IllegalStateException(
                    "Payment reference mismatch"
            );
        }


        /*
         * Amount validation.
         */
        long expectedAmount =
                toMinorUnit(
                        payment.getAmount()
                );


        if (data.amount() == null
                || expectedAmount != data.amount()) {

            throw new IllegalStateException(
                    "Payment amount mismatch"
            );
        }


        /*
         * Currency validation.
         */
        if (data.currency() == null
                || !payment.getCurrency()
                .equalsIgnoreCase(
                        data.currency()
                )) {

            throw new IllegalStateException(
                    "Payment currency mismatch"
            );
        }
    }


    // =========================================================
    // MAP PAYSTACK STATUS
    // =========================================================

    private PaymentStatus mapPaystackStatus(
            String status
    ) {

        if (status == null) {

            return PaymentStatus.FAILED;
        }


        return switch (
                status.toLowerCase()
                ) {

            case "success" ->
                    PaymentStatus.SUCCESSFUL;

            case "abandoned" ->
                    PaymentStatus.ABANDONED;

            case "failed" ->
                    PaymentStatus.FAILED;

            case "pending" ->
                    PaymentStatus.PENDING;

            default ->
                    PaymentStatus.PENDING;
        };
    }
}
