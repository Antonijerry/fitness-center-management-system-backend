package com.fitnesscenter.attendance.entity;

import com.fitnesscenter.common.entity.BaseEntity;
import com.fitnesscenter.member.entity.MemberProfile;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "attendance",
        indexes = {
                @Index(
                        name = "idx_attendance_member",
                        columnList = "member_id"
                ),
                @Index(
                        name = "idx_attendance_date",
                        columnList = "attendance_date"
                ),
                @Index(
                        name = "idx_attendance_status",
                        columnList = "status"
                ),
                @Index(
                        name = "idx_attendance_check_in",
                        columnList = "check_in_time"
                )
        }
)
@Getter
@Setter
@Builder     //added during control access
@NoArgsConstructor
@AllArgsConstructor   //added during control access
public class Attendance extends BaseEntity {

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
                    name = "fk_attendance_member"
            )
    )
    private MemberProfile member;

    @Column(
            name = "attendance_date",
            nullable = false
    )
    private LocalDate attendanceDate;

    @Column(
            name = "check_in_time",
            nullable = false
    )
    private LocalDateTime checkInTime;

    @Column(
            name = "check_out_time"
    )
    private LocalDateTime checkOutTime;

    @Enumerated(EnumType.STRING)
    @Column(
            nullable = false,
            length = 30
    )
    private AttendanceStatus status =
            AttendanceStatus.CHECKED_IN;

    @Enumerated(EnumType.STRING)
    @Column(
            nullable = false,
            length = 30
    )
    private AttendanceMethod method =
            AttendanceMethod.MANUAL;

    @Column(
            length = 1000
    )
    private String notes;
}