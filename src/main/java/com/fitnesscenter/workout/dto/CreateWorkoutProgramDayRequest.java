package com.fitnesscenter.workout.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateWorkoutProgramDayRequest(

        @NotNull(
                message = "Day number is required"
        )
        @Min(
                value = 1,
                message = "Day number must be greater than zero"
        )
        Integer dayNumber,

        @NotBlank(
                message = "Day name is required"
        )
        @Size(
                max = 150,
                message = "Day name cannot exceed 150 characters"
        )
        String name,

        @Size(
                max = 1000,
                message = "Notes cannot exceed 1000 characters"
        )
        String notes
) {
}