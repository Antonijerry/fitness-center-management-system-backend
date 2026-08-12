package com.fitnesscenter.trainer.entity;

import com.fitnesscenter.common.entity.BaseEntity;
import com.fitnesscenter.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(
        name = "trainer_profiles",
        indexes = {
                @Index(
                        name = "idx_trainer_profile_status",
                        columnList = "status"
                ),
                @Index(
                        name = "idx_trainer_profile_specialization",
                        columnList = "specialization"
                ),
                @Index(
                        name = "idx_trainer_profile_employee_number",
                        columnList = "employee_number"
                )
        },
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_trainer_profile_user",
                        columnNames = "user_id"
                ),
                @UniqueConstraint(
                        name = "uk_trainer_profile_employee_number",
                        columnNames = "employee_number"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
public class TrainerProfile extends BaseEntity {

//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;

    @OneToOne(
            fetch = FetchType.LAZY,
            optional = false
    )
    @JoinColumn(
            name = "user_id",
            nullable = false,
            foreignKey = @ForeignKey(
                    name = "fk_trainer_profile_user"
            )
    )
    private User user;

    @Column(
            name = "employee_number",
            nullable = false,
            unique = true,
            length = 30
    )
    private String employeeNumber;

    @Column(
            nullable = false,
            length = 150
    )
    private String specialization;

    @Column(
            length = 1000
    )
    private String certifications;

    @Column(
            name = "years_of_experience"
    )
    private Integer yearsOfExperience;

    @Column(
            length = 2000
    )
    private String bio;

    @Column(
            name = "hourly_rate",
            precision = 12,
            scale = 2
    )
    private BigDecimal hourlyRate;

    @Enumerated(EnumType.STRING)
    @Column(
            nullable = false,
            length = 30
    )
    private TrainerStatus status = TrainerStatus.ACTIVE;
}