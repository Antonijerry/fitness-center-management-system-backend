package com.fitnesscenter.training.controller;

import com.fitnesscenter.common.response.ApiResponse;
import com.fitnesscenter.training.dto.CreateTrainingSessionRequest;
import com.fitnesscenter.training.dto.TrainingSessionResponse;
import com.fitnesscenter.training.dto.UpdateTrainingSessionRequest;
import com.fitnesscenter.training.service.TrainingSessionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/training-sessions")
@RequiredArgsConstructor
public class TrainingSessionController {

    private final TrainingSessionService sessionService;


    @PostMapping
    @PreAuthorize(
            "hasAnyRole('ADMIN', 'MANAGER', 'RECEPTIONIST')"
    )
    public ResponseEntity<ApiResponse<TrainingSessionResponse>>
    create(
            @Valid @RequestBody
            CreateTrainingSessionRequest request
    ) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        ApiResponse.success(
                                "Training session created successfully",
                                sessionService.create(request)
                        )
                );
    }


    @GetMapping("/{id}")
    @PreAuthorize(
            "hasAnyRole('ADMIN', 'MANAGER', 'RECEPTIONIST', 'TRAINER')"
    )
    public ResponseEntity<ApiResponse<TrainingSessionResponse>>
    getById(
            @PathVariable Long id
    ) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Training session retrieved successfully",
                        sessionService.getById(id)
                )
        );
    }


    @GetMapping
    @PreAuthorize(
            "hasAnyRole('ADMIN', 'MANAGER', 'RECEPTIONIST')"
    )
    public ResponseEntity<ApiResponse<List<TrainingSessionResponse>>>
    getAll() {

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Training sessions retrieved successfully",
                        sessionService.getAll()
                )
        );
    }


    @GetMapping("/trainer/{trainerId}")
    @PreAuthorize(
            "hasAnyRole('ADMIN', 'MANAGER', 'RECEPTIONIST', 'TRAINER')"
    )
    public ResponseEntity<ApiResponse<List<TrainingSessionResponse>>>
    getByTrainer(
            @PathVariable Long trainerId
    ) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Trainer sessions retrieved successfully",
                        sessionService.getByTrainer(
                                trainerId
                        )
                )
        );
    }


    @GetMapping("/member/{memberId}")
    @PreAuthorize(
            "hasAnyRole('ADMIN', 'MANAGER', 'RECEPTIONIST', 'TRAINER')"
    )
    public ResponseEntity<ApiResponse<List<TrainingSessionResponse>>>
    getByMember(
            @PathVariable Long memberId
    ) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Member sessions retrieved successfully",
                        sessionService.getByMember(
                                memberId
                        )
                )
        );
    }


    @GetMapping("/date/{date}")
    @PreAuthorize(
            "hasAnyRole('ADMIN', 'MANAGER', 'RECEPTIONIST')"
    )
    public ResponseEntity<ApiResponse<List<TrainingSessionResponse>>>
    getByDate(
            @PathVariable
            @DateTimeFormat(
                    iso = DateTimeFormat.ISO.DATE
            )
            LocalDate date
    ) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Sessions retrieved successfully",
                        sessionService.getByDate(
                                date
                        )
                )
        );
    }


    @GetMapping("/trainer/{trainerId}/date/{date}")
    @PreAuthorize(
            "hasAnyRole('ADMIN', 'MANAGER', 'RECEPTIONIST', 'TRAINER')"
    )
    public ResponseEntity<ApiResponse<List<TrainingSessionResponse>>>
    getTrainerSchedule(
            @PathVariable Long trainerId,

            @PathVariable
            @DateTimeFormat(
                    iso = DateTimeFormat.ISO.DATE
            )
            LocalDate date
    ) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Trainer schedule retrieved successfully",
                        sessionService.getTrainerSchedule(
                                trainerId,
                                date
                        )
                )
        );
    }


    @GetMapping("/member/{memberId}/date/{date}")
    @PreAuthorize(
            "hasAnyRole('ADMIN', 'MANAGER', 'RECEPTIONIST', 'TRAINER')"
    )
    public ResponseEntity<ApiResponse<List<TrainingSessionResponse>>>
    getMemberSchedule(
            @PathVariable Long memberId,

            @PathVariable
            @DateTimeFormat(
                    iso = DateTimeFormat.ISO.DATE
            )
            LocalDate date
    ) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Member schedule retrieved successfully",
                        sessionService.getMemberSchedule(
                                memberId,
                                date
                        )
                )
        );
    }


    @PutMapping("/{id}")
    @PreAuthorize(
            "hasAnyRole('ADMIN', 'MANAGER', 'RECEPTIONIST')"
    )
    public ResponseEntity<ApiResponse<TrainingSessionResponse>>
    update(
            @PathVariable Long id,

            @Valid @RequestBody
            UpdateTrainingSessionRequest request
    ) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Training session updated successfully",
                        sessionService.update(
                                id,
                                request
                        )
                )
        );
    }


    @PatchMapping("/{id}/confirm")
    @PreAuthorize(
            "hasAnyRole('ADMIN', 'MANAGER', 'RECEPTIONIST', 'TRAINER')"
    )
    public ResponseEntity<ApiResponse<TrainingSessionResponse>>
    confirm(
            @PathVariable Long id
    ) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Training session confirmed successfully",
                        sessionService.confirm(id)
                )
        );
    }


    @PatchMapping("/{id}/start")
    @PreAuthorize(
            "hasAnyRole('ADMIN', 'MANAGER', 'TRAINER')"
    )
    public ResponseEntity<ApiResponse<TrainingSessionResponse>>
    start(
            @PathVariable Long id
    ) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Training session started successfully",
                        sessionService.start(id)
                )
        );
    }


    @PatchMapping("/{id}/complete")
    @PreAuthorize(
            "hasAnyRole('ADMIN', 'MANAGER', 'TRAINER')"
    )
    public ResponseEntity<ApiResponse<TrainingSessionResponse>>
    complete(
            @PathVariable Long id
    ) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Training session completed successfully",
                        sessionService.complete(id)
                )
        );
    }


    @PatchMapping("/{id}/cancel")
    @PreAuthorize(
            "hasAnyRole('ADMIN', 'MANAGER', 'RECEPTIONIST', 'TRAINER')"
    )
    public ResponseEntity<ApiResponse<TrainingSessionResponse>>
    cancel(
            @PathVariable Long id,

            @RequestParam(
                    required = false
            )
            String reason
    ) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Training session cancelled successfully",
                        sessionService.cancel(
                                id,
                                reason
                        )
                )
        );
    }
}