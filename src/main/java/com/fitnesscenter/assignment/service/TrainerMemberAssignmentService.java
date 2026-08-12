package com.fitnesscenter.assignment.service;

import com.fitnesscenter.assignment.dto.AssignmentResponse;
import com.fitnesscenter.assignment.dto.CreateAssignmentRequest;
import com.fitnesscenter.assignment.dto.UpdateAssignmentRequest;
import com.fitnesscenter.assignment.entity.AssignmentStatus;

import java.util.List;

public interface TrainerMemberAssignmentService {

    AssignmentResponse create(
            CreateAssignmentRequest request
    );

    AssignmentResponse getById(
            Long id
    );

    List<AssignmentResponse> getAll();

    List<AssignmentResponse> getByTrainer(
            Long trainerId
    );

    List<AssignmentResponse> getByMember(
            Long memberId
    );

    List<AssignmentResponse> getActiveByTrainer(
            Long trainerId
    );

    List<AssignmentResponse> getActiveByMember(
            Long memberId
    );

    AssignmentResponse update(
            Long id,
            UpdateAssignmentRequest request
    );

    AssignmentResponse endAssignment(
            Long id
    );
}