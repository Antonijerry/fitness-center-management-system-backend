package com.fitnesscenter.training.dto;

import com.fitnesscenter.training.entity.TrainingSessionType;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.time.LocalTime;

public record UpdateTrainingSessionRequest(

        LocalDate sessionDate,

        LocalTime startTime,

        LocalTime endTime,

        TrainingSessionType sessionType,

        @Size(
                max = 255,
                message = "Location cannot exceed 255 characters"
        )
        String location,

        @Size(
                max = 2000,
                message = "Notes cannot exceed 2000 characters"
        )
        String notes
) {
}