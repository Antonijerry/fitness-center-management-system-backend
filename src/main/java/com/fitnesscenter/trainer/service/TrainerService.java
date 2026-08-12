package com.fitnesscenter.trainer.service;

import com.fitnesscenter.trainer.dto.CreateTrainerRequest;
import com.fitnesscenter.trainer.dto.TrainerResponse;
import com.fitnesscenter.trainer.dto.UpdateTrainerRequest;
import com.fitnesscenter.trainer.entity.TrainerStatus;

import java.util.List;

public interface TrainerService {

    TrainerResponse create(
            CreateTrainerRequest request
    );

    TrainerResponse getById(
            Long id
    );

    TrainerResponse getByUserId(
            Long userId
    );

    TrainerResponse getByEmployeeNumber(
            String employeeNumber
    );

    List<TrainerResponse> getAll();

    List<TrainerResponse> getByStatus(
            TrainerStatus status
    );

    List<TrainerResponse> getBySpecialization(
            String specialization
    );

    TrainerResponse update(
            Long id,
            UpdateTrainerRequest request
    );

    TrainerResponse updateStatus(
            Long id,
            TrainerStatus status
    );
}