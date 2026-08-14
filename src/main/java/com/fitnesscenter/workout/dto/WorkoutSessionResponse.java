package com.fitnesscenter.workout.dto;

import com.fitnesscenter.workout.entity.WorkoutSessionStatus;

import java.time.LocalDateTime;
import java.util.List;

public record WorkoutSessionResponse(

        Long id,

        Long memberId,

        String memberName,

        Long workoutProgramId,

        String workoutProgramName,

        LocalDateTime startedAt,

        LocalDateTime completedAt,

        WorkoutSessionStatus status,

        String notes,

        List<WorkoutExerciseLogResponse> exercises
) {
}