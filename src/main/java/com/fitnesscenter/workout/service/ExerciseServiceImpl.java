package com.fitnesscenter.workout.service;

import com.fitnesscenter.common.exception.ConflictException;
import com.fitnesscenter.common.exception.ResourceNotFoundException;
import com.fitnesscenter.workout.dto.CreateExerciseRequest;
import com.fitnesscenter.workout.dto.ExerciseResponse;
import com.fitnesscenter.workout.entity.Exercise;
import com.fitnesscenter.workout.entity.ExerciseCategory;
import com.fitnesscenter.workout.mapper.ExerciseMapper;
import com.fitnesscenter.workout.repository.ExerciseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ExerciseServiceImpl
        implements ExerciseService {

    private final ExerciseRepository exerciseRepository;

    private final ExerciseMapper exerciseMapper;


    @Override
    @Transactional
    public ExerciseResponse create(
            CreateExerciseRequest request
    ) {

        if (
                exerciseRepository.existsByNameIgnoreCase(
                        request.name()
                )
        ) {

            throw new ConflictException(
                    "An exercise with this name already exists"
            );
        }


        Exercise exercise = new Exercise();

        exercise.setName(
                request.name().trim()
        );

        exercise.setCategory(
                request.category()
        );

        exercise.setMuscleGroup(
                request.muscleGroup()
        );

        exercise.setInstructions(
                request.instructions()
        );

        exercise.setEquipment(
                request.equipment()
        );

        exercise.setActive(true);


        return exerciseMapper.toResponse(
                exerciseRepository.save(exercise)
        );
    }


    @Override
    public ExerciseResponse getById(
            Long id
    ) {

        return exerciseMapper.toResponse(
                getExercise(id)
        );
    }


    @Override
    public List<ExerciseResponse> getAll() {

        return exerciseRepository
                .findAllByActiveTrueOrderByNameAsc()
                .stream()
                .map(exerciseMapper::toResponse)
                .toList();
    }


    @Override
    public List<ExerciseResponse> getByCategory(
            ExerciseCategory category
    ) {

        return exerciseRepository
                .findAllByCategoryAndActiveTrueOrderByNameAsc(
                        category
                )
                .stream()
                .map(exerciseMapper::toResponse)
                .toList();
    }


    @Override
    @Transactional
    public ExerciseResponse deactivate(
            Long id
    ) {

        Exercise exercise =
                getExercise(id);

        exercise.setActive(false);

        return exerciseMapper.toResponse(
                exercise
        );
    }


    @Override
    @Transactional
    public ExerciseResponse activate(
            Long id
    ) {

        Exercise exercise =
                getExercise(id);

        exercise.setActive(true);

        return exerciseMapper.toResponse(
                exercise
        );
    }


    private Exercise getExercise(
            Long id
    ) {

        return exerciseRepository
                .findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Exercise not found"
                        )
                );
    }
}