package com.fitnesscenter.payment.dto;

public record PaymentInitializeResponse(

        Long paymentId,

        String reference,

        String authorizationUrl,

        String accessCode,

        Long amount,

        String currency
) {
}