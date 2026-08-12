package com.fitnesscenter.attendance.repository;

import com.fitnesscenter.attendance.entity.Attendance;
import com.fitnesscenter.attendance.entity.AttendanceStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface AttendanceRepository
        extends JpaRepository<Attendance, Long> {

    List<Attendance> findAllByMemberIdOrderByCheckInTimeDesc(
            Long memberId
    );

    List<Attendance> findAllByAttendanceDateOrderByCheckInTimeDesc(
            LocalDate date
    );

    List<Attendance> findAllByMemberIdAndAttendanceDateOrderByCheckInTimeDesc(
            Long memberId,
            LocalDate date
    );

    List<Attendance> findAllByStatusOrderByCheckInTimeDesc(
            AttendanceStatus status
    );

    Optional<Attendance> findFirstByMemberIdAndStatusOrderByCheckInTimeDesc(
            Long memberId,
            AttendanceStatus status
    );

    boolean existsByMemberIdAndStatus(
            Long memberId,
            AttendanceStatus status
    );

    long countByMemberId(
            Long memberId
    );

    long countByMemberIdAndAttendanceDateBetween(
            Long memberId,
            LocalDate startDate,
            LocalDate endDate
    );
}