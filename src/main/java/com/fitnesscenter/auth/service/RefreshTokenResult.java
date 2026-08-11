package com.fitnesscenter.auth.service;

import com.fitnesscenter.auth.entity.RefreshToken;

public record RefreshTokenResult(

        String rawToken,

        RefreshToken entity
) {
}