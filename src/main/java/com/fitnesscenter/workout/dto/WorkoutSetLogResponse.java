package com.fitnesscenter.workout.dto;

import java.math.BigDecimal;

public record WorkoutSetLogResponse(

        Long id,

        Integer setNumber,

        Integer repetitions,

        BigDecimal weight,

        Integer durationSeconds,

        BigDecimal distance,

        boolean completed,

        String notes
) {
}