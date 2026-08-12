package com.fitnesscenter.training.mapper;

import com.fitnesscenter.training.dto.TrainingSessionResponse;
import com.fitnesscenter.training.entity.TrainingSession;
import org.springframework.stereotype.Component;

@Component
public class TrainingSessionMapper {

    public TrainingSessionResponse toResponse(
            TrainingSession session
    ) {

        String trainerName =
                session.getTrainer()
                        .getUser()
                        .getFirstName()
                        + " "
                        + session.getTrainer()
                        .getUser()
                        .getLastName();

        String memberName =
                session.getMember()
                        .getUser()
                        .getFirstName()
                        + " "
                        + session.getMember()
                        .getUser()
                        .getLastName();

        return new TrainingSessionResponse(

                session.getId(),

                session.getTrainer().getId(),

                session.getTrainer()
                        .getEmployeeNumber(),

                trainerName,

                session.getMember().getId(),

                session.getMember()
                        .getMemberNumber(),

                memberName,

                session.getSessionDate(),

                session.getStartTime(),

                session.getEndTime(),

                session.getSessionType(),

                session.getStatus(),

                session.getLocation(),

                session.getNotes(),

                session.getCancellationReason(),

                session.getStartedAt(),

                session.getCompletedAt()
        );
    }
}