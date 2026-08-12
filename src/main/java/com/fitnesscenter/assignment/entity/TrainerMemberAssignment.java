package com.fitnesscenter.assignment.entity;

import com.fitnesscenter.common.entity.BaseEntity;
import com.fitnesscenter.member.entity.MemberProfile;
import com.fitnesscenter.trainer.entity.TrainerProfile;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "trainer_member_assignments",
        indexes = {
                @Index(
                        name = "idx_assignment_trainer",
                        columnList = "trainer_id"
                ),
                @Index(
                        name = "idx_assignment_member",
                        columnList = "member_id"
                ),
                @Index(
                        name = "idx_assignment_status",
                        columnList = "status"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
public class TrainerMemberAssignment extends BaseEntity {

//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;

    //means A trainer can have many members:
    @ManyToOne(
            fetch = FetchType.LAZY,
            optional = false
    )
    @JoinColumn(
            name = "trainer_id",
            nullable = false,
            foreignKey = @ForeignKey(
                    name = "fk_assignment_trainer"
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
                    name = "fk_assignment_member"
            )
    )
    private MemberProfile member;

    @Column(
            name = "assigned_at",
            nullable = false
    )
    private LocalDateTime assignedAt;

    @Column(
            name = "ended_at"
    )
    private LocalDateTime endedAt;

    @Enumerated(EnumType.STRING)
    @Column(
            nullable = false,
            length = 20
    )
    private AssignmentStatus status =
            AssignmentStatus.ACTIVE;

    @Column(
            length = 2000
    )
    private String notes;

    @Column(
            name = "primary_trainer",
            nullable = false
    )
    private boolean primaryTrainer = false;
}