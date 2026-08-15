package com.fitnesscenter.workout.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PersonalRecordResponse(

        Long exerciseId,

        String exerciseName,

        BigDecimal weight,

        Integer repetitions,

        BigDecimal estimatedOneRepMax,

        LocalDateTime achievedAt
) {
}