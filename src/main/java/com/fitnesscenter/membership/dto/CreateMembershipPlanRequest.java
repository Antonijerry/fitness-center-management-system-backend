package com.fitnesscenter.membership.dto;

import com.fitnesscenter.membership.entity.MembershipType;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record CreateMembershipPlanRequest(

        @NotBlank(message = "Plan name is required")
        @Size(max = 100)
        String name,

        @Size(max = 500)
        String description,

        @NotNull(message = "Membership type is required")
        MembershipType type,

        @NotNull(message = "Price is required")
        @DecimalMin(
                value = "0.00",
                inclusive = false,
                message = "Price must be greater than zero"
        )
        @Digits(
                integer = 10,
                fraction = 2
        )
        BigDecimal price,

        @NotNull(message = "Duration is required")
        @Min(
                value = 1,
                message = "Duration must be at least one day"
        )
        Integer durationInDays,

        @NotNull(message = "Maximum monthly visits is required")
        @Min(
                value = 1,
                message = "Maximum visits must be at least 1"
        )
        Integer maxVisitsPerMonth,

        boolean autoRenewable
) {
}