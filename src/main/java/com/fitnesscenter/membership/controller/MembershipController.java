package com.fitnesscenter.membership.controller;

import com.fitnesscenter.common.response.ApiResponse;
import com.fitnesscenter.membership.dto.CreateMembershipRequest;
import com.fitnesscenter.membership.dto.MembershipResponse;
import com.fitnesscenter.membership.service.MembershipService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/memberships")
@RequiredArgsConstructor
public class MembershipController {

    private final MembershipService membershipService;


    @PostMapping
    @PreAuthorize(
            "hasAnyRole('ADMIN', 'MANAGER', 'RECEPTIONIST')"
    )
    public ResponseEntity<ApiResponse<MembershipResponse>> create(
            @Valid @RequestBody
            CreateMembershipRequest request
    ) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        ApiResponse.success(
                                "Membership created successfully",
                                membershipService.create(request)
                        )
                );
    }


    @GetMapping("/{id}")
    @PreAuthorize(
            "hasAnyRole('ADMIN', 'MANAGER', 'RECEPTIONIST')"
    )
    public ResponseEntity<ApiResponse<MembershipResponse>> getById(
            @PathVariable Long id
    ) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Membership retrieved successfully",
                        membershipService.getById(id)
                )
        );
    }


    @GetMapping
    @PreAuthorize(
            "hasAnyRole('ADMIN', 'MANAGER', 'RECEPTIONIST')"
    )
    public ResponseEntity<ApiResponse<List<MembershipResponse>>> getAll() {

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Memberships retrieved successfully",
                        membershipService.getAll()
                )
        );
    }


    @GetMapping("/user/{userId}")
    @PreAuthorize(
            "hasAnyRole('ADMIN', 'MANAGER', 'RECEPTIONIST')"
    )
    public ResponseEntity<ApiResponse<List<MembershipResponse>>> getByUser(
            @PathVariable Long userId
    ) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        "User memberships retrieved successfully",
                        membershipService.getByUserId(userId)
                )
        );
    }


    @PatchMapping("/{id}/activate")
    @PreAuthorize(
            "hasAnyRole('ADMIN', 'MANAGER', 'RECEPTIONIST')"
    )
    public ResponseEntity<ApiResponse<MembershipResponse>> activate(
            @PathVariable Long id
    ) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Membership activated successfully",
                        membershipService.activate(id)
                )
        );
    }


    @PatchMapping("/{id}/suspend")
    @PreAuthorize(
            "hasAnyRole('ADMIN', 'MANAGER')"
    )
    public ResponseEntity<ApiResponse<MembershipResponse>> suspend(
            @PathVariable Long id
    ) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Membership suspended successfully",
                        membershipService.suspend(id)
                )
        );
    }


    @PatchMapping("/{id}/cancel")
    @PreAuthorize(
            "hasAnyRole('ADMIN', 'MANAGER')"
    )
    public ResponseEntity<ApiResponse<MembershipResponse>> cancel(
            @PathVariable Long id
    ) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Membership cancelled successfully",
                        membershipService.cancel(id)
                )
        );
    }
}