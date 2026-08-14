package com.fitnesscenter.workout.entity;

import com.fitnesscenter.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(
        name = "workout_set_logs",
        indexes = {
                @Index(
                        name = "idx_set_log_exercise_log",
                        columnList = "workout_exercise_log_id"
                )
        },
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_exercise_log_set",
                        columnNames = {
                                "workout_exercise_log_id",
                                "set_number"
                        }
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
public class WorkoutSetLog extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "workout_exercise_log_id",
            nullable = false
    )
    private WorkoutExerciseLog workoutExerciseLog;

    @Column(
            name = "set_number",
            nullable = false
    )
    private Integer setNumber;

    @Column(name = "repetitions")
    private Integer repetitions;

    @Column(
            name = "weight",
            precision = 10,
            scale = 2
    )
    private BigDecimal weight;

    @Column(name = "duration_seconds")
    private Integer durationSeconds;

    @Column(
            name = "distance",
            precision = 10,
            scale = 2
    )
    private BigDecimal distance;

    @Column(name = "completed")
    private boolean completed;

    @Column(
            name = "notes",
            columnDefinition = "TEXT"
    )
    private String notes;
}