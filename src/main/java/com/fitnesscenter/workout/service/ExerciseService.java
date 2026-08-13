package com.fitnesscenter.workout.service;

import com.fitnesscenter.workout.dto.CreateExerciseRequest;
import com.fitnesscenter.workout.dto.ExerciseResponse;
import com.fitnesscenter.workout.entity.ExerciseCategory;

import java.util.List;

public interface ExerciseService {

    ExerciseResponse create(
            CreateExerciseRequest request
    );

    ExerciseResponse getById(
            Long id
    );

    List<ExerciseResponse> getAll();

    List<ExerciseResponse> getByCategory(
            ExerciseCategory category
    );

    ExerciseResponse deactivate(
            Long id
    );

    ExerciseResponse activate(
            Long id
    );
}