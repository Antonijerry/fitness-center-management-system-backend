package com.fitnesscenter.attendance.service;

import com.fitnesscenter.attendance.dto.AttendanceResponse;
import com.fitnesscenter.attendance.dto.CheckInRequest;

import java.time.LocalDate;
import java.util.List;

public interface AttendanceService {

    AttendanceResponse checkIn(
            CheckInRequest request
    );

    AttendanceResponse checkOut(
            Long memberId
    );

    AttendanceResponse getById(
            Long id
    );

    List<AttendanceResponse> getByMember(
            Long memberId
    );

    List<AttendanceResponse> getByDate(
            LocalDate date
    );

    List<AttendanceResponse> getToday();

    List<AttendanceResponse> getCurrentlyCheckedIn();

    List<AttendanceResponse> getMemberAttendanceByDate(
            Long memberId,
            LocalDate date
    );
}