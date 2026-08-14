package com.fitnesscenter.workout.service;

import com.fitnesscenter.workout.dto.LogWorkoutExerciseRequest;
import com.fitnesscenter.workout.dto.LogWorkoutSetRequest;
import com.fitnesscenter.workout.dto.StartWorkoutSessionRequest;
import com.fitnesscenter.workout.dto.WorkoutExerciseLogResponse;
import com.fitnesscenter.workout.dto.WorkoutSessionResponse;
import com.fitnesscenter.workout.dto.WorkoutSetLogResponse;

import java.util.List;

public interface WorkoutSessionService {

    WorkoutSessionResponse start(
            StartWorkoutSessionRequest request
    );

    WorkoutSessionResponse getById(
            Long id
    );

    List<WorkoutSessionResponse> getByMember(
            Long memberId
    );

    WorkoutSessionResponse complete(
            Long id
    );

    WorkoutSessionResponse cancel(
            Long id
    );

    WorkoutExerciseLogResponse logExercise(
            Long sessionId,
            LogWorkoutExerciseRequest request
    );

    WorkoutSetLogResponse logSet(
            Long exerciseLogId,
            LogWorkoutSetRequest request
    );

    List<WorkoutExerciseLogResponse> getExercises(
            Long sessionId
    );

    List<WorkoutSetLogResponse> getSets(
            Long exerciseLogId
    );
}