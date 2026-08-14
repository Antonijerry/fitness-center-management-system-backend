package com.fitnesscenter.workout.repository;

import com.fitnesscenter.workout.entity.WorkoutExerciseLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface WorkoutExerciseLogRepository
        extends JpaRepository<WorkoutExerciseLog, Long> {

    List<WorkoutExerciseLog>
    findAllByWorkoutSessionIdOrderByExerciseOrderAsc(
            Long workoutSessionId
    );

    Optional<WorkoutExerciseLog>
    findByWorkoutSessionIdAndExerciseId(
            Long workoutSessionId,
            Long exerciseId
    );
}