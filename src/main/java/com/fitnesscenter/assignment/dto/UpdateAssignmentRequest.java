package com.fitnesscenter.assignment.dto;

import jakarta.validation.constraints.Size;

public record UpdateAssignmentRequest(

        boolean primaryTrainer,

        @Size(
                max = 2000,
                message = "Notes cannot exceed 2000 characters"
        )
        String notes
) {
}