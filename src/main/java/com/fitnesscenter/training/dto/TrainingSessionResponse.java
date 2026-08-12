package com.fitnesscenter.training.dto;

import com.fitnesscenter.training.entity.TrainingSessionStatus;
import com.fitnesscenter.training.entity.TrainingSessionType;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public record TrainingSessionResponse(

        Long id,

        Long trainerId,

        String trainerEmployeeNumber,

        String trainerName,

        Long memberId,

        String memberNumber,

        String memberName,

        LocalDate sessionDate,

        LocalTime startTime,

        LocalTime endTime,

        TrainingSessionType sessionType,

        TrainingSessionStatus status,

        String location,

        String notes,

        String cancellationReason,

        LocalDateTime startedAt,

        LocalDateTime completedAt
) {
}