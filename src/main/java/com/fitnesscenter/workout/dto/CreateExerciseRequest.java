package com.fitnesscenter.workout.dto;

import com.fitnesscenter.workout.entity.ExerciseCategory;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateExerciseRequest(

        @NotBlank(
                message = "Exercise name is required"
        )
        @Size(
                max = 150,
                message = "Exercise name cannot exceed 150 characters"
        )
        String name,

        @NotNull(
                message = "Exercise category is required"
        )
        ExerciseCategory category,

        @Size(
                max = 100,
                message = "Muscle group cannot exceed 100 characters"
        )
        String muscleGroup,

        @Size(
                max = 2000,
                message = "Instructions cannot exceed 2000 characters"
        )
        String instructions,

        @Size(
                max = 1000,
                message = "Equipment cannot exceed 1000 characters"
        )
        String equipment
) {
}