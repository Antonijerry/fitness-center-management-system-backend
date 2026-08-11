package com.fitnesscenter.auth.service;

import com.fitnesscenter.auth.entity.RefreshToken;
import com.fitnesscenter.user.entity.User;

public interface RefreshTokenService {

    RefreshTokenResult createToken(User user);

    RefreshToken validateToken(String rawToken);

    void revokeToken(String rawToken);

    void revokeAllUserTokens(User user);
}