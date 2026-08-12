package com.fitnesscenter.training.service;

import com.fitnesscenter.assignment.entity.AssignmentStatus;
import com.fitnesscenter.assignment.repository.TrainerMemberAssignmentRepository;
import com.fitnesscenter.common.exception.ConflictException;
import com.fitnesscenter.common.exception.ResourceNotFoundException;
import com.fitnesscenter.member.entity.MemberProfile;
import com.fitnesscenter.member.repository.MemberProfileRepository;
import com.fitnesscenter.trainer.entity.TrainerProfile;
import com.fitnesscenter.trainer.entity.TrainerStatus;
import com.fitnesscenter.trainer.repository.TrainerProfileRepository;
import com.fitnesscenter.training.dto.CreateTrainingSessionRequest;
import com.fitnesscenter.training.dto.TrainingSessionResponse;
import com.fitnesscenter.training.dto.UpdateTrainingSessionRequest;
import com.fitnesscenter.training.entity.TrainingSession;
import com.fitnesscenter.training.entity.TrainingSessionStatus;
import com.fitnesscenter.training.mapper.TrainingSessionMapper;
import com.fitnesscenter.training.repository.TrainingSessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TrainingSessionServiceImpl
        implements TrainingSessionService {

    private final TrainingSessionRepository sessionRepository;

    private final TrainerProfileRepository trainerRepository;

    private final MemberProfileRepository memberRepository;

    private final TrainerMemberAssignmentRepository assignmentRepository;

    private final TrainingSessionMapper sessionMapper;


    @Override
    @Transactional
    public TrainingSessionResponse create(
            CreateTrainingSessionRequest request
    ) {

        validateTimeRange(
                request.startTime(),
                request.endTime()
        );


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
                    "Only an active trainer can conduct training sessions"
            );
        }


        boolean assigned =
                assignmentRepository
                        .existsByTrainerIdAndMemberIdAndStatus(
                                trainer.getId(),
                                member.getId(),
                                AssignmentStatus.ACTIVE
                        );


        if (!assigned) {

            throw new ConflictException(
                    "Trainer is not actively assigned to this member"
            );
        }


        validateTrainerAvailability(
                trainer.getId(),
                request.sessionDate(),
                request.startTime(),
                request.endTime()
        );


        validateMemberAvailability(
                member.getId(),
                request.sessionDate(),
                request.startTime(),
                request.endTime()
        );


        TrainingSession session =
                new TrainingSession();

        session.setTrainer(trainer);

        session.setMember(member);

        session.setSessionDate(
                request.sessionDate()
        );

        session.setStartTime(
                request.startTime()
        );

        session.setEndTime(
                request.endTime()
        );

        session.setSessionType(
                request.sessionType()
        );

        session.setStatus(
                TrainingSessionStatus.SCHEDULED
        );

        session.setLocation(
                request.location()
        );

        session.setNotes(
                request.notes()
        );


        return sessionMapper.toResponse(
                sessionRepository.save(session)
        );
    }


    @Override
    public TrainingSessionResponse getById(
            Long id
    ) {

        return sessionMapper.toResponse(
                getSession(id)
        );
    }


    @Override
    public List<TrainingSessionResponse> getAll() {

        return sessionRepository.findAll()
                .stream()
                .map(sessionMapper::toResponse)
                .toList();
    }


    @Override
    public List<TrainingSessionResponse> getByTrainer(
            Long trainerId
    ) {

        return sessionRepository
                .findAllByTrainerIdOrderBySessionDateAscStartTimeAsc(
                        trainerId
                )
                .stream()
                .map(sessionMapper::toResponse)
                .toList();
    }


    @Override
    public List<TrainingSessionResponse> getByMember(
            Long memberId
    ) {

        return sessionRepository
                .findAllByMemberIdOrderBySessionDateAscStartTimeAsc(
                        memberId
                )
                .stream()
                .map(sessionMapper::toResponse)
                .toList();
    }


    @Override
    public List<TrainingSessionResponse> getByDate(
            LocalDate date
    ) {

        return sessionRepository
                .findAllBySessionDateOrderByStartTimeAsc(
                        date
                )
                .stream()
                .map(sessionMapper::toResponse)
                .toList();
    }


    @Override
    public List<TrainingSessionResponse> getTrainerSchedule(
            Long trainerId,
            LocalDate date
    ) {

        return sessionRepository
                .findAllByTrainerIdAndSessionDateOrderByStartTimeAsc(
                        trainerId,
                        date
                )
                .stream()
                .map(sessionMapper::toResponse)
                .toList();
    }


    @Override
    public List<TrainingSessionResponse> getMemberSchedule(
            Long memberId,
            LocalDate date
    ) {

        return sessionRepository
                .findAllByMemberIdAndSessionDateOrderByStartTimeAsc(
                        memberId,
                        date
                )
                .stream()
                .map(sessionMapper::toResponse)
                .toList();
    }


    @Override
    @Transactional
    public TrainingSessionResponse update(
            Long id,
            UpdateTrainingSessionRequest request
    ) {

        TrainingSession session =
                getSession(id);


        if (
                session.getStatus()
                        != TrainingSessionStatus.SCHEDULED
                        &&
                        session.getStatus()
                                != TrainingSessionStatus.CONFIRMED
        ) {

            throw new ConflictException(
                    "Only scheduled or confirmed sessions can be updated"
            );
        }


        if (
                request.startTime() != null
                        &&
                        request.endTime() != null
        ) {

            validateTimeRange(
                    request.startTime(),
                    request.endTime()
            );
        }


        LocalDate date =
                request.sessionDate() != null
                        ? request.sessionDate()
                        : session.getSessionDate();

        var start =
                request.startTime() != null
                        ? request.startTime()
                        : session.getStartTime();

        var end =
                request.endTime() != null
                        ? request.endTime()
                        : session.getEndTime();


        validateTrainerAvailabilityExcludingSession(
                session.getTrainer().getId(),
                date,
                start,
                end,
                session.getId()
        );


        validateMemberAvailabilityExcludingSession(
                session.getMember().getId(),
                date,
                start,
                end,
                session.getId()
        );


        if (request.sessionDate() != null) {
            session.setSessionDate(
                    request.sessionDate()
            );
        }

        if (request.startTime() != null) {
            session.setStartTime(
                    request.startTime()
            );
        }

        if (request.endTime() != null) {
            session.setEndTime(
                    request.endTime()
            );
        }

        if (request.sessionType() != null) {
            session.setSessionType(
                    request.sessionType()
            );
        }

        if (request.location() != null) {
            session.setLocation(
                    request.location()
            );
        }

        if (request.notes() != null) {
            session.setNotes(
                    request.notes()
            );
        }


        return sessionMapper.toResponse(
                session
        );
    }


    @Override
    @Transactional
    public TrainingSessionResponse confirm(
            Long id
    ) {

        TrainingSession session =
                getSession(id);


        requireStatus(
                session,
                TrainingSessionStatus.SCHEDULED
        );


        session.setStatus(
                TrainingSessionStatus.CONFIRMED
        );


        return sessionMapper.toResponse(
                session
        );
    }


    @Override
    @Transactional
    public TrainingSessionResponse start(
            Long id
    ) {

        TrainingSession session =
                getSession(id);


        if (
                session.getStatus()
                        != TrainingSessionStatus.CONFIRMED
                        &&
                        session.getStatus()
                                != TrainingSessionStatus.SCHEDULED
        ) {

            throw new ConflictException(
                    "Only scheduled or confirmed sessions can be started"
            );
        }


        session.setStatus(
                TrainingSessionStatus.IN_PROGRESS
        );

        session.setStartedAt(
                LocalDateTime.now()
        );


        return sessionMapper.toResponse(
                session
        );
    }


    @Override
    @Transactional
    public TrainingSessionResponse complete(
            Long id
    ) {

        TrainingSession session =
                getSession(id);


        requireStatus(
                session,
                TrainingSessionStatus.IN_PROGRESS
        );


        session.setStatus(
                TrainingSessionStatus.COMPLETED
        );

        session.setCompletedAt(
                LocalDateTime.now()
        );


        return sessionMapper.toResponse(
                session
        );
    }


    @Override
    @Transactional
    public TrainingSessionResponse cancel(
            Long id,
            String reason
    ) {

        TrainingSession session =
                getSession(id);


        if (
                session.getStatus()
                        == TrainingSessionStatus.COMPLETED
                        ||
                        session.getStatus()
                                == TrainingSessionStatus.CANCELLED
        ) {

            throw new ConflictException(
                    "Completed or already cancelled sessions cannot be cancelled"
            );
        }


        session.setStatus(
                TrainingSessionStatus.CANCELLED
        );

        session.setCancellationReason(
                reason
        );


        return sessionMapper.toResponse(
                session
        );
    }


    private TrainingSession getSession(
            Long id
    ) {

        return sessionRepository
                .findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Training session not found"
                        )
                );
    }


    private void requireStatus(
            TrainingSession session,
            TrainingSessionStatus expected
    ) {

        if (session.getStatus() != expected) {

            throw new ConflictException(
                    "Invalid session status transition"
            );
        }
    }


    private void validateTimeRange(
            java.time.LocalTime start,
            java.time.LocalTime end
    ) {

        if (
                !start.isBefore(end)
        ) {

            throw new ConflictException(
                    "Session start time must be before end time"
            );
        }
    }


    private void validateTrainerAvailability(
            Long trainerId,
            LocalDate date,
            java.time.LocalTime start,
            java.time.LocalTime end
    ) {

        List<TrainingSession> sessions =
                sessionRepository
                        .findAllByTrainerIdAndSessionDateOrderByStartTimeAsc(
                                trainerId,
                                date
                        );


        boolean conflict =
                sessions.stream()
                        .filter(session ->
                                session.getStatus()
                                        != TrainingSessionStatus.CANCELLED
                        )
                        .anyMatch(session ->
                                overlaps(
                                        start,
                                        end,
                                        session.getStartTime(),
                                        session.getEndTime()
                                )
                        );


        if (conflict) {

            throw new ConflictException(
                    "Trainer already has a session during this time"
            );
        }
    }


    private void validateMemberAvailability(
            Long memberId,
            LocalDate date,
            java.time.LocalTime start,
            java.time.LocalTime end
    ) {

        List<TrainingSession> sessions =
                sessionRepository
                        .findAllByMemberIdAndSessionDateOrderByStartTimeAsc(
                                memberId,
                                date
                        );


        boolean conflict =
                sessions.stream()
                        .filter(session ->
                                session.getStatus()
                                        != TrainingSessionStatus.CANCELLED
                        )
                        .anyMatch(session ->
                                overlaps(
                                        start,
                                        end,
                                        session.getStartTime(),
                                        session.getEndTime()
                                )
                        );


        if (conflict) {

            throw new ConflictException(
                    "Member already has a session during this time"
            );
        }
    }


    private boolean overlaps(
            java.time.LocalTime newStart,
            java.time.LocalTime newEnd,
            java.time.LocalTime existingStart,
            java.time.LocalTime existingEnd
    ) {

        return newStart.isBefore(existingEnd)
                && newEnd.isAfter(existingStart);
    }


    private void validateTrainerAvailabilityExcludingSession(
            Long trainerId,
            LocalDate date,
            java.time.LocalTime start,
            java.time.LocalTime end,
            Long excludedSessionId
    ) {

        List<TrainingSession> sessions =
                sessionRepository
                        .findAllByTrainerIdAndSessionDateOrderByStartTimeAsc(
                                trainerId,
                                date
                        );


        boolean conflict =
                sessions.stream()
                        .filter(session ->
                                !session.getId()
                                        .equals(excludedSessionId)
                        )
                        .filter(session ->
                                session.getStatus()
                                        != TrainingSessionStatus.CANCELLED
                        )
                        .anyMatch(session ->
                                overlaps(
                                        start,
                                        end,
                                        session.getStartTime(),
                                        session.getEndTime()
                                )
                        );


        if (conflict) {

            throw new ConflictException(
                    "Trainer already has a session during this time"
            );
        }
    }


    private void validateMemberAvailabilityExcludingSession(
            Long memberId,
            LocalDate date,
            java.time.LocalTime start,
            java.time.LocalTime end,
            Long excludedSessionId
    ) {

        List<TrainingSession> sessions =
                sessionRepository
                        .findAllByMemberIdAndSessionDateOrderByStartTimeAsc(
                                memberId,
                                date
                        );


        boolean conflict =
                sessions.stream()
                        .filter(session ->
                                !session.getId()
                                        .equals(excludedSessionId)
                        )
                        .filter(session ->
                                session.getStatus()
                                        != TrainingSessionStatus.CANCELLED
                        )
                        .anyMatch(session ->
                                overlaps(
                                        start,
                                        end,
                                        session.getStartTime(),
                                        session.getEndTime()
                                )
                        );


        if (conflict) {

            throw new ConflictException(
                    "Member already has a session during this time"
            );
        }
    }
}