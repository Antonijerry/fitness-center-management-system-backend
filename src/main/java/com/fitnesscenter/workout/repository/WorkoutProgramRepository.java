package com.fitnesscenter.workout.repository;

import com.fitnesscenter.workout.entity.WorkoutProgram;
import com.fitnesscenter.workout.entity.WorkoutProgramStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WorkoutProgramRepository
        extends JpaRepository<WorkoutProgram, Long> {

    List<WorkoutProgram>
    findAllByMemberIdOrderByStartDateDesc(
            Long memberId
    );

    List<WorkoutProgram>
    findAllByTrainerIdOrderByStartDateDesc(
            Long trainerId
    );

    List<WorkoutProgram>
    findAllByStatusOrderByStartDateDesc(
            WorkoutProgramStatus status
    );

    boolean existsByMemberIdAndStatus(
            Long memberId,
            WorkoutProgramStatus status
    );
}