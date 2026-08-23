package com.fitnesscenter.notification.event;

import com.fitnesscenter.notification.entity.NotificationType;

public record NotificationEvent(

        Long userId,

        NotificationType type,

        String title,

        String message,

        Long referenceId
) {
}