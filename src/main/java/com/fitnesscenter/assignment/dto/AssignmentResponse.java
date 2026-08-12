package com.fitnesscenter.assignment.dto;

import com.fitnesscenter.assignment.entity.AssignmentStatus;

import java.time.LocalDateTime;

public record AssignmentResponse(

        Long id,

        Long trainerId,

        String trainerEmployeeNumber,

        String trainerName,

        Long memberId,

        String memberNumber,

        String memberName,

        LocalDateTime assignedAt,

        LocalDateTime endedAt,

        AssignmentStatus status,

        boolean primaryTrainer,

        String notes
) {
}