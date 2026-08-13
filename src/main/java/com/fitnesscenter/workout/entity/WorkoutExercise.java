package com.fitnesscenter.workout.entity;

import com.fitnesscenter.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(
        name = "workout_exercises",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_day_exercise_order",
                        columnNames = {
                                "program_day_id",
                                "exercise_order"
                        }
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
public class WorkoutExercise extends BaseEntity {

    @ManyToOne(
            fetch = FetchType.LAZY,
            optional = false
    )
    @JoinColumn(
            name = "program_day_id",
            nullable = false,
            foreignKey = @ForeignKey(
                    name = "fk_workout_exercise_day"
            )
    )
    private WorkoutProgramDay programDay;

    @ManyToOne(
            fetch = FetchType.LAZY,
            optional = false
    )
    @JoinColumn(
            name = "exercise_id",
            nullable = false,
            foreignKey = @ForeignKey(
                    name = "fk_workout_exercise_exercise"
            )
    )
    private Exercise exercise;

    @Column(
            name = "exercise_order",
            nullable = false
    )
    private Integer exerciseOrder;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "exercise_type",
            nullable = false,
            length = 20
    )
    private WorkoutExerciseType exerciseType;

    private Integer sets;

    private Integer repetitions;

    @Column(
            precision = 10,
            scale = 2
    )
    private BigDecimal weight;

    @Column(
            name = "duration_seconds"
    )
    private Integer durationSeconds;

    @Column(
            precision = 10,
            scale = 2
    )
    private BigDecimal distance;

    @Column(
            name = "rest_seconds"
    )
    private Integer restSeconds;

    @Column(length = 2000)
    private String instructions;
}