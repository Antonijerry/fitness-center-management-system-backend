package com.fitnesscenter.attendance.dto;

import com.fitnesscenter.attendance.entity.AttendanceMethod;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CheckInRequest(

        @NotNull(message = "Member ID is required")
        Long memberId,

        AttendanceMethod method,

        @Size(
                max = 1000,
                message = "Notes cannot exceed 1000 characters"
        )
        String notes
) {
}