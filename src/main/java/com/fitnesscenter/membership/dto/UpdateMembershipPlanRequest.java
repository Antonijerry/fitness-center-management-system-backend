package com.fitnesscenter.membership.dto;

import com.fitnesscenter.membership.entity.MembershipType;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record UpdateMembershipPlanRequest(

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
                inclusive = false
        )
        @Digits(
                integer = 10,
                fraction = 2
        )
        BigDecimal price,

        @NotNull
        @Min(1)
        Integer durationInDays,

        @NotNull
        @Min(1)
        Integer maxVisitsPerMonth,

        boolean active,

        boolean autoRenewable
) {
}