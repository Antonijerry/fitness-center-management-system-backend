package com.fitnesscenter.workout.service;

import com.fitnesscenter.workout.dto.ExerciseProgressResponse;
import com.fitnesscenter.workout.dto.PersonalRecordResponse;
import com.fitnesscenter.workout.dto.WorkoutProgressSummaryResponse;

import java.time.LocalDate;
import java.util.List;

public interface WorkoutAnalyticsService {

    WorkoutProgressSummaryResponse getProgressSummary(
            Long memberId,
            LocalDate fromDate,
            LocalDate toDate
    );

    List<ExerciseProgressResponse> getExerciseProgress(
            Long memberId,
            LocalDate fromDate,
            LocalDate toDate
    );

    List<PersonalRecordResponse> getPersonalRecords(
            Long memberId,
            LocalDate fromDate,
            LocalDate toDate
    );
}