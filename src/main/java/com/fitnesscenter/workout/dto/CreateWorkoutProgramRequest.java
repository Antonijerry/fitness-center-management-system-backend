package com.fitnesscenter.workout.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record CreateWorkoutProgramRequest(

        @NotNull(
                message = "Member ID is required"
        )
        Long memberId,

        @NotNull(
                message = "Trainer ID is required"
        )
        Long trainerId,

        @NotBlank(
                message = "Program name is required"
        )
        @Size(
                max = 200,
                message = "Program name cannot exceed 200 characters"
        )
        String name,

        @Size(
                max = 2000,
                message = "Description cannot exceed 2000 characters"
        )
        String description,

        @Size(
                max = 255,
                message = "Goal cannot exceed 255 characters"
        )
        String goal,

        @NotNull(
                message = "Start date is required"
        )
        @FutureOrPresent(
                message = "Start date cannot be in the past"
        )
        LocalDate startDate,

        LocalDate endDate
) {
}