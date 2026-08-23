package com.fitnesscenter.dashboard.controller;

import com.fitnesscenter.common.response.ApiResponse;
import com.fitnesscenter.dashboard.dto.DashboardStatsResponse;
import com.fitnesscenter.dashboard.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/stats")
    public ResponseEntity<ApiResponse<DashboardStatsResponse>>
    getDashboardStats() {

        DashboardStatsResponse stats =
                dashboardService.getDashboardStats();

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Dashboard statistics retrieved successfully.",
                        stats
                )
        );
    }
}