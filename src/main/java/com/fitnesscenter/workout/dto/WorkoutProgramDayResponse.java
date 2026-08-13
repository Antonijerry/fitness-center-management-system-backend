package com.fitnesscenter.workout.dto;

import java.util.List;

public record WorkoutProgramDayResponse(

        Long id,

        Integer dayNumber,

        String name,

        String notes,

        List<WorkoutExerciseResponse> exercises
) {
}