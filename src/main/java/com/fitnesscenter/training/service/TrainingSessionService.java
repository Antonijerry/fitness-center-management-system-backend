package com.fitnesscenter.training.service;

import com.fitnesscenter.training.dto.CreateTrainingSessionRequest;
import com.fitnesscenter.training.dto.TrainingSessionResponse;
import com.fitnesscenter.training.dto.UpdateTrainingSessionRequest;

import java.time.LocalDate;
import java.util.List;

public interface TrainingSessionService {

    TrainingSessionResponse create(
            CreateTrainingSessionRequest request
    );

    TrainingSessionResponse getById(
            Long id
    );

    List<TrainingSessionResponse> getAll();

    List<TrainingSessionResponse> getByTrainer(
            Long trainerId
    );

    List<TrainingSessionResponse> getByMember(
            Long memberId
    );

    List<TrainingSessionResponse> getByDate(
            LocalDate date
    );

    List<TrainingSessionResponse> getTrainerSchedule(
            Long trainerId,
            LocalDate date
    );

    List<TrainingSessionResponse> getMemberSchedule(
            Long memberId,
            LocalDate date
    );

    TrainingSessionResponse update(
            Long id,
            UpdateTrainingSessionRequest request
    );

    TrainingSessionResponse confirm(
            Long id
    );

    TrainingSessionResponse start(
            Long id
    );

    TrainingSessionResponse complete(
            Long id
    );

    TrainingSessionResponse cancel(
            Long id,
            String reason
    );
}