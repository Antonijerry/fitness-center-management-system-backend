
        package com.fitnesscenter.payment.client;

import com.fasterxml.jackson.annotation.JsonProperty;

public record PaystackVerifyResponse(

        boolean status,

        String message,

        Data data

) {

    public record Data(

            Long id,

            String domain,

            String status,

            Long amount,

            String currency,

            String reference,

            @JsonProperty("paid_at")
            String paidAt,

            @JsonProperty("created_at")
            String createdAt,

            @JsonProperty("transaction_date")
            String transactionDate,

            String channel,

            @JsonProperty("gateway_response")
            String gatewayResponse,

            @JsonProperty("gateway_response_code")
            String gatewayResponseCode,

            Customer customer

    ) {
    }


    public record Customer(

            Long id,

            String email,

            @JsonProperty("customer_code")
            String customerCode

    ) {
    }
}

