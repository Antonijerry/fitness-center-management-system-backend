package com.fitnesscenter.access.controller;

import com.fitnesscenter.access.dto.AccessValidationResponse;
import com.fitnesscenter.access.service.AccessControlService;
import com.fitnesscenter.common.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/access")
@RequiredArgsConstructor
public class AccessControlController {

    private final AccessControlService accessControlService;

    /**
     * Validate whether a member is allowed to access
     * the fitness center.
     *
     * GET /api/v1/access/members/{memberId}/validate
     */
    @GetMapping("/members/{memberId}/validate")
    public ResponseEntity<
            ApiResponse<AccessValidationResponse>
            > validateAccess(

            @PathVariable Long memberId
    ) {

        AccessValidationResponse response =
                accessControlService.validateAccess(
                        memberId
                );

        return ResponseEntity.ok(
                ApiResponse.success(
                        response.isAllowed()
                                ? "Member access granted"
                                : "Member access denied",
                        response
                )
        );
    }
}