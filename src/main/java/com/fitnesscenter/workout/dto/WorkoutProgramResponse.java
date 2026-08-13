package com.fitnesscenter.workout.dto;

import com.fitnesscenter.workout.entity.WorkoutProgramStatus;

import java.time.LocalDate;
import java.util.List;

public record WorkoutProgramResponse(

        Long id,

        Long memberId,

        String memberName,

        Long trainerId,

        String trainerName,

        String name,

        String description,

        String goal,

        LocalDate startDate,

        LocalDate endDate,

        WorkoutProgramStatus status,

        List<WorkoutProgramDayResponse> days
) {
}