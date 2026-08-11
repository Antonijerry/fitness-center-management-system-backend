package com.fitnesscenter.auth.repository;

import com.fitnesscenter.auth.entity.RefreshToken;
import com.fitnesscenter.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RefreshTokenRepository
        extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByTokenHash(
            String tokenHash
    );

    List<RefreshToken> findAllByUserAndRevokedAtIsNull(
            User user
    );

    void deleteAllByUser(User user);
}