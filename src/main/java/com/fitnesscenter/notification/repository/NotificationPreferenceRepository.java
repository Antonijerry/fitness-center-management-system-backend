package com.fitnesscenter.notification.repository;

import com.fitnesscenter.notification.entity.NotificationPreference;
import com.fitnesscenter.notification.entity.NotificationType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface NotificationPreferenceRepository
        extends JpaRepository<NotificationPreference, Long> {

    Optional<NotificationPreference>
    findByUserIdAndNotificationType(
            Long userId,
            NotificationType notificationType
    );

    List<NotificationPreference>
    findByUserIdOrderByNotificationTypeAsc(
            Long userId
    );
}