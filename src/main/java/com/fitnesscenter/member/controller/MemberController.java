package com.fitnesscenter.member.controller;

import com.fitnesscenter.common.response.ApiResponse;
import com.fitnesscenter.member.dto.CreateMemberProfileRequest;
import com.fitnesscenter.member.dto.MemberProfileResponse;
import com.fitnesscenter.member.dto.UpdateMemberProfileRequest;
import com.fitnesscenter.member.entity.MemberStatus;
import com.fitnesscenter.member.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/members")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;


    @PostMapping
    @PreAuthorize(
            "hasAnyRole('ADMIN', 'MANAGER', 'RECEPTIONIST')"
    )
    public ResponseEntity<ApiResponse<MemberProfileResponse>> create(
            @Valid @RequestBody
            CreateMemberProfileRequest request
    ) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        ApiResponse.success(
                                "Member profile created successfully",
                                memberService.create(request)
                        )
                );
    }


    @GetMapping("/{id}")
    @PreAuthorize(
            "hasAnyRole('ADMIN', 'MANAGER', 'RECEPTIONIST')"
    )
    public ResponseEntity<ApiResponse<MemberProfileResponse>> getById(
            @PathVariable Long id
    ) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Member profile retrieved successfully",
                        memberService.getById(id)
                )
        );
    }


    @GetMapping("/user/{userId}")
    @PreAuthorize(
            "hasAnyRole('ADMIN', 'MANAGER', 'RECEPTIONIST')"
    )
    public ResponseEntity<ApiResponse<MemberProfileResponse>> getByUserId(
            @PathVariable Long userId
    ) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Member profile retrieved successfully",
                        memberService.getByUserId(userId)
                )
        );
    }


    @GetMapping("/number/{memberNumber}")
    @PreAuthorize(
            "hasAnyRole('ADMIN', 'MANAGER', 'RECEPTIONIST')"
    )
    public ResponseEntity<ApiResponse<MemberProfileResponse>> getByMemberNumber(
            @PathVariable String memberNumber
    ) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Member profile retrieved successfully",
                        memberService.getByMemberNumber(memberNumber)
                )
        );
    }


    @GetMapping
    @PreAuthorize(
            "hasAnyRole('ADMIN', 'MANAGER', 'RECEPTIONIST')"
    )
    public ResponseEntity<ApiResponse<List<MemberProfileResponse>>> getAll() {

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Members retrieved successfully",
                        memberService.getAll()
                )
        );
    }


    @GetMapping("/status/{status}")
    @PreAuthorize(
            "hasAnyRole('ADMIN', 'MANAGER', 'RECEPTIONIST')"
    )
    public ResponseEntity<ApiResponse<List<MemberProfileResponse>>> getByStatus(
            @PathVariable MemberStatus status
    ) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Members retrieved successfully",
                        memberService.getByStatus(status)
                )
        );
    }


    @GetMapping("/search")
    @PreAuthorize(
            "hasAnyRole('ADMIN', 'MANAGER', 'RECEPTIONIST')"
    )
    public ResponseEntity<ApiResponse<List<MemberProfileResponse>>> search(
            @RequestParam String query
    ) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Member search completed successfully",
                        memberService.search(query)
                )
        );
    }


    @PutMapping("/{id}")
    @PreAuthorize(
            "hasAnyRole('ADMIN', 'MANAGER', 'RECEPTIONIST')"
    )
    public ResponseEntity<ApiResponse<MemberProfileResponse>> update(
            @PathVariable Long id,
            @Valid @RequestBody
            UpdateMemberProfileRequest request
    ) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Member profile updated successfully",
                        memberService.update(
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
    public ResponseEntity<ApiResponse<MemberProfileResponse>> updateStatus(
            @PathVariable Long id,
            @RequestParam MemberStatus status
    ) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Member status updated successfully",
                        memberService.updateStatus(
                                id,
                                status
                        )
                )
        );
    }
}