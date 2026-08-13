package com.fitnesscenter.workout.mapper;

import com.fitnesscenter.workout.dto.ExerciseResponse;
import com.fitnesscenter.workout.entity.Exercise;
import org.springframework.stereotype.Component;

@Component
public class ExerciseMapper {

    public ExerciseResponse toResponse(
            Exercise exercise
    ) {

        return new ExerciseResponse(

                exercise.getId(),

                exercise.getName(),

                exercise.getCategory(),

                exercise.getMuscleGroup(),

                exercise.getInstructions(),

                exercise.getEquipment(),

                exercise.isActive()
        );
    }
}