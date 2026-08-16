package com.fitnesscenter.access.dto;

import com.fitnesscenter.access.entity.AccessDecision;
import com.fitnesscenter.access.entity.AccessDenialReason;

public record AccessValidationResponse(

        Long memberId,

        AccessDecision decision,

        AccessDenialReason denialReason,

        String message
) {

    public boolean isAllowed() {
        return decision == AccessDecision.ALLOWED;
    }
}