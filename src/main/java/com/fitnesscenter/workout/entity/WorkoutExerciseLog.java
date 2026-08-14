package com.fitnesscenter.workout.entity;

import com.fitnesscenter.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
        name = "workout_exercise_logs",
        indexes = {
                @Index(
                        name = "idx_exercise_log_session",
                        columnList = "workout_session_id"
                ),
                @Index(
                        name = "idx_exercise_log_exercise",
                        columnList = "exercise_id"
                )
        },
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_session_exercise",
                        columnNames = {
                                "workout_session_id",
                                "exercise_id"
                        }
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
public class WorkoutExerciseLog extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "workout_session_id",
            nullable = false
    )
    private WorkoutSession workoutSession;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "exercise_id",
            nullable = false
    )
    private Exercise exercise;

    @Column(
            name = "exercise_order",
            nullable = false
    )
    private Integer exerciseOrder;

    @Column(
            name = "notes",
            columnDefinition = "TEXT"
    )
    private String notes;

    @OneToMany(
            mappedBy = "workoutExerciseLog",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @OrderBy("setNumber ASC")
    private List<WorkoutSetLog> sets =
            new ArrayList<>();
}