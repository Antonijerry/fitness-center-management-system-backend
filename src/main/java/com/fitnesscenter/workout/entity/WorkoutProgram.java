package com.fitnesscenter.workout.entity;

import com.fitnesscenter.common.entity.BaseEntity;
import com.fitnesscenter.member.entity.MemberProfile;
import com.fitnesscenter.trainer.entity.TrainerProfile;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(
        name = "workout_programs",
        indexes = {
                @Index(
                        name = "idx_workout_program_member",
                        columnList = "member_id"
                ),
                @Index(
                        name = "idx_workout_program_trainer",
                        columnList = "trainer_id"
                ),
                @Index(
                        name = "idx_workout_program_status",
                        columnList = "status"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
public class WorkoutProgram extends BaseEntity {

//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;

    @ManyToOne(
            fetch = FetchType.LAZY,
            optional = false
    )
    @JoinColumn(
            name = "member_id",
            nullable = false,
            foreignKey = @ForeignKey(
                    name = "fk_workout_program_member"
            )
    )
    private MemberProfile member;

    @ManyToOne(
            fetch = FetchType.LAZY,
            optional = false
    )
    @JoinColumn(
            name = "trainer_id",
            nullable = false,
            foreignKey = @ForeignKey(
                    name = "fk_workout_program_trainer"
            )
    )
    private TrainerProfile trainer;

    @Column(
            nullable = false,
            length = 200
    )
    private String name;

    @Column(length = 2000)
    private String description;

    @Column(length = 255)
    private String goal;

    @Column(nullable = false)
    private LocalDate startDate;

    private LocalDate endDate;

    @Enumerated(EnumType.STRING)
    @Column(
            nullable = false,
            length = 30
    )
    private WorkoutProgramStatus status =
            WorkoutProgramStatus.DRAFT;
}