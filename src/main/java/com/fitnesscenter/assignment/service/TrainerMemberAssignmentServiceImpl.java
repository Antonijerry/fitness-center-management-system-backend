package com.fitnesscenter.assignment.service;

import com.fitnesscenter.assignment.dto.AssignmentResponse;
import com.fitnesscenter.assignment.dto.CreateAssignmentRequest;
import com.fitnesscenter.assignment.dto.UpdateAssignmentRequest;
import com.fitnesscenter.assignment.entity.AssignmentStatus;
import com.fitnesscenter.assignment.entity.TrainerMemberAssignment;
import com.fitnesscenter.assignment.mapper.AssignmentMapper;
import com.fitnesscenter.assignment.repository.TrainerMemberAssignmentRepository;
import com.fitnesscenter.common.exception.ConflictException;
import com.fitnesscenter.common.exception.ResourceNotFoundException;
import com.fitnesscenter.member.entity.MemberProfile;
import com.fitnesscenter.member.repository.MemberProfileRepository;
import com.fitnesscenter.trainer.entity.TrainerProfile;
import com.fitnesscenter.trainer.entity.TrainerStatus;
import com.fitnesscenter.trainer.repository.TrainerProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TrainerMemberAssignmentServiceImpl
        implements TrainerMemberAssignmentService {

    private final TrainerMemberAssignmentRepository assignmentRepository;

    private final TrainerProfileRepository trainerRepository;

    private final MemberProfileRepository memberRepository;

    private final AssignmentMapper assignmentMapper;


    @Override
    @Transactional
    public AssignmentResponse create(
            CreateAssignmentRequest request
    ) {

        TrainerProfile trainer =
                trainerRepository.findById(
                        request.trainerId()
                ).orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Trainer not found"
                        )
                );

        MemberProfile member =
                memberRepository.findById(
                        request.memberId()
                ).orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Member not found"
                        )
                );


        if (
                trainer.getStatus()
                        != TrainerStatus.ACTIVE
        ) {

            throw new ConflictException(
                    "Only an active trainer can be assigned to a member"
            );
        }


        if (
                assignmentRepository
                        .existsByTrainerIdAndMemberIdAndStatus(
                                trainer.getId(),
                                member.getId(),
                                AssignmentStatus.ACTIVE
                        )
        ) {

            throw new ConflictException(
                    "This trainer is already actively assigned to this member"
            );
        }


        TrainerMemberAssignment assignment =
                new TrainerMemberAssignment();

        assignment.setTrainer(trainer);

        assignment.setMember(member);

        assignment.setAssignedAt(
                LocalDateTime.now()
        );

        assignment.setStatus(
                AssignmentStatus.ACTIVE
        );

        assignment.setPrimaryTrainer(
                request.primaryTrainer()
        );

        assignment.setNotes(
                request.notes()
        );


        if (request.primaryTrainer()) {

            removeExistingPrimaryTrainer(
                    member.getId()
            );
        }


        return assignmentMapper.toResponse(
                assignmentRepository.save(assignment)
        );
    }


    @Override
    public AssignmentResponse getById(
            Long id
    ) {

        return assignmentMapper.toResponse(
                getAssignment(id)
        );
    }


    @Override
    public List<AssignmentResponse> getAll() {

        return assignmentRepository.findAll()
                .stream()
                .map(assignmentMapper::toResponse)
                .toList();
    }


    @Override
    public List<AssignmentResponse> getByTrainer(
            Long trainerId
    ) {

        return assignmentRepository
                .findAllByTrainerId(trainerId)
                .stream()
                .map(assignmentMapper::toResponse)
                .toList();
    }


    @Override
    public List<AssignmentResponse> getByMember(
            Long memberId
    ) {

        return assignmentRepository
                .findAllByMemberId(memberId)
                .stream()
                .map(assignmentMapper::toResponse)
                .toList();
    }


    @Override
    public List<AssignmentResponse> getActiveByTrainer(
            Long trainerId
    ) {

        return assignmentRepository
                .findAllByTrainerIdAndStatus(
                        trainerId,
                        AssignmentStatus.ACTIVE
                )
                .stream()
                .map(assignmentMapper::toResponse)
                .toList();
    }


    @Override
    public List<AssignmentResponse> getActiveByMember(
            Long memberId
    ) {

        return assignmentRepository
                .findAllByMemberIdAndStatus(
                        memberId,
                        AssignmentStatus.ACTIVE
                )
                .stream()
                .map(assignmentMapper::toResponse)
                .toList();
    }


    @Override
    @Transactional
    public AssignmentResponse update(
            Long id,
            UpdateAssignmentRequest request
    ) {

        TrainerMemberAssignment assignment =
                getAssignment(id);


        if (
                assignment.getStatus()
                        != AssignmentStatus.ACTIVE
        ) {

            throw new ConflictException(
                    "Only an active assignment can be updated"
            );
        }


        if (request.primaryTrainer()) {

            removeExistingPrimaryTrainer(
                    assignment.getMember().getId()
            );
        }


        assignment.setPrimaryTrainer(
                request.primaryTrainer()
        );

        assignment.setNotes(
                request.notes()
        );


        return assignmentMapper.toResponse(
                assignment
        );
    }


    @Override
    @Transactional
    public AssignmentResponse endAssignment(
            Long id
    ) {

        TrainerMemberAssignment assignment =
                getAssignment(id);


        if (
                assignment.getStatus()
                        == AssignmentStatus.ENDED
        ) {

            throw new ConflictException(
                    "Assignment has already ended"
            );
        }


        assignment.setStatus(
                AssignmentStatus.ENDED
        );

        assignment.setEndedAt(
                LocalDateTime.now()
        );

        assignment.setPrimaryTrainer(
                false
        );


        return assignmentMapper.toResponse(
                assignment
        );
    }


    private TrainerMemberAssignment getAssignment(
            Long id
    ) {

        return assignmentRepository
                .findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Trainer-member assignment not found"
                        )
                );
    }


    private void removeExistingPrimaryTrainer(
            Long memberId
    ) {

        List<TrainerMemberAssignment> activeAssignments =
                assignmentRepository
                        .findAllByMemberIdAndStatus(
                                memberId,
                                AssignmentStatus.ACTIVE
                        );

        activeAssignments.forEach(
                assignment ->
                        assignment.setPrimaryTrainer(false)
        );
    }
}