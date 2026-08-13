package com.fitnesscenter.workout.dto;

import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record UpdateWorkoutProgramRequest(

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

        LocalDate startDate,

        LocalDate endDate
) {
}