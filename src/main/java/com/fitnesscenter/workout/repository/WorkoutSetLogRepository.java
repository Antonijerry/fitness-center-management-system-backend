package com.fitnesscenter.workout.repository;

import com.fitnesscenter.workout.entity.WorkoutSetLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WorkoutSetLogRepository
        extends JpaRepository<WorkoutSetLog, Long> {

    List<WorkoutSetLog>
    findAllByWorkoutExerciseLogIdOrderBySetNumberAsc(
            Long workoutExerciseLogId
    );

    boolean existsByWorkoutExerciseLogIdAndSetNumber(
            Long workoutExerciseLogId,
            Integer setNumber
    );

    //for workout analytics
    List<WorkoutSetLog>
    findAllByWorkoutExerciseLogIdIn(
            List<Long> exerciseLogIds
    );
}