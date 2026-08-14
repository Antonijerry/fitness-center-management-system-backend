package com.fitnesscenter.workout.controller;

import com.fitnesscenter.common.response.ApiResponse;
import com.fitnesscenter.workout.dto.LogWorkoutExerciseRequest;
import com.fitnesscenter.workout.dto.LogWorkoutSetRequest;
import com.fitnesscenter.workout.dto.StartWorkoutSessionRequest;
import com.fitnesscenter.workout.dto.WorkoutExerciseLogResponse;
import com.fitnesscenter.workout.dto.WorkoutSessionResponse;
import com.fitnesscenter.workout.dto.WorkoutSetLogResponse;
import com.fitnesscenter.workout.service.WorkoutSessionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/workout-sessions")
@RequiredArgsConstructor
public class WorkoutSessionController {

    private final WorkoutSessionService sessionService;


    @PostMapping
    public ResponseEntity<ApiResponse<WorkoutSessionResponse>>
    start(
            @Valid @RequestBody
            StartWorkoutSessionRequest request
    ) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        ApiResponse.success(
                                "Workout session started successfully",
                                sessionService.start(request)
                        )
                );
    }


    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<WorkoutSessionResponse>>
    getById(
            @PathVariable Long id
    ) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Workout session retrieved successfully",
                        sessionService.getById(id)
                )
        );
    }


    @GetMapping("/member/{memberId}")
    public ResponseEntity<ApiResponse<List<WorkoutSessionResponse>>>
    getByMember(
            @PathVariable Long memberId
    ) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Workout sessions retrieved successfully",
                        sessionService.getByMember(memberId)
                )
        );
    }


    @PatchMapping("/{id}/complete")
    public ResponseEntity<ApiResponse<WorkoutSessionResponse>>
    complete(
            @PathVariable Long id
    ) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Workout session completed successfully",
                        sessionService.complete(id)
                )
        );
    }


    @PatchMapping("/{id}/cancel")
    public ResponseEntity<ApiResponse<WorkoutSessionResponse>>
    cancel(
            @PathVariable Long id
    ) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Workout session cancelled successfully",
                        sessionService.cancel(id)
                )
        );
    }


    @PostMapping("/{sessionId}/exercises")
    public ResponseEntity<ApiResponse<WorkoutExerciseLogResponse>>
    logExercise(
            @PathVariable Long sessionId,

            @Valid @RequestBody
            LogWorkoutExerciseRequest request
    ) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        ApiResponse.success(
                                "Workout exercise logged successfully",
                                sessionService.logExercise(
                                        sessionId,
                                        request
                                )
                        )
                );
    }


    @GetMapping("/{sessionId}/exercises")
    public ResponseEntity<ApiResponse<List<WorkoutExerciseLogResponse>>>
    getExercises(
            @PathVariable Long sessionId
    ) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Workout exercises retrieved successfully",
                        sessionService.getExercises(sessionId)
                )
        );
    }


    @PostMapping("/exercises/{exerciseLogId}/sets")
    public ResponseEntity<ApiResponse<WorkoutSetLogResponse>>
    logSet(
            @PathVariable Long exerciseLogId,

            @Valid @RequestBody
            LogWorkoutSetRequest request
    ) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        ApiResponse.success(
                                "Workout set logged successfully",
                                sessionService.logSet(
                                        exerciseLogId,
                                        request
                                )
                        )
                );
    }


    @GetMapping("/exercises/{exerciseLogId}/sets")
    public ResponseEntity<ApiResponse<List<WorkoutSetLogResponse>>>
    getSets(
            @PathVariable Long exerciseLogId
    ) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Workout sets retrieved successfully",
                        sessionService.getSets(
                                exerciseLogId
                        )
                )
        );
    }
}