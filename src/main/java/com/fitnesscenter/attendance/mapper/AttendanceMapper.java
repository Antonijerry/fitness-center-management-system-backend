package com.fitnesscenter.attendance.mapper;

import com.fitnesscenter.attendance.dto.AttendanceResponse;
import com.fitnesscenter.attendance.entity.Attendance;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
public class AttendanceMapper {

    public AttendanceResponse toResponse(
            Attendance attendance
    ) {

        String memberName =
                attendance.getMember()
                        .getUser()
                        .getFirstName()
                        + " "
                        + attendance.getMember()
                        .getUser()
                        .getLastName();

        Long durationMinutes = null;

        if (
                attendance.getCheckOutTime() != null
        ) {

            durationMinutes =
                    Duration.between(
                            attendance.getCheckInTime(),
                            attendance.getCheckOutTime()
                    ).toMinutes();
        }

        return new AttendanceResponse(

                attendance.getId(),

                attendance.getMember().getId(),

                attendance.getMember()
                        .getMemberNumber(),

                memberName,

                attendance.getAttendanceDate(),

                attendance.getCheckInTime(),

                attendance.getCheckOutTime(),

                attendance.getStatus(),

                attendance.getMethod(),

                durationMinutes,

                attendance.getNotes()
        );
    }
}