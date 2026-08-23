package com.fitnesscenter.notification.dto;

import jakarta.validation.constraints.NotNull;

public record UpdateNotificationPreferenceRequest(

        @NotNull(message = "In-app enabled is required")
        Boolean inAppEnabled,

        @NotNull(message = "Email enabled is required")
        Boolean emailEnabled
) {
}