package com.fitnesscenter.attendance.dto;

public record AttendanceSummaryResponse(

        Long memberId,

        String memberNumber,

        String memberName,

        long totalVisits,

        long totalMinutes

) {
}