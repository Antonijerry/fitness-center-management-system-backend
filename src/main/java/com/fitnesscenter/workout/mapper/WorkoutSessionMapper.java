package com.fitnesscenter.workout.mapper;

import com.fitnesscenter.workout.dto.WorkoutExerciseLogResponse;
import com.fitnesscenter.workout.dto.WorkoutSessionResponse;
import com.fitnesscenter.workout.dto.WorkoutSetLogResponse;
import com.fitnesscenter.workout.entity.WorkoutExerciseLog;
import com.fitnesscenter.workout.entity.WorkoutSession;
import com.fitnesscenter.workout.entity.WorkoutSetLog;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class WorkoutSessionMapper {

    public WorkoutSetLogResponse toSetResponse(
            WorkoutSetLog set
    ) {

        return new WorkoutSetLogResponse(
                set.getId(),
                set.getSetNumber(),
                set.getRepetitions(),
                set.getWeight(),
                set.getDurationSeconds(),
                set.getDistance(),
                set.isCompleted(),
                set.getNotes()
        );
    }


    public WorkoutExerciseLogResponse toExerciseResponse(
            WorkoutExerciseLog exerciseLog,
            List<WorkoutSetLogResponse> sets
    ) {

        return new WorkoutExerciseLogResponse(
                exerciseLog.getId(),
                exerciseLog.getExercise().getId(),
                exerciseLog.getExercise().getName(),
                exerciseLog.getExerciseOrder(),
                exerciseLog.getNotes(),
                sets
        );
    }


    public WorkoutSessionResponse toSessionResponse(
            WorkoutSession session,
            List<WorkoutExerciseLogResponse> exercises
    ) {

        String memberName =
                session.getMember()
                        .getUser()
                        .getFirstName()
                        + " "
                        + session.getMember()
                        .getUser()
                        .getLastName();

        return new WorkoutSessionResponse(
                session.getId(),
                session.getMember().getId(),
                memberName,
                session.getWorkoutProgram().getId(),
                session.getWorkoutProgram().getName(),
                session.getStartedAt(),
                session.getCompletedAt(),
                session.getStatus(),
                session.getNotes(),
                exercises
        );
    }
}