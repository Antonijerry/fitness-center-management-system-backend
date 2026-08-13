package com.fitnesscenter.workout.repository;

import com.fitnesscenter.workout.entity.WorkoutProgramDay;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface WorkoutProgramDayRepository
        extends JpaRepository<WorkoutProgramDay, Long> {

    List<WorkoutProgramDay>
    findAllByWorkoutProgramIdOrderByDayNumberAsc(
            Long workoutProgramId
    );

    Optional<WorkoutProgramDay>
    findByWorkoutProgramIdAndDayNumber(
            Long workoutProgramId,
            Integer dayNumber
    );

    boolean existsByWorkoutProgramIdAndDayNumber(
            Long workoutProgramId,
            Integer dayNumber
    );
}