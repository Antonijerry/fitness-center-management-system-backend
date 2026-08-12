package com.fitnesscenter.trainer.dto;

import com.fitnesscenter.trainer.entity.TrainerStatus;

import java.math.BigDecimal;

public record TrainerResponse(

        Long id,

        Long userId,

        String employeeNumber,

        String firstName,

        String lastName,

        String email,

        String specialization,

        String certifications,

        Integer yearsOfExperience,

        String bio,

        BigDecimal hourlyRate,

        TrainerStatus status
) {
}