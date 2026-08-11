package com.fitnesscenter.user.dto;

import jakarta.validation.constraints.NotBlank;

public record AssignRoleRequest(

        @NotBlank(message = "Role name is required")
        String roleName
) {
}