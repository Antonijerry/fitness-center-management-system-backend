package com.fitnesscenter.user.dto;

import java.time.Instant;
import java.util.Set;

public record UserResponse(

        Long id,

        String firstName,

        String lastName,

        String email,

        String phone,

        boolean enabled,

        boolean accountNonLocked,

        Set<String> roles,

        Instant createdAt,

        Instant updatedAt
) {
}