package com.fitnesscenter.training.repository;

import com.fitnesscenter.training.entity.TrainingSession;
import com.fitnesscenter.training.entity.TrainingSessionStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface TrainingSessionRepository
        extends JpaRepository<TrainingSession, Long> {

    List<TrainingSession>
    findAllByTrainerIdOrderBySessionDateAscStartTimeAsc(
            Long trainerId
    );

    List<TrainingSession>
    findAllByMemberIdOrderBySessionDateAscStartTimeAsc(
            Long memberId
    );

    List<TrainingSession>
    findAllBySessionDateOrderByStartTimeAsc(
            LocalDate sessionDate
    );

    List<TrainingSession>
    findAllByTrainerIdAndSessionDateOrderByStartTimeAsc(
            Long trainerId,
            LocalDate sessionDate
    );

    List<TrainingSession>
    findAllByMemberIdAndSessionDateOrderByStartTimeAsc(
            Long memberId,
            LocalDate sessionDate
    );

    List<TrainingSession>
    findAllByStatusOrderBySessionDateAscStartTimeAsc(
            TrainingSessionStatus status
    );
}