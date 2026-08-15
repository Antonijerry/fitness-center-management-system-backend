package com.fitnesscenter.workout.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ExerciseProgressResponse(

        Long exerciseId,

        String exerciseName,

        LocalDate firstWorkoutDate,

        LocalDate latestWorkoutDate,

        BigDecimal bestWeight,

        Integer bestRepetitions,

        BigDecimal totalVolume,

        long totalSets,

        long completedSets,

        long totalRepetitions
) {
}