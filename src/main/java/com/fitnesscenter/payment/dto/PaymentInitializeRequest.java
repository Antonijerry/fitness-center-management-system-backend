package com.fitnesscenter.payment.dto;

import jakarta.validation.constraints.NotNull;

public record PaymentInitializeRequest(

        @NotNull(message = "Membership ID is required")
        Long membershipId
) {
}