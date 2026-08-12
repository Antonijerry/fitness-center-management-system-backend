package com.fitnesscenter.assignment.mapper;

import com.fitnesscenter.assignment.dto.AssignmentResponse;
import com.fitnesscenter.assignment.entity.TrainerMemberAssignment;
import org.springframework.stereotype.Component;

@Component
public class AssignmentMapper {

    public AssignmentResponse toResponse(
            TrainerMemberAssignment assignment
    ) {

        String trainerName =
                assignment.getTrainer()
                        .getUser()
                        .getFirstName()
                        + " "
                        + assignment.getTrainer()
                        .getUser()
                        .getLastName();

        String memberName =
                assignment.getMember()
                        .getUser()
                        .getFirstName()
                        + " "
                        + assignment.getMember()
                        .getUser()
                        .getLastName();

        return new AssignmentResponse(

                assignment.getId(),

                assignment.getTrainer().getId(),

                assignment.getTrainer()
                        .getEmployeeNumber(),

                trainerName,

                assignment.getMember().getId(),

                assignment.getMember()
                        .getMemberNumber(),

                memberName,

                assignment.getAssignedAt(),

                assignment.getEndedAt(),

                assignment.getStatus(),

                assignment.isPrimaryTrainer(),

                assignment.getNotes()
        );
    }
}