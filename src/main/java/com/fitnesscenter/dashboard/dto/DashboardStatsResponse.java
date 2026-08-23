package com.fitnesscenter.dashboard.dto;

import java.math.BigDecimal;

public record DashboardStatsResponse(

        long totalMembers,

        long activeMembers,

        long totalTrainers,

        long totalMemberships,

        long activeMemberships,

        long totalTrainingSessions,

        long todayAttendance,

        long expiringMemberships,

        BigDecimal totalRevenue

) {
}