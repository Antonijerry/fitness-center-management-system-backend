package com.fitnesscenter.notification.repository;

import com.fitnesscenter.notification.entity.Notification;
import com.fitnesscenter.notification.entity.NotificationChannel;
import com.fitnesscenter.notification.entity.NotificationStatus;
import com.fitnesscenter.notification.entity.NotificationType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository
        extends JpaRepository<Notification, Long> {

    Page<Notification>
    findByUserIdOrderByCreatedAtDesc(
            Long userId,
            Pageable pageable
    );

    long countByUserIdAndStatus(
            Long userId,
            NotificationStatus status
    );

    List<Notification>
    findAllByUserIdAndStatus(
            Long userId,
            NotificationStatus status
    );

    List<Notification>
    findTop100ByStatusAndRetryCountLessThan(
            NotificationStatus status,
            Integer retryCount
    );

    boolean existsByUserIdAndTypeAndReferenceId(
            Long userId,
            NotificationType type,
            Long referenceId
    );

    List<Notification>
    findTop100ByStatusAndChannelAndRetryCountLessThanOrderByCreatedAtAsc(
            NotificationStatus status,
            NotificationChannel channel,
            Integer retryCount
    );
}