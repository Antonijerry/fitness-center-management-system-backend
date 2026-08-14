package com.fitnesscenter.workout.entity;

import com.fitnesscenter.common.entity.BaseEntity;
import com.fitnesscenter.member.entity.MemberProfile;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
        name = "workout_sessions",
        indexes = {
                @Index(
                        name = "idx_workout_session_member",
                        columnList = "member_id"
                ),
                @Index(
                        name = "idx_workout_session_program",
                        columnList = "workout_program_id"
                ),
                @Index(
                        name = "idx_workout_session_started_at",
                        columnList = "started_at"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
public class WorkoutSession extends BaseEntity {

//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "member_id",
            nullable = false
    )
    private MemberProfile member;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "workout_program_id",
            nullable = false
    )
    private WorkoutProgram workoutProgram;

    @Column(
            name = "started_at",
            nullable = false
    )
    private LocalDateTime startedAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "status",
            nullable = false,
            length = 30
    )
    private WorkoutSessionStatus status;

    @Column(
            name = "notes",
            columnDefinition = "TEXT"
    )
    private String notes;

    @OneToMany(
            mappedBy = "workoutSession",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @OrderBy("exerciseOrder ASC")
    private List<WorkoutExerciseLog> exerciseLogs =
            new ArrayList<>();
}