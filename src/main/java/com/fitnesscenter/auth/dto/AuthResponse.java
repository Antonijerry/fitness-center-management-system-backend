package com.fitnesscenter.auth.dto;

import com.fitnesscenter.user.dto.UserResponse;

public record AuthResponse(

        String accessToken,

        String refreshToken,

        String tokenType,

        long expiresIn,

        UserResponse user
) {
}