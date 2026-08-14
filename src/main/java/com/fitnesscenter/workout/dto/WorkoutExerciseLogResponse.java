package com.fitnesscenter.workout.dto;

import java.util.List;

public record WorkoutExerciseLogResponse(

        Long id,

        Long exerciseId,

        String exerciseName,

        Integer exerciseOrder,

        String notes,

        List<WorkoutSetLogResponse> sets
) {
}