package com.fitnesscenter.workout.controller;

import com.fitnesscenter.common.response.ApiResponse;
import com.fitnesscenter.workout.dto.CreateExerciseRequest;
import com.fitnesscenter.workout.dto.ExerciseResponse;
import com.fitnesscenter.workout.entity.ExerciseCategory;
import com.fitnesscenter.workout.service.ExerciseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/exercises")
@RequiredArgsConstructor
public class ExerciseController {

    private final ExerciseService exerciseService;


    @PostMapping
    @PreAuthorize(
            "hasAnyRole('ADMIN', 'MANAGER')"
    )
    public ResponseEntity<ApiResponse<ExerciseResponse>>
    create(
            @Valid @RequestBody
            CreateExerciseRequest request
    ) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        ApiResponse.success(
                                "Exercise created successfully",
                                exerciseService.create(request)
                        )
                );
    }


    @GetMapping("/{id}")
    @PreAuthorize(
            "hasAnyRole('ADMIN', 'MANAGER', 'TRAINER', 'RECEPTIONIST')"
    )
    public ResponseEntity<ApiResponse<ExerciseResponse>>
    getById(
            @PathVariable Long id
    ) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Exercise retrieved successfully",
                        exerciseService.getById(id)
                )
        );
    }


    @GetMapping
    @PreAuthorize(
            "hasAnyRole('ADMIN', 'MANAGER', 'TRAINER', 'RECEPTIONIST')"
    )
    public ResponseEntity<ApiResponse<List<ExerciseResponse>>>
    getAll() {

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Exercises retrieved successfully",
                        exerciseService.getAll()
                )
        );
    }


    @GetMapping("/category/{category}")
    @PreAuthorize(
            "hasAnyRole('ADMIN', 'MANAGER', 'TRAINER', 'RECEPTIONIST')"
    )
    public ResponseEntity<ApiResponse<List<ExerciseResponse>>>
    getByCategory(
            @PathVariable ExerciseCategory category
    ) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Exercises retrieved successfully",
                        exerciseService.getByCategory(category)
                )
        );
    }


    @PatchMapping("/{id}/deactivate")
    @PreAuthorize(
            "hasAnyRole('ADMIN', 'MANAGER')"
    )
    public ResponseEntity<ApiResponse<ExerciseResponse>>
    deactivate(
            @PathVariable Long id
    ) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Exercise deactivated successfully",
                        exerciseService.deactivate(id)
                )
        );
    }


    @PatchMapping("/{id}/activate")
    @PreAuthorize(
            "hasAnyRole('ADMIN', 'MANAGER')"
    )
    public ResponseEntity<ApiResponse<ExerciseResponse>>
    activate(
            @PathVariable Long id
    ) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Exercise activated successfully",
                        exerciseService.activate(id)
                )
        );
    }
}