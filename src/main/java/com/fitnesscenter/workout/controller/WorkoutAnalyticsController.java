package com.fitnesscenter.workout.controller;

import com.fitnesscenter.common.response.ApiResponse;
import com.fitnesscenter.workout.dto.ExerciseProgressResponse;
import com.fitnesscenter.workout.dto.PersonalRecordResponse;
import com.fitnesscenter.workout.dto.WorkoutProgressSummaryResponse;
import com.fitnesscenter.workout.service.WorkoutAnalyticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/workout-analytics")
@RequiredArgsConstructor
public class WorkoutAnalyticsController {

    private final WorkoutAnalyticsService analyticsService;


    @GetMapping("/members/{memberId}/summary")
    public ResponseEntity<
            ApiResponse<WorkoutProgressSummaryResponse>
            > getProgressSummary(

            @PathVariable Long memberId,

            @RequestParam
            @DateTimeFormat(
                    iso = DateTimeFormat.ISO.DATE
            )
            LocalDate fromDate,

            @RequestParam
            @DateTimeFormat(
                    iso = DateTimeFormat.ISO.DATE
            )
            LocalDate toDate
    ) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Workout progress summary retrieved successfully",

                        analyticsService.getProgressSummary(
                                memberId,
                                fromDate,
                                toDate
                        )
                )
        );
    }


    @GetMapping("/members/{memberId}/exercises")
    public ResponseEntity<
            ApiResponse<List<ExerciseProgressResponse>>
            > getExerciseProgress(

            @PathVariable Long memberId,

            @RequestParam
            @DateTimeFormat(
                    iso = DateTimeFormat.ISO.DATE
            )
            LocalDate fromDate,

            @RequestParam
            @DateTimeFormat(
                    iso = DateTimeFormat.ISO.DATE
            )
            LocalDate toDate
    ) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Exercise progress retrieved successfully",

                        analyticsService.getExerciseProgress(
                                memberId,
                                fromDate,
                                toDate
                        )
                )
        );
    }


    @GetMapping("/members/{memberId}/personal-records")
    public ResponseEntity<
            ApiResponse<List<PersonalRecordResponse>>
            > getPersonalRecords(

            @PathVariable Long memberId,

            @RequestParam
            @DateTimeFormat(
                    iso = DateTimeFormat.ISO.DATE
            )
            LocalDate fromDate,

            @RequestParam
            @DateTimeFormat(
                    iso = DateTimeFormat.ISO.DATE
            )
            LocalDate toDate
    ) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Personal records retrieved successfully",

                        analyticsService.getPersonalRecords(
                                memberId,
                                fromDate,
                                toDate
                        )
                )
        );
    }
}