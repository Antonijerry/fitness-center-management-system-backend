package com.fitnesscenter.auth.service;

import com.fitnesscenter.auth.dto.*;

public interface AuthService {

    AuthResponse register(
            RegisterRequest request
    );

    AuthResponse login(
            LoginRequest request
    );

    AuthResponse refreshToken(
            RefreshTokenRequest request
    );

    void logout(
            LogoutRequest request
    );
}