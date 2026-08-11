package com.fitnesscenter.user.mapper;

import com.fitnesscenter.user.dto.UserResponse;
import com.fitnesscenter.user.entity.Role;
import com.fitnesscenter.user.entity.User;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class UserMapper {

    public UserResponse toResponse(User user) {

        Set<String> roles = user.getRoles() == null
                ? Collections.emptySet()
                : user.getRoles()
                .stream()
                .map(Role::getName)
                .collect(Collectors.toSet());

        return new UserResponse(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getPhone(),
                user.isEnabled(),
                user.isAccountNonLocked(),
                roles,
                user.getCreatedAt(),
                user.getUpdatedAt()
        );
    }
}