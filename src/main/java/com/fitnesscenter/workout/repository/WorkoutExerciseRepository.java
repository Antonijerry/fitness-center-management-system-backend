package com.fitnesscenter.workout.repository;

import com.fitnesscenter.workout.entity.WorkoutExercise;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WorkoutExerciseRepository
        extends JpaRepository<WorkoutExercise, Long> {

    List<WorkoutExercise>
    findAllByProgramDayIdOrderByExerciseOrderAsc(
            Long programDayId
    );

    boolean existsByProgramDayIdAndExerciseOrder(
            Long programDayId,
            Integer exerciseOrder
    );
}