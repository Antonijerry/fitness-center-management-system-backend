package com.fitnesscenter.workout.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record LogWorkoutSetRequest(

        @NotNull(
                message = "Set number is required"
        )
        @Min(
                value = 1,
                message = "Set number must be greater than zero"
        )
        Integer setNumber,

        @Min(
                value = 1,
                message = "Repetitions must be greater than zero"
        )
        Integer repetitions,

        @Min(
                value = 0,
                message = "Weight cannot be negative"
        )
        BigDecimal weight,

        @Min(
                value = 1,
                message = "Duration must be greater than zero"
        )
        Integer durationSeconds,

        @Min(
                value = 0,
                message = "Distance cannot be negative"
        )
        BigDecimal distance,

        boolean completed,

        @Size(
                max = 2000,
                message = "Notes cannot exceed 2000 characters"
        )
        String notes
) {
}