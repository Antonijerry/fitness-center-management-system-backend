package com.fitnesscenter.workout.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record WorkoutProgressSummaryResponse(

        Long memberId,

        LocalDate fromDate,

        LocalDate toDate,

        long totalWorkouts,

        long completedWorkouts,

        long cancelledWorkouts,

        long totalExercises,

        long totalSets,

        long completedSets,

        long totalRepetitions,

        BigDecimal totalVolume,

        BigDecimal averageWorkoutVolume,

        BigDecimal averageWorkoutDurationMinutes
) {
}