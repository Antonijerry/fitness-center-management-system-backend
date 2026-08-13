package com.fitnesscenter.workout.dto;

import com.fitnesscenter.workout.entity.WorkoutExerciseType;

import java.math.BigDecimal;

public record WorkoutExerciseResponse(

        Long id,

        Long exerciseId,

        String exerciseName,

        Integer exerciseOrder,

        WorkoutExerciseType exerciseType,

        Integer sets,

        Integer repetitions,

        BigDecimal weight,

        Integer durationSeconds,

        BigDecimal distance,

        Integer restSeconds,

        String instructions
) {
}