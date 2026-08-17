package com.fitnesscenter.payment.client;

public record PaystackInitializeRequest(

        String email,

        Long amount,

        String reference,

        String currency,

        String callback_url
) {
}