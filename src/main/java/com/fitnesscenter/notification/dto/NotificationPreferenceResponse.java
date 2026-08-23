package com.fitnesscenter.notification.dto;

import com.fitnesscenter.notification.entity.NotificationType;

public record NotificationPreferenceResponse(

        NotificationType notificationType,

        boolean inAppEnabled,

        boolean emailEnabled
) {
}