package com.fitnesscenter.workout.dto;

import com.fitnesscenter.workout.entity.WorkoutExerciseType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record AddWorkoutExerciseRequest(

        @NotNull(
                message = "Exercise ID is required"
        )
        Long exerciseId,

        @NotNull(
                message = "Exercise order is required"
        )
        @Min(
                value = 1,
                message = "Exercise order must be greater than zero"
        )
        Integer exerciseOrder,

        @NotNull(
                message = "Exercise type is required"
        )
        WorkoutExerciseType exerciseType,

        @Min(
                value = 1,
                message = "Sets must be greater than zero"
        )
        Integer sets,

        @Min(
                value = 1,
                message = "Repetitions must be greater than zero"
        )
        Integer repetitions,

        @Min(
                value = 0,
                message = "Weight cannot be negative"
        )
        Double weight,

        @Min(
                value = 1,
                message = "Duration must be greater than zero"
        )
        Integer durationSeconds,

        @Min(
                value = 0,
                message = "Distance cannot be negative"
        )
        Double distance,

        @Min(
                value = 0,
                message = "Rest time cannot be negative"
        )
        Integer restSeconds,

        @Size(
                max = 2000,
                message = "Instructions cannot exceed 2000 characters"
        )
        String instructions
) {
}