package com.fitnesscenter.assignment.controller;

import com.fitnesscenter.assignment.dto.AssignmentResponse;
import com.fitnesscenter.assignment.dto.CreateAssignmentRequest;
import com.fitnesscenter.assignment.dto.UpdateAssignmentRequest;
import com.fitnesscenter.assignment.service.TrainerMemberAssignmentService;
import com.fitnesscenter.common.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/assignments")
@RequiredArgsConstructor
public class TrainerMemberAssignmentController {

    private final TrainerMemberAssignmentService assignmentService;


    @PostMapping
    @PreAuthorize(
            "hasAnyRole('ADMIN', 'MANAGER')"
    )
    public ResponseEntity<ApiResponse<AssignmentResponse>> create(
            @Valid @RequestBody
            CreateAssignmentRequest request
    ) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        ApiResponse.success(
                                "Trainer assigned to member successfully",
                                assignmentService.create(request)
                        )
                );
    }


    @GetMapping("/{id}")
    @PreAuthorize(
            "hasAnyRole('ADMIN', 'MANAGER', 'RECEPTIONIST')"
    )
    public ResponseEntity<ApiResponse<AssignmentResponse>> getById(
            @PathVariable Long id
    ) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Assignment retrieved successfully",
                        assignmentService.getById(id)
                )
        );
    }


    @GetMapping
    @PreAuthorize(
            "hasAnyRole('ADMIN', 'MANAGER', 'RECEPTIONIST')"
    )
    public ResponseEntity<ApiResponse<List<AssignmentResponse>>> getAll() {

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Assignments retrieved successfully",
                        assignmentService.getAll()
                )
        );
    }


    @GetMapping("/trainer/{trainerId}")
    @PreAuthorize(
            "hasAnyRole('ADMIN', 'MANAGER', 'RECEPTIONIST')"
    )
    public ResponseEntity<ApiResponse<List<AssignmentResponse>>>
    getByTrainer(
            @PathVariable Long trainerId
    ) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Trainer assignments retrieved successfully",
                        assignmentService.getByTrainer(
                                trainerId
                        )
                )
        );
    }


    @GetMapping("/trainer/{trainerId}/active")
    @PreAuthorize(
            "hasAnyRole('ADMIN', 'MANAGER', 'RECEPTIONIST')"
    )
    public ResponseEntity<ApiResponse<List<AssignmentResponse>>>
    getActiveByTrainer(
            @PathVariable Long trainerId
    ) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Active trainer assignments retrieved successfully",
                        assignmentService.getActiveByTrainer(
                                trainerId
                        )
                )
        );
    }


    @GetMapping("/member/{memberId}")
    @PreAuthorize(
            "hasAnyRole('ADMIN', 'MANAGER', 'RECEPTIONIST')"
    )
    public ResponseEntity<ApiResponse<List<AssignmentResponse>>>
    getByMember(
            @PathVariable Long memberId
    ) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Member assignments retrieved successfully",
                        assignmentService.getByMember(
                                memberId
                        )
                )
        );
    }


    @GetMapping("/member/{memberId}/active")
    @PreAuthorize(
            "hasAnyRole('ADMIN', 'MANAGER', 'RECEPTIONIST')"
    )
    public ResponseEntity<ApiResponse<List<AssignmentResponse>>>
    getActiveByMember(
            @PathVariable Long memberId
    ) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Active member assignments retrieved successfully",
                        assignmentService.getActiveByMember(
                                memberId
                        )
                )
        );
    }


    @PutMapping("/{id}")
    @PreAuthorize(
            "hasAnyRole('ADMIN', 'MANAGER')"
    )
    public ResponseEntity<ApiResponse<AssignmentResponse>> update(
            @PathVariable Long id,
            @Valid @RequestBody
            UpdateAssignmentRequest request
    ) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Assignment updated successfully",
                        assignmentService.update(
                                id,
                                request
                        )
                )
        );
    }


    @PatchMapping("/{id}/end")
    @PreAuthorize(
            "hasAnyRole('ADMIN', 'MANAGER')"
    )
    public ResponseEntity<ApiResponse<AssignmentResponse>>
    endAssignment(
            @PathVariable Long id
    ) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Trainer-member assignment ended successfully",
                        assignmentService.endAssignment(id)
                )
        );
    }
}