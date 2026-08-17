package com.fitnesscenter.payment.dto;

import com.fitnesscenter.payment.entity.PaymentStatus;

public record PaymentVerificationResponse(

        Long paymentId,

        String reference,

        PaymentStatus status,

        Long amount,

        String currency,

        boolean successful
) {
}