package com.fitnesscenter.assignment.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateAssignmentRequest(

        @NotNull(message = "Trainer ID is required")
        Long trainerId,

        @NotNull(message = "Member ID is required")
        Long memberId,

        boolean primaryTrainer,

        @Size(
                max = 2000,
                message = "Notes cannot exceed 2000 characters"
        )
        String notes
) {
}