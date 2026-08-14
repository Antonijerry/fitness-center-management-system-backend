package com.fitnesscenter.workout.service;

import com.fitnesscenter.common.exception.ConflictException;
import com.fitnesscenter.common.exception.ResourceNotFoundException;
import com.fitnesscenter.member.entity.MemberProfile;
import com.fitnesscenter.member.repository.MemberProfileRepository;
import com.fitnesscenter.workout.dto.LogWorkoutExerciseRequest;
import com.fitnesscenter.workout.dto.LogWorkoutSetRequest;
import com.fitnesscenter.workout.dto.StartWorkoutSessionRequest;
import com.fitnesscenter.workout.dto.WorkoutExerciseLogResponse;
import com.fitnesscenter.workout.dto.WorkoutSessionResponse;
import com.fitnesscenter.workout.dto.WorkoutSetLogResponse;
import com.fitnesscenter.workout.entity.Exercise;
import com.fitnesscenter.workout.entity.WorkoutExerciseLog;
import com.fitnesscenter.workout.entity.WorkoutProgram;
import com.fitnesscenter.workout.entity.WorkoutProgramStatus;
import com.fitnesscenter.workout.entity.WorkoutSession;
import com.fitnesscenter.workout.entity.WorkoutSessionStatus;
import com.fitnesscenter.workout.entity.WorkoutSetLog;
import com.fitnesscenter.workout.mapper.WorkoutSessionMapper;
import com.fitnesscenter.workout.repository.ExerciseRepository;
import com.fitnesscenter.workout.repository.WorkoutExerciseLogRepository;
import com.fitnesscenter.workout.repository.WorkoutProgramRepository;
import com.fitnesscenter.workout.repository.WorkoutSessionRepository;
import com.fitnesscenter.workout.repository.WorkoutSetLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class WorkoutSessionServiceImpl
        implements WorkoutSessionService {

    private final WorkoutSessionRepository sessionRepository;

    private final WorkoutExerciseLogRepository exerciseLogRepository;

    private final WorkoutSetLogRepository setLogRepository;

    private final WorkoutProgramRepository programRepository;

    private final MemberProfileRepository memberRepository;

    private final ExerciseRepository exerciseRepository;

    private final WorkoutSessionMapper mapper;


    @Override
    @Transactional
    public WorkoutSessionResponse start(
            StartWorkoutSessionRequest request
    ) {

        WorkoutProgram program =
                programRepository.findById(
                        request.workoutProgramId()
                ).orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Workout program not found"
                        )
                );


        if (
                program.getStatus()
                        != WorkoutProgramStatus.ACTIVE
        ) {

            throw new ConflictException(
                    "Only an active workout program can be started"
            );
        }


        MemberProfile member =
                program.getMember();


        boolean activeSession =
                sessionRepository
                        .existsByMemberIdAndStatus(
                                member.getId(),
                                WorkoutSessionStatus.IN_PROGRESS
                        );


        if (activeSession) {

            throw new ConflictException(
                    "Member already has a workout session in progress"
            );
        }


        WorkoutSession session =
                new WorkoutSession();

        session.setMember(member);

        session.setWorkoutProgram(program);

        session.setStartedAt(
                LocalDateTime.now()
        );

        session.setStatus(
                WorkoutSessionStatus.IN_PROGRESS
        );

        session.setNotes(
                request.notes()
        );


        return toResponse(
                sessionRepository.save(session)
        );
    }


    @Override
    public WorkoutSessionResponse getById(
            Long id
    ) {

        return toResponse(
                getSession(id)
        );
    }


    @Override
    public List<WorkoutSessionResponse> getByMember(
            Long memberId
    ) {

        if (!memberRepository.existsById(memberId)) {

            throw new ResourceNotFoundException(
                    "Member not found"
            );
        }


        return sessionRepository
                .findAllByMemberIdOrderByStartedAtDesc(
                        memberId
                )
                .stream()
                .map(this::toResponse)
                .toList();
    }


    @Override
    @Transactional
    public WorkoutSessionResponse complete(
            Long id
    ) {

        WorkoutSession session =
                getSession(id);


        requireStatus(
                session,
                WorkoutSessionStatus.IN_PROGRESS
        );


        List<WorkoutExerciseLog> exerciseLogs =
                exerciseLogRepository
                        .findAllByWorkoutSessionIdOrderByExerciseOrderAsc(
                                session.getId()
                        );


        if (exerciseLogs.isEmpty()) {

            throw new ConflictException(
                    "A workout session must contain at least one exercise"
            );
        }


        session.setCompletedAt(
                LocalDateTime.now()
        );

        session.setStatus(
                WorkoutSessionStatus.COMPLETED
        );


        return toResponse(session);
    }


    @Override
    @Transactional
    public WorkoutSessionResponse cancel(
            Long id
    ) {

        WorkoutSession session =
                getSession(id);


        requireStatus(
                session,
                WorkoutSessionStatus.IN_PROGRESS
        );


        session.setCompletedAt(
                LocalDateTime.now()
        );

        session.setStatus(
                WorkoutSessionStatus.CANCELLED
        );


        return toResponse(session);
    }


    @Override
    @Transactional
    public WorkoutExerciseLogResponse logExercise(
            Long sessionId,
            LogWorkoutExerciseRequest request
    ) {

        WorkoutSession session =
                getSession(sessionId);


        requireStatus(
                session,
                WorkoutSessionStatus.IN_PROGRESS
        );


        Exercise exercise =
                exerciseRepository.findById(
                        request.exerciseId()
                ).orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Exercise not found"
                        )
                );


        boolean alreadyLogged =
                exerciseLogRepository
                        .findByWorkoutSessionIdAndExerciseId(
                                sessionId,
                                request.exerciseId()
                        )
                        .isPresent();


        if (alreadyLogged) {

            throw new ConflictException(
                    "This exercise has already been logged for this session"
            );
        }


        WorkoutExerciseLog exerciseLog =
                new WorkoutExerciseLog();

        exerciseLog.setWorkoutSession(session);

        exerciseLog.setExercise(exercise);

        exerciseLog.setExerciseOrder(
                request.exerciseOrder()
        );

        exerciseLog.setNotes(
                request.notes()
        );


        WorkoutExerciseLog saved =
                exerciseLogRepository.save(
                        exerciseLog
                );


        return mapper.toExerciseResponse(
                saved,
                List.of()
        );
    }


    @Override
    @Transactional
    public WorkoutSetLogResponse logSet(
            Long exerciseLogId,
            LogWorkoutSetRequest request
    ) {

        WorkoutExerciseLog exerciseLog =
                getExerciseLog(exerciseLogId);


        requireStatus(
                exerciseLog
                        .getWorkoutSession(),
                WorkoutSessionStatus.IN_PROGRESS
        );


        if (
                setLogRepository
                        .existsByWorkoutExerciseLogIdAndSetNumber(
                                exerciseLogId,
                                request.setNumber()
                        )
        ) {

            throw new ConflictException(
                    "This set number already exists"
            );
        }


        validateSetData(request);


        WorkoutSetLog set =
                new WorkoutSetLog();

        set.setWorkoutExerciseLog(
                exerciseLog
        );

        set.setSetNumber(
                request.setNumber()
        );

        set.setRepetitions(
                request.repetitions()
        );

        set.setWeight(
                request.weight()
        );

        set.setDurationSeconds(
                request.durationSeconds()
        );

        set.setDistance(
                request.distance()
        );

        set.setCompleted(
                request.completed()
        );

        set.setNotes(
                request.notes()
        );


        return mapper.toSetResponse(
                setLogRepository.save(set)
        );
    }


    @Override
    public List<WorkoutExerciseLogResponse> getExercises(
            Long sessionId
    ) {

        getSession(sessionId);


        return exerciseLogRepository
                .findAllByWorkoutSessionIdOrderByExerciseOrderAsc(
                        sessionId
                )
                .stream()
                .map(this::toExerciseResponse)
                .toList();
    }


    @Override
    public List<WorkoutSetLogResponse> getSets(
            Long exerciseLogId
    ) {

        getExerciseLog(exerciseLogId);


        return setLogRepository
                .findAllByWorkoutExerciseLogIdOrderBySetNumberAsc(
                        exerciseLogId
                )
                .stream()
                .map(mapper::toSetResponse)
                .toList();
    }


    private WorkoutSession getSession(
            Long id
    ) {

        return sessionRepository
                .findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Workout session not found"
                        )
                );
    }


    private WorkoutExerciseLog getExerciseLog(
            Long id
    ) {

        return exerciseLogRepository
                .findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Workout exercise log not found"
                        )
                );
    }


    private WorkoutSessionResponse toResponse(
            WorkoutSession session
    ) {

        List<WorkoutExerciseLogResponse> exercises =
                exerciseLogRepository
                        .findAllByWorkoutSessionIdOrderByExerciseOrderAsc(
                                session.getId()
                        )
                        .stream()
                        .map(this::toExerciseResponse)
                        .toList();


        return mapper.toSessionResponse(
                session,
                exercises
        );
    }


    private WorkoutExerciseLogResponse toExerciseResponse(
            WorkoutExerciseLog exerciseLog
    ) {

        List<WorkoutSetLogResponse> sets =
                setLogRepository
                        .findAllByWorkoutExerciseLogIdOrderBySetNumberAsc(
                                exerciseLog.getId()
                        )
                        .stream()
                        .map(mapper::toSetResponse)
                        .toList();


        return mapper.toExerciseResponse(
                exerciseLog,
                sets
        );
    }


    private void requireStatus(
            WorkoutSession session,
            WorkoutSessionStatus expected
    ) {

        if (session.getStatus() != expected) {

            throw new ConflictException(
                    "Workout session must be in "
                            + expected
                            + " status"
            );
        }
    }


    private void validateSetData(
            LogWorkoutSetRequest request
    ) {

        boolean hasReps =
                request.repetitions() != null;

        boolean hasDuration =
                request.durationSeconds() != null;

        boolean hasDistance =
                request.distance() != null;


        if (
                !hasReps
                        &&
                        !hasDuration
                        &&
                        !hasDistance
        ) {

            throw new ConflictException(
                    "A workout set must contain repetitions, duration, or distance"
            );
        }
    }
}