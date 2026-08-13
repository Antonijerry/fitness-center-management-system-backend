package com.fitnesscenter.workout.mapper;

import com.fitnesscenter.workout.dto.WorkoutExerciseResponse;
import com.fitnesscenter.workout.dto.WorkoutProgramDayResponse;
import com.fitnesscenter.workout.dto.WorkoutProgramResponse;
import com.fitnesscenter.workout.entity.WorkoutExercise;
import com.fitnesscenter.workout.entity.WorkoutProgram;
import com.fitnesscenter.workout.entity.WorkoutProgramDay;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class WorkoutProgramMapper {

    public WorkoutExerciseResponse toExerciseResponse(
            WorkoutExercise workoutExercise
    ) {

        return new WorkoutExerciseResponse(

                workoutExercise.getId(),

                workoutExercise.getExercise().getId(),

                workoutExercise.getExercise().getName(),

                workoutExercise.getExerciseOrder(),

                workoutExercise.getExerciseType(),

                workoutExercise.getSets(),

                workoutExercise.getRepetitions(),

                workoutExercise.getWeight(),

                workoutExercise.getDurationSeconds(),

                workoutExercise.getDistance(),

                workoutExercise.getRestSeconds(),

                workoutExercise.getInstructions()
        );
    }


    public WorkoutProgramDayResponse toDayResponse(
            WorkoutProgramDay day,
            List<WorkoutExerciseResponse> exercises
    ) {

        return new WorkoutProgramDayResponse(

                day.getId(),

                day.getDayNumber(),

                day.getName(),

                day.getNotes(),

                exercises
        );
    }


    public WorkoutProgramResponse toProgramResponse(
            WorkoutProgram program,
            List<WorkoutProgramDayResponse> days
    ) {

        String memberName =
                program.getMember()
                        .getUser()
                        .getFirstName()
                        + " "
                        + program.getMember()
                        .getUser()
                        .getLastName();

        String trainerName =
                program.getTrainer()
                        .getUser()
                        .getFirstName()
                        + " "
                        + program.getTrainer()
                        .getUser()
                        .getLastName();

        return new WorkoutProgramResponse(

                program.getId(),

                program.getMember().getId(),

                memberName,

                program.getTrainer().getId(),

                trainerName,

                program.getName(),

                program.getDescription(),

                program.getGoal(),

                program.getStartDate(),

                program.getEndDate(),

                program.getStatus(),

                days
        );
    }
}