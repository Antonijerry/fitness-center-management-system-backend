package com.fitnesscenter.membership.dto;

import jakarta.validation.constraints.*;

import java.time.LocalDate;

public record CreateMembershipRequest(

        @NotNull(message = "User ID is required")
        Long userId,

        @NotNull(message = "Membership plan ID is required")
        Long planId,

        @NotNull(message = "Start date is required")
        LocalDate startDate,

        boolean autoRenewable,

        @Size(max = 500)
        String notes
) {
}