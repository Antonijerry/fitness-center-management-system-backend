package com.fitnesscenter.workout.service;

import com.fitnesscenter.assignment.entity.AssignmentStatus;
import com.fitnesscenter.assignment.repository.TrainerMemberAssignmentRepository;
import com.fitnesscenter.common.exception.ConflictException;
import com.fitnesscenter.common.exception.ResourceNotFoundException;
import com.fitnesscenter.member.entity.MemberProfile;
import com.fitnesscenter.member.repository.MemberProfileRepository;
import com.fitnesscenter.trainer.entity.TrainerProfile;
import com.fitnesscenter.trainer.entity.TrainerStatus;
import com.fitnesscenter.trainer.repository.TrainerProfileRepository;
import com.fitnesscenter.workout.dto.AddWorkoutExerciseRequest;
import com.fitnesscenter.workout.dto.CreateWorkoutProgramDayRequest;
import com.fitnesscenter.workout.dto.CreateWorkoutProgramRequest;
import com.fitnesscenter.workout.dto.UpdateWorkoutProgramRequest;
import com.fitnesscenter.workout.dto.WorkoutExerciseResponse;
import com.fitnesscenter.workout.dto.WorkoutProgramDayResponse;
import com.fitnesscenter.workout.dto.WorkoutProgramResponse;
import com.fitnesscenter.workout.entity.Exercise;
import com.fitnesscenter.workout.entity.WorkoutExercise;
import com.fitnesscenter.workout.entity.WorkoutExerciseType;
import com.fitnesscenter.workout.entity.WorkoutProgram;
import com.fitnesscenter.workout.entity.WorkoutProgramDay;
import com.fitnesscenter.workout.entity.WorkoutProgramStatus;
import com.fitnesscenter.workout.mapper.WorkoutProgramMapper;
import com.fitnesscenter.workout.repository.ExerciseRepository;
import com.fitnesscenter.workout.repository.WorkoutExerciseRepository;
import com.fitnesscenter.workout.repository.WorkoutProgramDayRepository;
import com.fitnesscenter.workout.repository.WorkoutProgramRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class WorkoutProgramServiceImpl
        implements WorkoutProgramService {

    private final WorkoutProgramRepository programRepository;

    private final WorkoutProgramDayRepository dayRepository;

    private final WorkoutExerciseRepository workoutExerciseRepository;

    private final ExerciseRepository exerciseRepository;

    private final TrainerProfileRepository trainerRepository;

    private final MemberProfileRepository memberRepository;

    private final TrainerMemberAssignmentRepository assignmentRepository;

    private final WorkoutProgramMapper mapper;


    @Override
    @Transactional
    public WorkoutProgramResponse create(
            CreateWorkoutProgramRequest request
    ) {

        validateDates(
                request.startDate(),
                request.endDate()
        );


        MemberProfile member =
                memberRepository.findById(
                        request.memberId()
                ).orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Member not found"
                        )
                );


        TrainerProfile trainer =
                trainerRepository.findById(
                        request.trainerId()
                ).orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Trainer not found"
                        )
                );


        if (
                trainer.getStatus()
                        != TrainerStatus.ACTIVE
        ) {

            throw new ConflictException(
                    "Only an active trainer can create a workout program"
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


        WorkoutProgram program =
                new WorkoutProgram();

        program.setMember(member);

        program.setTrainer(trainer);

        program.setName(
                request.name().trim()
        );

        program.setDescription(
                request.description()
        );

        program.setGoal(
                request.goal()
        );

        program.setStartDate(
                request.startDate()
        );

        program.setEndDate(
                request.endDate()
        );

        program.setStatus(
                WorkoutProgramStatus.DRAFT
        );


        return toResponse(
                programRepository.save(program)
        );
    }


    @Override
    public WorkoutProgramResponse getById(
            Long id
    ) {

        return toResponse(
                getProgram(id)
        );
    }


    @Override
    public List<WorkoutProgramResponse> getAll() {

        return programRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }


    @Override
    public List<WorkoutProgramResponse> getByMember(
            Long memberId
    ) {

        return programRepository
                .findAllByMemberIdOrderByStartDateDesc(
                        memberId
                )
                .stream()
                .map(this::toResponse)
                .toList();
    }


    @Override
    public List<WorkoutProgramResponse> getByTrainer(
            Long trainerId
    ) {

        return programRepository
                .findAllByTrainerIdOrderByStartDateDesc(
                        trainerId
                )
                .stream()
                .map(this::toResponse)
                .toList();
    }


    @Override
    @Transactional
    public WorkoutProgramResponse update(
            Long id,
            UpdateWorkoutProgramRequest request
    ) {

        WorkoutProgram program =
                getProgram(id);


        if (
                program.getStatus()
                        != WorkoutProgramStatus.DRAFT
        ) {

            throw new ConflictException(
                    "Only draft workout programs can be updated"
            );
        }


        LocalDate startDate =
                request.startDate() != null
                        ? request.startDate()
                        : program.getStartDate();

        LocalDate endDate =
                request.endDate() != null
                        ? request.endDate()
                        : program.getEndDate();


        validateDates(
                startDate,
                endDate
        );


        if (request.name() != null) {

            program.setName(
                    request.name().trim()
            );
        }

        if (request.description() != null) {

            program.setDescription(
                    request.description()
            );
        }

        if (request.goal() != null) {

            program.setGoal(
                    request.goal()
            );
        }

        if (request.startDate() != null) {

            program.setStartDate(
                    request.startDate()
            );
        }

        if (request.endDate() != null) {

            program.setEndDate(
                    request.endDate()
            );
        }


        return toResponse(program);
    }


    @Override
    @Transactional
    public WorkoutProgramResponse activate(
            Long id
    ) {

        WorkoutProgram program =
                getProgram(id);


        requireStatus(
                program,
                WorkoutProgramStatus.DRAFT
        );


        List<WorkoutProgramDay> days =
                dayRepository
                        .findAllByWorkoutProgramIdOrderByDayNumberAsc(
                                program.getId()
                        );


        if (days.isEmpty()) {

            throw new ConflictException(
                    "A workout program must contain at least one day before activation"
            );
        }


        for (WorkoutProgramDay day : days) {

            List<WorkoutExercise> exercises =
                    workoutExerciseRepository
                            .findAllByProgramDayIdOrderByExerciseOrderAsc(
                                    day.getId()
                            );

            if (exercises.isEmpty()) {

                throw new ConflictException(
                        "Every workout day must contain at least one exercise"
                );
            }
        }


        program.setStatus(
                WorkoutProgramStatus.ACTIVE
        );


        return toResponse(program);
    }


    @Override
    @Transactional
    public WorkoutProgramResponse complete(
            Long id
    ) {

        WorkoutProgram program =
                getProgram(id);


        requireStatus(
                program,
                WorkoutProgramStatus.ACTIVE
        );


        program.setStatus(
                WorkoutProgramStatus.COMPLETED
        );


        return toResponse(program);
    }


    @Override
    @Transactional
    public WorkoutProgramResponse cancel(
            Long id
    ) {

        WorkoutProgram program =
                getProgram(id);


        if (
                program.getStatus()
                        == WorkoutProgramStatus.COMPLETED
        ) {

            throw new ConflictException(
                    "A completed workout program cannot be cancelled"
            );
        }


        if (
                program.getStatus()
                        == WorkoutProgramStatus.CANCELLED
        ) {

            throw new ConflictException(
                    "Workout program is already cancelled"
            );
        }


        program.setStatus(
                WorkoutProgramStatus.CANCELLED
        );


        return toResponse(program);
    }


    @Override
    @Transactional
    public WorkoutProgramDayResponse addDay(
            Long programId,
            CreateWorkoutProgramDayRequest request
    ) {

        WorkoutProgram program =
                getProgram(programId);


        requireStatus(
                program,
                WorkoutProgramStatus.DRAFT
        );


        if (
                dayRepository
                        .existsByWorkoutProgramIdAndDayNumber(
                                programId,
                                request.dayNumber()
                        )
        ) {

            throw new ConflictException(
                    "This day number already exists in the workout program"
            );
        }


        WorkoutProgramDay day =
                new WorkoutProgramDay();

        day.setWorkoutProgram(program);

        day.setDayNumber(
                request.dayNumber()
        );

        day.setName(
                request.name().trim()
        );

        day.setNotes(
                request.notes()
        );


        WorkoutProgramDay saved =
                dayRepository.save(day);


        return mapper.toDayResponse(
                saved,
                List.of()
        );
    }


    @Override
    public List<WorkoutProgramDayResponse> getDays(
            Long programId
    ) {

        getProgram(programId);


        return dayRepository
                .findAllByWorkoutProgramIdOrderByDayNumberAsc(
                        programId
                )
                .stream()
                .map(this::toDayResponse)
                .toList();
    }


    @Override
    @Transactional
    public WorkoutExerciseResponse addExercise(
            Long dayId,
            AddWorkoutExerciseRequest request
    ) {

        WorkoutProgramDay day =
                getDay(dayId);


        requireStatus(
                day.getWorkoutProgram(),
                WorkoutProgramStatus.DRAFT
        );


        if (
                workoutExerciseRepository
                        .existsByProgramDayIdAndExerciseOrder(
                                dayId,
                                request.exerciseOrder()
                        )
        ) {

            throw new ConflictException(
                    "This exercise order already exists for this workout day"
            );
        }


        Exercise exercise =
                exerciseRepository.findById(
                        request.exerciseId()
                ).orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Exercise not found"
                        )
                );


        if (!exercise.isActive()) {

            throw new ConflictException(
                    "Cannot add an inactive exercise to a workout program"
            );
        }


        validateExerciseParameters(
                request
        );


        WorkoutExercise workoutExercise =
                new WorkoutExercise();

        workoutExercise.setProgramDay(day);

        workoutExercise.setExercise(exercise);

        workoutExercise.setExerciseOrder(
                request.exerciseOrder()
        );

        workoutExercise.setExerciseType(
                request.exerciseType()
        );

        workoutExercise.setSets(
                request.sets()
        );

        workoutExercise.setRepetitions(
                request.repetitions()
        );

        workoutExercise.setWeight(
                request.weight() != null
                        ? BigDecimal.valueOf(request.weight())
                        : null
        );
        workoutExercise.setDurationSeconds(
                request.durationSeconds()
        );

        workoutExercise.setDistance(
                request.distance() != null
                        ? BigDecimal.valueOf(request.distance())
                        : null
        );

        workoutExercise.setRestSeconds(
                request.restSeconds()
        );

        workoutExercise.setInstructions(
                request.instructions()
        );


        return mapper.toExerciseResponse(
                workoutExerciseRepository.save(
                        workoutExercise
                )
        );
    }


    @Override
    public List<WorkoutExerciseResponse> getExercises(
            Long dayId
    ) {

        getDay(dayId);


        return workoutExerciseRepository
                .findAllByProgramDayIdOrderByExerciseOrderAsc(
                        dayId
                )
                .stream()
                .map(mapper::toExerciseResponse)
                .toList();
    }


    @Override
    @Transactional
    public void removeExercise(
            Long exerciseId
    ) {

        WorkoutExercise workoutExercise =
                workoutExerciseRepository.findById(
                        exerciseId
                ).orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Workout exercise not found"
                        )
                );


        requireStatus(
                workoutExercise
                        .getProgramDay()
                        .getWorkoutProgram(),
                WorkoutProgramStatus.DRAFT
        );


        workoutExerciseRepository.delete(
                workoutExercise
        );
    }


    private WorkoutProgram getProgram(
            Long id
    ) {

        return programRepository
                .findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Workout program not found"
                        )
                );
    }


    private WorkoutProgramDay getDay(
            Long id
    ) {

        return dayRepository
                .findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Workout program day not found"
                        )
                );
    }


    private WorkoutProgramResponse toResponse(
            WorkoutProgram program
    ) {

        List<WorkoutProgramDayResponse> days =
                dayRepository
                        .findAllByWorkoutProgramIdOrderByDayNumberAsc(
                                program.getId()
                        )
                        .stream()
                        .map(this::toDayResponse)
                        .toList();


        return mapper.toProgramResponse(
                program,
                days
        );
    }


    private WorkoutProgramDayResponse toDayResponse(
            WorkoutProgramDay day
    ) {

        List<WorkoutExerciseResponse> exercises =
                workoutExerciseRepository
                        .findAllByProgramDayIdOrderByExerciseOrderAsc(
                                day.getId()
                        )
                        .stream()
                        .map(mapper::toExerciseResponse)
                        .toList();


        return mapper.toDayResponse(
                day,
                exercises
        );
    }


    private void requireStatus(
            WorkoutProgram program,
            WorkoutProgramStatus expected
    ) {

        if (program.getStatus() != expected) {

            throw new ConflictException(
                    "Workout program must be in "
                            + expected
                            + " status for this operation"
            );
        }
    }


    private void validateDates(
            LocalDate startDate,
            LocalDate endDate
    ) {

        if (
                endDate != null
                        &&
                        endDate.isBefore(startDate)
        ) {

            throw new ConflictException(
                    "Workout program end date cannot be before start date"
            );
        }
    }


    private void validateExerciseParameters(
            AddWorkoutExerciseRequest request
    ) {

        switch (request.exerciseType()) {

            case REPS -> {

                if (
                        request.sets() == null
                                ||
                                request.repetitions() == null
                ) {

                    throw new ConflictException(
                            "REPS exercises require sets and repetitions"
                    );
                }
            }


            case TIME -> {

                if (
                        request.durationSeconds() == null
                ) {

                    throw new ConflictException(
                            "TIME exercises require duration"
                    );
                }
            }


            case DISTANCE -> {

                if (
                        request.distance() == null
                ) {

                    throw new ConflictException(
                            "DISTANCE exercises require distance"
                    );
                }
            }
        }
    }
}