package com.fitnesscenter.workout.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record StartWorkoutSessionRequest(

        @NotNull(
                message = "Workout program ID is required"
        )
        Long workoutProgramId,

        @Size(
                max = 2000,
                message = "Notes cannot exceed 2000 characters"
        )
        String notes
) {
}