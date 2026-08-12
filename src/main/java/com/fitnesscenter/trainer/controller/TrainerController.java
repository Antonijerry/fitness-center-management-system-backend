package com.fitnesscenter.trainer.controller;

import com.fitnesscenter.common.response.ApiResponse;
import com.fitnesscenter.trainer.dto.CreateTrainerRequest;
import com.fitnesscenter.trainer.dto.TrainerResponse;
import com.fitnesscenter.trainer.dto.UpdateTrainerRequest;
import com.fitnesscenter.trainer.entity.TrainerStatus;
import com.fitnesscenter.trainer.service.TrainerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/trainers")
@RequiredArgsConstructor
public class TrainerController {

    private final TrainerService trainerService;


    @PostMapping
    @PreAuthorize(
            "hasAnyRole('ADMIN', 'MANAGER')"
    )
    public ResponseEntity<ApiResponse<TrainerResponse>> create(
            @Valid @RequestBody
            CreateTrainerRequest request
    ) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        ApiResponse.success(
                                "Trainer created successfully",
                                trainerService.create(request)
                        )
                );
    }


    @GetMapping("/{id}")
    @PreAuthorize(
            "hasAnyRole('ADMIN', 'MANAGER', 'RECEPTIONIST')"
    )
    public ResponseEntity<ApiResponse<TrainerResponse>> getById(
            @PathVariable Long id
    ) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Trainer retrieved successfully",
                        trainerService.getById(id)
                )
        );
    }


    @GetMapping
    @PreAuthorize(
            "hasAnyRole('ADMIN', 'MANAGER', 'RECEPTIONIST')"
    )
    public ResponseEntity<ApiResponse<List<TrainerResponse>>> getAll() {

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Trainers retrieved successfully",
                        trainerService.getAll()
                )
        );
    }


    @GetMapping("/user/{userId}")
    @PreAuthorize(
            "hasAnyRole('ADMIN', 'MANAGER', 'RECEPTIONIST')"
    )
    public ResponseEntity<ApiResponse<TrainerResponse>> getByUserId(
            @PathVariable Long userId
    ) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Trainer retrieved successfully",
                        trainerService.getByUserId(userId)
                )
        );
    }


    @GetMapping("/employee/{employeeNumber}")
    @PreAuthorize(
            "hasAnyRole('ADMIN', 'MANAGER', 'RECEPTIONIST')"
    )
    public ResponseEntity<ApiResponse<TrainerResponse>> getByEmployeeNumber(
            @PathVariable String employeeNumber
    ) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Trainer retrieved successfully",
                        trainerService.getByEmployeeNumber(
                                employeeNumber
                        )
                )
        );
    }


    @GetMapping("/status/{status}")
    @PreAuthorize(
            "hasAnyRole('ADMIN', 'MANAGER', 'RECEPTIONIST')"
    )
    public ResponseEntity<ApiResponse<List<TrainerResponse>>> getByStatus(
            @PathVariable TrainerStatus status
    ) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Trainers retrieved successfully",
                        trainerService.getByStatus(status)
                )
        );
    }


    @GetMapping("/specialization")
    @PreAuthorize(
            "hasAnyRole('ADMIN', 'MANAGER', 'RECEPTIONIST')"
    )
    public ResponseEntity<ApiResponse<List<TrainerResponse>>>
    getBySpecialization(
            @RequestParam String value
    ) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Trainers retrieved successfully",
                        trainerService.getBySpecialization(value)
                )
        );
    }


    @PutMapping("/{id}")
    @PreAuthorize(
            "hasAnyRole('ADMIN', 'MANAGER')"
    )
    public ResponseEntity<ApiResponse<TrainerResponse>> update(
            @PathVariable Long id,
            @Valid @RequestBody
            UpdateTrainerRequest request
    ) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Trainer updated successfully",
                        trainerService.update(
                                id,
                                request
                        )
                )
        );
    }


    @PatchMapping("/{id}/status")
    @PreAuthorize(
            "hasAnyRole('ADMIN', 'MANAGER')"
    )
    public ResponseEntity<ApiResponse<TrainerResponse>> updateStatus(
            @PathVariable Long id,
            @RequestParam TrainerStatus status
    ) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Trainer status updated successfully",
                        trainerService.updateStatus(
                                id,
                                status
                        )
                )
        );
    }
}