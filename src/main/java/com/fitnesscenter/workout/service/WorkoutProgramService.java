package com.fitnesscenter.workout.service;

import com.fitnesscenter.workout.dto.AddWorkoutExerciseRequest;
import com.fitnesscenter.workout.dto.CreateWorkoutProgramDayRequest;
import com.fitnesscenter.workout.dto.CreateWorkoutProgramRequest;
import com.fitnesscenter.workout.dto.UpdateWorkoutProgramRequest;
import com.fitnesscenter.workout.dto.WorkoutExerciseResponse;
import com.fitnesscenter.workout.dto.WorkoutProgramDayResponse;
import com.fitnesscenter.workout.dto.WorkoutProgramResponse;

import java.util.List;

public interface WorkoutProgramService {

    WorkoutProgramResponse create(
            CreateWorkoutProgramRequest request
    );

    WorkoutProgramResponse getById(
            Long id
    );

    List<WorkoutProgramResponse> getAll();

    List<WorkoutProgramResponse> getByMember(
            Long memberId
    );

    List<WorkoutProgramResponse> getByTrainer(
            Long trainerId
    );

    WorkoutProgramResponse update(
            Long id,
            UpdateWorkoutProgramRequest request
    );

    WorkoutProgramResponse activate(
            Long id
    );

    WorkoutProgramResponse complete(
            Long id
    );

    WorkoutProgramResponse cancel(
            Long id
    );

    WorkoutProgramDayResponse addDay(
            Long programId,
            CreateWorkoutProgramDayRequest request
    );

    List<WorkoutProgramDayResponse> getDays(
            Long programId
    );

    WorkoutExerciseResponse addExercise(
            Long dayId,
            AddWorkoutExerciseRequest request
    );

    List<WorkoutExerciseResponse> getExercises(
            Long dayId
    );

    void removeExercise(
            Long exerciseId
    );
}