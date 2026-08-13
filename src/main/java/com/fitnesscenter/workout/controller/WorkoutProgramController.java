package com.fitnesscenter.workout.controller;

import com.fitnesscenter.common.response.ApiResponse;
import com.fitnesscenter.workout.dto.AddWorkoutExerciseRequest;
import com.fitnesscenter.workout.dto.CreateWorkoutProgramDayRequest;
import com.fitnesscenter.workout.dto.CreateWorkoutProgramRequest;
import com.fitnesscenter.workout.dto.UpdateWorkoutProgramRequest;
import com.fitnesscenter.workout.dto.WorkoutExerciseResponse;
import com.fitnesscenter.workout.dto.WorkoutProgramDayResponse;
import com.fitnesscenter.workout.dto.WorkoutProgramResponse;
import com.fitnesscenter.workout.service.WorkoutProgramService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/workout-programs")
@RequiredArgsConstructor
public class WorkoutProgramController {

    private final WorkoutProgramService programService;


    @PostMapping
    @PreAuthorize(
            "hasAnyRole('ADMIN', 'MANAGER', 'TRAINER')"
    )
    public ResponseEntity<ApiResponse<WorkoutProgramResponse>>
    create(
            @Valid @RequestBody
            CreateWorkoutProgramRequest request
    ) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        ApiResponse.success(
                                "Workout program created successfully",
                                programService.create(request)
                        )
                );
    }


    @GetMapping("/{id}")
    @PreAuthorize(
            "hasAnyRole('ADMIN', 'MANAGER', 'TRAINER', 'RECEPTIONIST')"
    )
    public ResponseEntity<ApiResponse<WorkoutProgramResponse>>
    getById(
            @PathVariable Long id
    ) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Workout program retrieved successfully",
                        programService.getById(id)
                )
        );
    }


    @GetMapping
    @PreAuthorize(
            "hasAnyRole('ADMIN', 'MANAGER', 'RECEPTIONIST')"
    )
    public ResponseEntity<ApiResponse<List<WorkoutProgramResponse>>>
    getAll() {

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Workout programs retrieved successfully",
                        programService.getAll()
                )
        );
    }


    @GetMapping("/member/{memberId}")
    @PreAuthorize(
            "hasAnyRole('ADMIN', 'MANAGER', 'TRAINER', 'RECEPTIONIST')"
    )
    public ResponseEntity<ApiResponse<List<WorkoutProgramResponse>>>
    getByMember(
            @PathVariable Long memberId
    ) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Member workout programs retrieved successfully",
                        programService.getByMember(memberId)
                )
        );
    }


    @GetMapping("/trainer/{trainerId}")
    @PreAuthorize(
            "hasAnyRole('ADMIN', 'MANAGER', 'TRAINER')"
    )
    public ResponseEntity<ApiResponse<List<WorkoutProgramResponse>>>
    getByTrainer(
            @PathVariable Long trainerId
    ) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Trainer workout programs retrieved successfully",
                        programService.getByTrainer(trainerId)
                )
        );
    }


    @PutMapping("/{id}")
    @PreAuthorize(
            "hasAnyRole('ADMIN', 'MANAGER', 'TRAINER')"
    )
    public ResponseEntity<ApiResponse<WorkoutProgramResponse>>
    update(
            @PathVariable Long id,

            @Valid @RequestBody
            UpdateWorkoutProgramRequest request
    ) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Workout program updated successfully",
                        programService.update(
                                id,
                                request
                        )
                )
        );
    }


    @PatchMapping("/{id}/activate")
    @PreAuthorize(
            "hasAnyRole('ADMIN', 'MANAGER', 'TRAINER')"
    )
    public ResponseEntity<ApiResponse<WorkoutProgramResponse>>
    activate(
            @PathVariable Long id
    ) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Workout program activated successfully",
                        programService.activate(id)
                )
        );
    }


    @PatchMapping("/{id}/complete")
    @PreAuthorize(
            "hasAnyRole('ADMIN', 'MANAGER', 'TRAINER')"
    )
    public ResponseEntity<ApiResponse<WorkoutProgramResponse>>
    complete(
            @PathVariable Long id
    ) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Workout program completed successfully",
                        programService.complete(id)
                )
        );
    }


    @PatchMapping("/{id}/cancel")
    @PreAuthorize(
            "hasAnyRole('ADMIN', 'MANAGER', 'TRAINER')"
    )
    public ResponseEntity<ApiResponse<WorkoutProgramResponse>>
    cancel(
            @PathVariable Long id
    ) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Workout program cancelled successfully",
                        programService.cancel(id)
                )
        );
    }


    @PostMapping("/{programId}/days")
    @PreAuthorize(
            "hasAnyRole('ADMIN', 'MANAGER', 'TRAINER')"
    )
    public ResponseEntity<ApiResponse<WorkoutProgramDayResponse>>
    addDay(
            @PathVariable Long programId,

            @Valid @RequestBody
            CreateWorkoutProgramDayRequest request
    ) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        ApiResponse.success(
                                "Workout day created successfully",
                                programService.addDay(
                                        programId,
                                        request
                                )
                        )
                );
    }


    @GetMapping("/{programId}/days")
    @PreAuthorize(
            "hasAnyRole('ADMIN', 'MANAGER', 'TRAINER', 'RECEPTIONIST')"
    )
    public ResponseEntity<ApiResponse<List<WorkoutProgramDayResponse>>>
    getDays(
            @PathVariable Long programId
    ) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Workout days retrieved successfully",
                        programService.getDays(programId)
                )
        );
    }


    @PostMapping("/days/{dayId}/exercises")
    @PreAuthorize(
            "hasAnyRole('ADMIN', 'MANAGER', 'TRAINER')"
    )
    public ResponseEntity<ApiResponse<WorkoutExerciseResponse>>
    addExercise(
            @PathVariable Long dayId,

            @Valid @RequestBody
            AddWorkoutExerciseRequest request
    ) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        ApiResponse.success(
                                "Exercise added to workout successfully",
                                programService.addExercise(
                                        dayId,
                                        request
                                )
                        )
                );
    }


    @GetMapping("/days/{dayId}/exercises")
    @PreAuthorize(
            "hasAnyRole('ADMIN', 'MANAGER', 'TRAINER', 'RECEPTIONIST')"
    )
    public ResponseEntity<ApiResponse<List<WorkoutExerciseResponse>>>
    getExercises(
            @PathVariable Long dayId
    ) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Workout exercises retrieved successfully",
                        programService.getExercises(dayId)
                )
        );
    }


    @DeleteMapping("/exercises/{exerciseId}")
    @PreAuthorize(
            "hasAnyRole('ADMIN', 'MANAGER', 'TRAINER')"
    )
    public ResponseEntity<ApiResponse<Void>>
    removeExercise(
            @PathVariable Long exerciseId
    ) {

        programService.removeExercise(
                exerciseId
        );

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Workout exercise removed successfully",
                        null
                )
        );
    }
}