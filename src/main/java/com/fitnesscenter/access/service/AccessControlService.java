package com.fitnesscenter.access.service;

import com.fitnesscenter.access.dto.AccessValidationResponse;

public interface AccessControlService {

    AccessValidationResponse validateAccess(
            Long memberId
    );
}