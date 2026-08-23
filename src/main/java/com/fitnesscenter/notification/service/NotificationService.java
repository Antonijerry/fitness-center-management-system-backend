package com.fitnesscenter.notification.service;

import com.fitnesscenter.notification.dto.NotificationPreferenceResponse;
import com.fitnesscenter.notification.dto.NotificationResponse;
import com.fitnesscenter.notification.entity.NotificationType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface NotificationService {

    Page<NotificationResponse> getUserNotifications(
            Long userId,
            Pageable pageable
    );

    long getUnreadCount(
            Long userId
    );

    NotificationResponse markAsRead(
            Long userId,
            Long notificationId
    );

    void markAllAsRead(
            Long userId
    );

    void createInAppNotification(
            Long userId,
            NotificationType type,
            String title,
            String message,
            Long referenceId
    );

    void createNotification(
            Long userId,
            NotificationType type,
            String title,
            String message,
            Long referenceId
    );

    List<NotificationPreferenceResponse> getPreferences(
            Long userId
    );

    NotificationPreferenceResponse updatePreference(
            Long userId,
            NotificationType type,
            boolean inAppEnabled,
            boolean emailEnabled
    );
}