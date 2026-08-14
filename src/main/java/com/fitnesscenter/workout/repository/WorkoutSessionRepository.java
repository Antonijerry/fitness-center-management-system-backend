package com.fitnesscenter.workout.repository;

import com.fitnesscenter.workout.entity.WorkoutSession;
import com.fitnesscenter.workout.entity.WorkoutSessionStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WorkoutSessionRepository
        extends JpaRepository<WorkoutSession, Long> {

    List<WorkoutSession>
    findAllByMemberIdOrderByStartedAtDesc(
            Long memberId
    );

    List<WorkoutSession>
    findAllByWorkoutProgramIdOrderByStartedAtDesc(
            Long workoutProgramId
    );

    List<WorkoutSession>
    findAllByMemberIdAndStatusOrderByStartedAtDesc(
            Long memberId,
            WorkoutSessionStatus status
    );

    boolean existsByMemberIdAndStatus(
            Long memberId,
            WorkoutSessionStatus status
    );
}