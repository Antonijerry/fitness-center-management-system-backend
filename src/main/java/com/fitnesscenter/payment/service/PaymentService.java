package com.fitnesscenter.payment.service;

import com.fitnesscenter.payment.dto.PaymentInitializeRequest;
import com.fitnesscenter.payment.dto.PaymentInitializeResponse;
import com.fitnesscenter.payment.dto.PaymentVerificationResponse;

public interface PaymentService {

    PaymentInitializeResponse initializePayment(
            PaymentInitializeRequest request
    );

    PaymentVerificationResponse verifyPayment(
            String reference
    );

    boolean hasSuccessfulPaymentForMembership(
            Long membershipId
    );
}