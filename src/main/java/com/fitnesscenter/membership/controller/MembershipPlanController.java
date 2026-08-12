package com.fitnesscenter.membership.controller;

import com.fitnesscenter.common.response.ApiResponse;
import com.fitnesscenter.membership.dto.CreateMembershipPlanRequest;
import com.fitnesscenter.membership.dto.MembershipPlanResponse;
import com.fitnesscenter.membership.dto.UpdateMembershipPlanRequest;
import com.fitnesscenter.membership.service.MembershipPlanService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/membership-plans")
@RequiredArgsConstructor
public class MembershipPlanController {

    private final MembershipPlanService planService;


    @PostMapping
    @PreAuthorize(
            "hasAnyRole('ADMIN', 'MANAGER')"
    )
    public ResponseEntity<ApiResponse<MembershipPlanResponse>> create(
            @Valid @RequestBody
            CreateMembershipPlanRequest request
    ) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        ApiResponse.success(
                                "Membership plan created successfully",
                                planService.create(request)
                        )
                );
    }


    @PutMapping("/{id}")
    @PreAuthorize(
            "hasAnyRole('ADMIN', 'MANAGER')"
    )
    public ResponseEntity<ApiResponse<MembershipPlanResponse>> update(
            @PathVariable Long id,
            @Valid @RequestBody
            UpdateMembershipPlanRequest request
    ) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Membership plan updated successfully",
                        planService.update(
                                id,
                                request
                        )
                )
        );
    }


    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<MembershipPlanResponse>> getById(
            @PathVariable Long id
    ) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Membership plan retrieved successfully",
                        planService.getById(id)
                )
        );
    }


    @GetMapping
    public ResponseEntity<ApiResponse<List<MembershipPlanResponse>>> getAll() {

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Membership plans retrieved successfully",
                        planService.getAll()
                )
        );
    }


    @GetMapping("/active")
    public ResponseEntity<ApiResponse<List<MembershipPlanResponse>>> getActive() {

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Active membership plans retrieved successfully",
                        planService.getActive()
                )
        );
    }


    @DeleteMapping("/{id}")
    @PreAuthorize(
            "hasRole('ADMIN')"
    )
    public ResponseEntity<ApiResponse<Void>> delete(
            @PathVariable Long id
    ) {

        planService.delete(id);

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Membership plan deactivated successfully",
                        null
                )
        );
    }
}