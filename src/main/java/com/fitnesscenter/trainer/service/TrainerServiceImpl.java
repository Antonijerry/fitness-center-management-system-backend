package com.fitnesscenter.trainer.service;

import com.fitnesscenter.common.exception.ConflictException;
import com.fitnesscenter.common.exception.ResourceNotFoundException;
import com.fitnesscenter.trainer.dto.CreateTrainerRequest;
import com.fitnesscenter.trainer.dto.TrainerResponse;
import com.fitnesscenter.trainer.dto.UpdateTrainerRequest;
import com.fitnesscenter.trainer.entity.TrainerProfile;
import com.fitnesscenter.trainer.entity.TrainerStatus;
import com.fitnesscenter.trainer.mapper.TrainerMapper;
import com.fitnesscenter.trainer.repository.TrainerProfileRepository;
import com.fitnesscenter.user.entity.User;
import com.fitnesscenter.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TrainerServiceImpl
        implements TrainerService {

    private final TrainerProfileRepository trainerRepository;

    private final UserRepository userRepository;

    private final TrainerMapper trainerMapper;


    @Override
    @Transactional
    public TrainerResponse create(
            CreateTrainerRequest request
    ) {

        User user =
                userRepository.findById(
                        request.userId()
                ).orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found"
                        )
                );

        if (
                trainerRepository.existsByUserId(
                        request.userId()
                )
        ) {

            throw new ConflictException(
                    "This user already has a trainer profile"
            );
        }

        TrainerProfile trainer =
                new TrainerProfile();

        trainer.setUser(user);

        trainer.setEmployeeNumber(
                generateEmployeeNumber()
        );

        trainer.setSpecialization(
                request.specialization().trim()
        );

        trainer.setCertifications(
                request.certifications()
        );

        trainer.setYearsOfExperience(
                request.yearsOfExperience()
        );

        trainer.setBio(
                request.bio()
        );

        trainer.setHourlyRate(
                request.hourlyRate()
        );

        trainer.setStatus(
                TrainerStatus.ACTIVE
        );

        return trainerMapper.toResponse(
                trainerRepository.save(trainer)
        );
    }


    @Override
    public TrainerResponse getById(
            Long id
    ) {

        return trainerMapper.toResponse(
                getTrainer(id)
        );
    }


    @Override
    public TrainerResponse getByUserId(
            Long userId
    ) {

        TrainerProfile trainer =
                trainerRepository.findByUserId(
                        userId
                ).orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Trainer profile not found"
                        )
                );

        return trainerMapper.toResponse(trainer);
    }


    @Override
    public TrainerResponse getByEmployeeNumber(
            String employeeNumber
    ) {

        TrainerProfile trainer =
                trainerRepository
                        .findByEmployeeNumber(
                                employeeNumber
                        )
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Trainer profile not found"
                                )
                        );

        return trainerMapper.toResponse(trainer);
    }


    @Override
    public List<TrainerResponse> getAll() {

        return trainerRepository.findAll()
                .stream()
                .map(trainerMapper::toResponse)
                .toList();
    }


    @Override
    public List<TrainerResponse> getByStatus(
            TrainerStatus status
    ) {

        return trainerRepository
                .findAllByStatus(status)
                .stream()
                .map(trainerMapper::toResponse)
                .toList();
    }


    @Override
    public List<TrainerResponse> getBySpecialization(
            String specialization
    ) {

        return trainerRepository
                .findBySpecializationContainingIgnoreCase(
                        specialization.trim()
                )
                .stream()
                .map(trainerMapper::toResponse)
                .toList();
    }


    @Override
    @Transactional
    public TrainerResponse update(
            Long id,
            UpdateTrainerRequest request
    ) {

        TrainerProfile trainer =
                getTrainer(id);

        trainer.setSpecialization(
                request.specialization().trim()
        );

        trainer.setCertifications(
                request.certifications()
        );

        trainer.setYearsOfExperience(
                request.yearsOfExperience()
        );

        trainer.setBio(
                request.bio()
        );

        trainer.setHourlyRate(
                request.hourlyRate()
        );

        return trainerMapper.toResponse(trainer);
    }


    @Override
    @Transactional
    public TrainerResponse updateStatus(
            Long id,
            TrainerStatus status
    ) {

        TrainerProfile trainer =
                getTrainer(id);

        trainer.setStatus(status);

        return trainerMapper.toResponse(trainer);
    }


    private TrainerProfile getTrainer(
            Long id
    ) {

        return trainerRepository
                .findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Trainer profile not found"
                        )
                );
    }


    private String generateEmployeeNumber() {

        String employeeNumber;

        do {

            employeeNumber =
                    "TRN-"
                            + System.currentTimeMillis();

        } while (
                trainerRepository
                        .existsByEmployeeNumber(
                                employeeNumber
                        )
        );

        return employeeNumber;
    }
}