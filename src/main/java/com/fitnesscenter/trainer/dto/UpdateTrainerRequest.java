package com.fitnesscenter.trainer.dto;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record UpdateTrainerRequest(

        @NotBlank(message = "Specialization is required")
        @Size(max = 150)
        String specialization,

        @Size(max = 1000)
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

        @Size(max = 2000)
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