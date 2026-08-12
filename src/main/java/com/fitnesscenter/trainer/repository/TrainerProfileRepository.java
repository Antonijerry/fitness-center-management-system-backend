package com.fitnesscenter.trainer.repository;

import com.fitnesscenter.trainer.entity.TrainerProfile;
import com.fitnesscenter.trainer.entity.TrainerStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TrainerProfileRepository
        extends JpaRepository<TrainerProfile, Long> {

    Optional<TrainerProfile> findByUserId(
            Long userId
    );

    Optional<TrainerProfile> findByEmployeeNumber(
            String employeeNumber
    );

    boolean existsByUserId(
            Long userId
    );

    boolean existsByEmployeeNumber(
            String employeeNumber
    );

    List<TrainerProfile> findAllByStatus(
            TrainerStatus status
    );

    List<TrainerProfile>
    findBySpecializationContainingIgnoreCase(
            String specialization
    );
}