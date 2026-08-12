package com.fitnesscenter.training.dto;

import com.fitnesscenter.training.entity.TrainingSessionType;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.time.LocalTime;

public record CreateTrainingSessionRequest(

        @NotNull(message = "Trainer ID is required")
        Long trainerId,

        @NotNull(message = "Member ID is required")
        Long memberId,

        @NotNull(message = "Session date is required")
        @FutureOrPresent(
                message = "Session date cannot be in the past"
        )
        LocalDate sessionDate,

        @NotNull(message = "Start time is required")
        LocalTime startTime,

        @NotNull(message = "End time is required")
        LocalTime endTime,

        @NotNull(message = "Session type is required")
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