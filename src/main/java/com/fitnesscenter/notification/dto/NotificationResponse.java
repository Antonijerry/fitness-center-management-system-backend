package com.fitnesscenter.notification.dto;

import com.fitnesscenter.notification.entity.NotificationChannel;
import com.fitnesscenter.notification.entity.NotificationStatus;
import com.fitnesscenter.notification.entity.NotificationType;

import java.time.LocalDateTime;

public record NotificationResponse(

        Long id,

        NotificationType type,

        NotificationChannel channel,

        NotificationStatus status,

        String title,

        String message,

        Long referenceId,

        LocalDateTime createdAt,

        LocalDateTime sentAt,

        LocalDateTime readAt,

        LocalDateTime failedAt
) {
}