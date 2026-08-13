package com.fitnesscenter.workout.dto;

import com.fitnesscenter.workout.entity.ExerciseCategory;

public record ExerciseResponse(

        Long id,

        String name,

        ExerciseCategory category,

        String muscleGroup,

        String instructions,

        String equipment,

        boolean active
) {
}