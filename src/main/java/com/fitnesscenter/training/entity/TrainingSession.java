package com.fitnesscenter.training.entity;

import com.fitnesscenter.common.entity.BaseEntity;
import com.fitnesscenter.member.entity.MemberProfile;
import com.fitnesscenter.trainer.entity.TrainerProfile;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(
        name = "training_sessions",
        indexes = {
                @Index(
                        name = "idx_session_trainer",
                        columnList = "trainer_id"
                ),
                @Index(
                        name = "idx_session_member",
                        columnList = "member_id"
                ),
                @Index(
                        name = "idx_session_date",
                        columnList = "session_date"
                ),
                @Index(
                        name = "idx_session_status",
                        columnList = "status"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
public class TrainingSession extends BaseEntity {

//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;

    @ManyToOne(
            fetch = FetchType.LAZY,
            optional = false
    )
    @JoinColumn(
            name = "trainer_id",
            nullable = false,
            foreignKey = @ForeignKey(
                    name = "fk_session_trainer"
            )
    )
    private TrainerProfile trainer;

    @ManyToOne(
            fetch = FetchType.LAZY,
            optional = false
    )
    @JoinColumn(
            name = "member_id",
            nullable = false,
            foreignKey = @ForeignKey(
                    name = "fk_session_member"
            )
    )
    private MemberProfile member;

    @Column(
            name = "session_date",
            nullable = false
    )
    private LocalDate sessionDate;

    @Column(
            name = "start_time",
            nullable = false
    )
    private LocalTime startTime;

    @Column(
            name = "end_time",
            nullable = false
    )
    private LocalTime endTime;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "session_type",
            nullable = false,
            length = 40
    )
    private TrainingSessionType sessionType;

    @Enumerated(EnumType.STRING)
    @Column(
            nullable = false,
            length = 30
    )
    private TrainingSessionStatus status =
            TrainingSessionStatus.SCHEDULED;

    @Column(
            length = 255
    )
    private String location;

    @Column(
            length = 2000
    )
    private String notes;

    @Column(
            name = "cancellation_reason",
            length = 1000
    )
    private String cancellationReason;

    @Column(
            name = "started_at"
    )
    private LocalDateTime startedAt;

    @Column(
            name = "completed_at"
    )
    private LocalDateTime completedAt;
}