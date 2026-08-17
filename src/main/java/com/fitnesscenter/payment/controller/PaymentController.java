package com.fitnesscenter.payment.controller;

import com.fitnesscenter.common.response.ApiResponse;
import com.fitnesscenter.payment.dto.PaymentInitializeRequest;
import com.fitnesscenter.payment.dto.PaymentInitializeResponse;
import com.fitnesscenter.payment.dto.PaymentVerificationResponse;
import com.fitnesscenter.payment.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;


    /**
     * Initialize a payment for a membership.
     *
     * Creates a local PENDING payment and
     * initializes the transaction with Paystack.
     */
    @PostMapping("/initialize")
    public ResponseEntity<
            ApiResponse<PaymentInitializeResponse>
            > initializePayment(

            @Valid
            @RequestBody
            PaymentInitializeRequest request
    ) {

        PaymentInitializeResponse response =
                paymentService.initializePayment(
                        request
                );

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Payment initialized successfully",
                        response
                )
        );
    }


    /**
     * Verify a Paystack payment.
     *
     * The service validates:
     *
     * - payment reference
     * - payment amount
     * - payment currency
     * - Paystack status
     *
     * A successful payment activates the membership.
     */
    @GetMapping("/verify/{reference}")
    public ResponseEntity<
            ApiResponse<PaymentVerificationResponse>
            > verifyPayment(

            @PathVariable
            String reference
    ) {

        PaymentVerificationResponse response =
                paymentService.verifyPayment(
                        reference
                );

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Payment verification completed",
                        response
                )
        );
    }
}