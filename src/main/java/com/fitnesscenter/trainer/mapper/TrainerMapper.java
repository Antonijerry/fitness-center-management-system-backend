package com.fitnesscenter.trainer.mapper;

import com.fitnesscenter.trainer.dto.TrainerResponse;
import com.fitnesscenter.trainer.entity.TrainerProfile;
import org.springframework.stereotype.Component;

@Component
public class TrainerMapper {

    public TrainerResponse toResponse(
            TrainerProfile trainer
    ) {

        return new TrainerResponse(
                trainer.getId(),
                trainer.getUser().getId(),
                trainer.getEmployeeNumber(),
                trainer.getUser().getFirstName(),
                trainer.getUser().getLastName(),
                trainer.getUser().getEmail(),
                trainer.getSpecialization(),
                trainer.getCertifications(),
                trainer.getYearsOfExperience(),
                trainer.getBio(),
                trainer.getHourlyRate(),
                trainer.getStatus()
        );
    }
}