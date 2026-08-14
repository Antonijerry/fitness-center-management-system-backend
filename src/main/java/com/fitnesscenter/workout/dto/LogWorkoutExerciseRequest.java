package com.fitnesscenter.workout.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record LogWorkoutExerciseRequest(

        @NotNull(
                message = "Exercise ID is required"
        )
        Long exerciseId,

        @NotNull(
                message = "Exercise order is required"
        )
        Integer exerciseOrder,

        @Size(
                max = 2000,
                message = "Notes cannot exceed 2000 characters"
        )
        String notes
) {
}