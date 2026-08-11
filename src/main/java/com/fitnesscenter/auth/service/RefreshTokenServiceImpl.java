package com.fitnesscenter.auth.service;

import com.fitnesscenter.auth.entity.RefreshToken;
import com.fitnesscenter.auth.repository.RefreshTokenRepository;
import com.fitnesscenter.auth.security.JwtProperties;
import com.fitnesscenter.common.exception.UnauthorizedException;
import com.fitnesscenter.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.Base64;
import java.util.HexFormat;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class RefreshTokenServiceImpl
        implements RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    private final JwtProperties jwtProperties;


    @Override
    public RefreshTokenResult createToken(
            User user
    ) {

        String rawToken =
                generateSecureToken();

        RefreshToken refreshToken =
                new RefreshToken();

        refreshToken.setUser(user);

        refreshToken.setTokenHash(
                hashToken(rawToken)
        );

        refreshToken.setExpiresAt(
                Instant.now()
                        .plusMillis(
                                jwtProperties.refreshTokenExpiration()
                        )
        );

        RefreshToken saved =
                refreshTokenRepository.save(
                        refreshToken
                );

        return new RefreshTokenResult(
                rawToken,
                saved
        );
    }

    @Override
    @Transactional(readOnly = true)
    public RefreshToken validateToken(
            String rawToken
    ) {

        String hash =
                hashToken(rawToken);

        RefreshToken refreshToken =
                refreshTokenRepository
                        .findByTokenHash(hash)
                        .orElseThrow(() ->
                                new UnauthorizedException(
                                        "Invalid refresh token"
                                )
                        );

        if (!refreshToken.isActive()) {

            throw new UnauthorizedException(
                    "Refresh token is expired or revoked"
            );
        }

        return refreshToken;
    }


    @Override
    public void revokeToken(
            String rawToken
    ) {

        String hash =
                hashToken(rawToken);

        refreshTokenRepository
                .findByTokenHash(hash)
                .ifPresent(token ->
                        token.setRevokedAt(
                                Instant.now()
                        )
                );
    }


    @Override
    public void revokeAllUserTokens(
            User user
    ) {

        refreshTokenRepository
                .findAllByUserAndRevokedAtIsNull(user)
                .forEach(token ->
                        token.setRevokedAt(
                                Instant.now()
                        )
                );
    }


    private String generateSecureToken() {

        return Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(
                        UUID.randomUUID()
                                .toString()
                                .getBytes(
                                        StandardCharsets.UTF_8
                                )
                );
    }


    private String hashToken(
            String token
    ) {

        try {

            MessageDigest digest =
                    MessageDigest.getInstance(
                            "SHA-256"
                    );

            byte[] hash =
                    digest.digest(
                            token.getBytes(
                                    StandardCharsets.UTF_8
                            )
                    );

            return HexFormat.of()
                    .formatHex(hash);

        } catch (NoSuchAlgorithmException exception) {

            throw new IllegalStateException(
                    "SHA-256 algorithm is unavailable",
                    exception
            );
        }
    }
}