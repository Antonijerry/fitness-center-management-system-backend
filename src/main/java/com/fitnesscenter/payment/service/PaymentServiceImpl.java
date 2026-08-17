package com.fitnesscenter.payment.service;

import com.fitnesscenter.common.exception.ResourceNotFoundException;
import com.fitnesscenter.membership.entity.Membership;
import com.fitnesscenter.membership.entity.MembershipStatus;
import com.fitnesscenter.membership.repository.MembershipRepository;
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


    @Override
    @Transactional
    public PaymentInitializeResponse initializePayment(
            PaymentInitializeRequest request
    ) {

        Membership membership =
                membershipRepository.findById(
                                request.membershipId()
                        )
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Membership not found with id: "
                                                + request.membershipId()
                                )
                        );


        validateMembershipForPayment(
                membership
        );


        /*
         * Your Membership entity contains:
         *
         * private BigDecimal price;
         *
         * Therefore we use getPrice(), NOT getAmount().
         */
        BigDecimal amount =
                getMembershipAmount(
                        membership
                );


        String currency =
                getMembershipCurrency(
                        membership
                );


        String reference =
                generateReference();


        /*
         * Create our internal payment record
         * before calling Paystack.
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
                paymentRepository.save(
                        payment
                );


        /*
         * Membership -> User -> email
         *
         * Your Membership entity has:
         *
         * private User user;
         *
         * It does NOT have getMember().
         */
        String email =
                getMemberEmail(
                        membership
                );


        /*
         * Paystack expects amount in the
         * smallest currency denomination.
         *
         * Example:
         *
         * NGN 50,000.00
         *
         * becomes:
         *
         * 5,000,000 kobo
         */
        long gatewayAmount =
                toMinorUnit(
                        amount
                );


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

            paymentRepository.save(
                    savedPayment
            );

            throw new IllegalStateException(
                    "Unable to initialize Paystack payment"
            );
        }


        /*
         * Save Paystack's reference.
         */
        savedPayment.setGatewayReference(
                paystackResponse.data().reference()
        );


        savedPayment.setGatewayResponse(
                paystackResponse.message()
        );


        paymentRepository.save(
                savedPayment
        );


        /*
         * Return the information required by
         * the frontend.
         *
         * authorizationUrl:
         * Where the customer can be redirected.
         *
         * accessCode:
         * Can also be used by Paystack frontend
         * integrations.
         */
        return new PaymentInitializeResponse(
                savedPayment.getId(),
                savedPayment.getReference(),
                paystackResponse.data()
                        .authorizationUrl(),
                paystackResponse.data()
                        .accessCode(),
                gatewayAmount,
                currency
        );
    }


    @Override
    @Transactional
    public PaymentVerificationResponse verifyPayment(
            String reference
    ) {

        /*
         * FIRST find the payment.
         *
         * This must happen before using
         * payment.getStatus().
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
         * Idempotency:
         *
         * If this payment has already been
         * successfully processed, don't call
         * Paystack again unnecessarily.
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

            paymentRepository.save(
                    payment
            );

            throw new IllegalStateException(
                    "Unable to verify Paystack payment"
            );
        }


        PaystackVerifyResponse.Data data =
                response.data();


        /*
         * NEVER trust the status alone.
         *
         * Verify:
         *
         * reference
         * amount
         * currency
         */
        validateGatewayPayment(
                payment,
                data
        );


        PaymentStatus status =
                mapPaystackStatus(
                        data.status()
                );


        payment.setStatus(
                status
        );


        payment.setGatewayReference(
                data.reference()
        );


        payment.setGatewayResponse(
                response.message()
        );


        /*
         * Payment successfully completed.
         */
        if (status
                == PaymentStatus.SUCCESSFUL) {

            payment.setPaidAt(
                    LocalDateTime.now()
            );


            /*
             * Activate membership after
             * successful payment.
             */
            activateMembership(
                    payment.getMembership()
            );
        }


        paymentRepository.save(
                payment
        );


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


    /*
     * ---------------------------------------------------------
     * MEMBERSHIP VALIDATION
     * ---------------------------------------------------------
     */
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


    /*
     * ---------------------------------------------------------
     * MEMBERSHIP PRICE
     * ---------------------------------------------------------
     *
     * Your Membership entity has:
     *
     * private BigDecimal price;
     *
     * Therefore:
     *
     * membership.getPrice()
     */
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


    /*
     * ---------------------------------------------------------
     * CURRENCY
     * ---------------------------------------------------------
     *
     * Your Membership entity currently doesn't
     * contain a currency field.
     *
     * Therefore we use NGN for this system.
     */
    private String getMembershipCurrency(
            Membership membership
    ) {

        return "NGN";
    }


    /*
     * ---------------------------------------------------------
     * MEMBER EMAIL
     * ---------------------------------------------------------
     *
     * Membership:
     *
     *     membership.getUser()
     *
     * User:
     *
     *     user.getEmail()
     */
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


    /*
     * ---------------------------------------------------------
     * ACTIVATE MEMBERSHIP
     * ---------------------------------------------------------
     */
    private void activateMembership(
            Membership membership
    ) {

        if (membership == null) {

            throw new IllegalStateException(
                    "Payment has no associated membership"
            );
        }


        /*
         * Do not reactivate cancelled memberships.
         */
        if (membership.getStatus()
                == MembershipStatus.CANCELLED) {

            throw new IllegalStateException(
                    "Cancelled membership cannot be activated"
            );
        }


        membership.setStatus(
                MembershipStatus.ACTIVE
        );


        membershipRepository.save(
                membership
        );
    }


    /*
     * ---------------------------------------------------------
     * CONVERT NGN TO KOBO
     * ---------------------------------------------------------
     */
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


    /*
     * ---------------------------------------------------------
     * GENERATE UNIQUE PAYMENT REFERENCE
     * ---------------------------------------------------------
     */
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


    /*
     * ---------------------------------------------------------
     * VALIDATE PAYSTACK RESPONSE
     * ---------------------------------------------------------
     */
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


        if (expectedAmount
                != data.amount()) {

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


    /*
     * ---------------------------------------------------------
     * MAP PAYSTACK STATUS
     * ---------------------------------------------------------
     */
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