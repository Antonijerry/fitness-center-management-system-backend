package com.fitnesscenter.member.entity;

import com.fitnesscenter.common.entity.BaseEntity;
import com.fitnesscenter.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(
        name = "member_profiles",
        indexes = {
                @Index(
                        name = "idx_member_profile_status",
                        columnList = "status"
                ),
                @Index(
                        name = "idx_member_profile_member_number",
                        columnList = "member_number"
                ),
                @Index(
                        name = "idx_member_profile_phone",
                        columnList = "phone"
                )
        },
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_member_profile_user",
                        columnNames = "user_id"
                ),
                @UniqueConstraint(
                        name = "uk_member_profile_member_number",
                        columnNames = "member_number"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
public class MemberProfile extends BaseEntity {

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
                    name = "fk_member_profile_user"
            )
    )
    private User user;

    @Column(
            name = "member_number",
            nullable = false,
            unique = true,
            length = 30
    )
    private String memberNumber;

    @Column(
            nullable = false,
            length = 30
    )
    private String phone;

    @Column(
            length = 20
    )
    private String gender;

    @Column(
            name = "date_of_birth"
    )
    private LocalDate dateOfBirth;

    @Column(
            length = 255
    )
    private String address;

    @Column(
            name = "emergency_contact_name",
            length = 150
    )
    private String emergencyContactName;

    @Column(
            name = "emergency_contact_phone",
            length = 30
    )
    private String emergencyContactPhone;

    @Column(
            name = "emergency_contact_relationship",
            length = 100
    )
    private String emergencyContactRelationship;

    @Column(
            name = "fitness_goals",
            length = 1000
    )
    private String fitnessGoals;

    @Column(
            name = "fitness_notes",
            length = 2000
    )
    private String fitnessNotes;

    @Enumerated(EnumType.STRING)
    @Column(
            nullable = false,
            length = 30
    )
    private MemberStatus status = MemberStatus.ACTIVE;
}