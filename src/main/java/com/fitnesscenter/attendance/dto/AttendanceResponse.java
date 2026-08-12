package com.fitnesscenter.attendance.dto;

import com.fitnesscenter.attendance.entity.AttendanceMethod;
import com.fitnesscenter.attendance.entity.AttendanceStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record AttendanceResponse(

        Long id,

        Long memberId,

        String memberNumber,

        String memberName,

        LocalDate attendanceDate,

        LocalDateTime checkInTime,

        LocalDateTime checkOutTime,

        AttendanceStatus status,

        AttendanceMethod method,

        Long durationMinutes,

        String notes
) {
}