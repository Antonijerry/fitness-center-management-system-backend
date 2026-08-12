package com.fitnesscenter.assignment.repository;

import com.fitnesscenter.assignment.entity.AssignmentStatus;
import com.fitnesscenter.assignment.entity.TrainerMemberAssignment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TrainerMemberAssignmentRepository
        extends JpaRepository<TrainerMemberAssignment, Long> {

    List<TrainerMemberAssignment>
    findAllByTrainerId(Long trainerId);

    List<TrainerMemberAssignment>
    findAllByMemberId(Long memberId);

    List<TrainerMemberAssignment>
    findAllByTrainerIdAndStatus(
            Long trainerId,
            AssignmentStatus status
    );

    List<TrainerMemberAssignment>
    findAllByMemberIdAndStatus(
            Long memberId,
            AssignmentStatus status
    );

    boolean existsByTrainerIdAndMemberIdAndStatus(
            Long trainerId,
            Long memberId,
            AssignmentStatus status
    );

    long countByTrainerIdAndStatus(
            Long trainerId,
            AssignmentStatus status
    );
}