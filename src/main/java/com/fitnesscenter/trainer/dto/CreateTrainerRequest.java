package com.fitnesscenter.trainer.dto;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record CreateTrainerRequest(

        @NotNull(message = "User ID is required")
        Long userId,

        @NotBlank(message = "Specialization is required")
        @Size(
                max = 150,
                message = "Specialization cannot exceed 150 characters"
        )
        String specialization,

        @Size(
                max = 1000,
                message = "Certifications cannot exceed 1000 characters"
        )
        String certifications,

        @Min(
                value = 0,
                message = "Years of experience cannot be negative"
        )
        @Max(
                value = 60,
                message = "Years of experience is invalid"
        )
        Integer yearsOfExperience,

        @Size(
                max = 2000,
                message = "Bio cannot exceed 2000 characters"
        )
        String bio,

        @DecimalMin(
                value = "0.00",
                inclusive = true,
                message = "Hourly rate cannot be negative"
        )
        @Digits(
                integer = 10,
                fraction = 2,
                message = "Invalid hourly rate"
        )
        BigDecimal hourlyRate
) {
}