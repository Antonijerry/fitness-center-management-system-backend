package com.fitnesscenter.notification.service;

import com.fitnesscenter.common.exception.ResourceNotFoundException;
import com.fitnesscenter.notification.dto.NotificationPreferenceResponse;
import com.fitnesscenter.notification.dto.NotificationResponse;
import com.fitnesscenter.notification.entity.Notification;
import com.fitnesscenter.notification.entity.NotificationChannel;
import com.fitnesscenter.notification.entity.NotificationPreference;
import com.fitnesscenter.notification.entity.NotificationStatus;
import com.fitnesscenter.notification.entity.NotificationType;
import com.fitnesscenter.notification.repository.NotificationPreferenceRepository;
import com.fitnesscenter.notification.repository.NotificationRepository;
import com.fitnesscenter.user.entity.User;
import com.fitnesscenter.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl
        implements NotificationService {

    private final NotificationRepository notificationRepository;

    private final NotificationPreferenceRepository
            notificationPreferenceRepository;

    private final UserRepository userRepository;

    private final NotificationDeliveryService
            notificationDeliveryService;


    @Override
    @Transactional(readOnly = true)
    public Page<NotificationResponse> getUserNotifications(
            Long userId,
            Pageable pageable
    ) {

        return notificationRepository
                .findByUserIdOrderByCreatedAtDesc(
                        userId,
                        pageable
                )
                .map(this::toResponse);
    }


    @Override
    @Transactional(readOnly = true)
    public long getUnreadCount(
            Long userId
    ) {

        return notificationRepository
                .countByUserIdAndStatus(
                        userId,
                        NotificationStatus.UNREAD
                );
    }


    @Override
    @Transactional
    public NotificationResponse markAsRead(
            Long userId,
            Long notificationId
    ) {

        Notification notification =
                getUserNotification(
                        userId,
                        notificationId
                );

        /*
         * Only IN_APP notifications have
         * READ / UNREAD semantics.
         */
        if (notification.getChannel()
                == NotificationChannel.IN_APP
                && notification.getStatus()
                == NotificationStatus.UNREAD) {

            notification.setStatus(
                    NotificationStatus.READ
            );

            notification.setReadAt(
                    LocalDateTime.now()
            );
        }

        return toResponse(
                notificationRepository.save(
                        notification
                )
        );
    }


    @Override
    @Transactional
    public void markAllAsRead(
            Long userId
    ) {

        List<Notification> notifications =
                notificationRepository
                        .findAllByUserIdAndStatus(
                                userId,
                                NotificationStatus.UNREAD
                        );

        if (notifications.isEmpty()) {
            return;
        }

        LocalDateTime now =
                LocalDateTime.now();

        notifications.forEach(
                notification -> {

                    if (notification.getChannel()
                            == NotificationChannel.IN_APP) {

                        notification.setStatus(
                                NotificationStatus.READ
                        );

                        notification.setReadAt(now);
                    }
                }
        );

        notificationRepository.saveAll(
                notifications
        );
    }


    @Override
    @Transactional
    public void createInAppNotification(
            Long userId,
            NotificationType type,
            String title,
            String message,
            Long referenceId
    ) {

        User user = getUser(userId);

        NotificationPreference preference =
                getPreference(
                        user,
                        type
                );

        if (!preference.isInAppEnabled()) {
            return;
        }

        Notification notification =
                Notification.builder()
                        .user(user)
                        .type(type)
                        .channel(
                                NotificationChannel.IN_APP
                        )
                        .status(
                                NotificationStatus.UNREAD
                        )
                        .title(title)
                        .message(message)
                        .referenceId(referenceId)
                        .retryCount(0)
                        .build();

        notificationRepository.save(
                notification
        );
    }


    @Override
    @Transactional
    public void createNotification(
            Long userId,
            NotificationType type,
            String title,
            String message,
            Long referenceId
    ) {

        User user = getUser(userId);

        NotificationPreference preference =
                getPreference(
                        user,
                        type
                );

        /*
         * Create IN_APP notification.
         */
        if (preference.isInAppEnabled()) {

            Notification inAppNotification =
                    Notification.builder()
                            .user(user)
                            .type(type)
                            .channel(
                                    NotificationChannel.IN_APP
                            )
                            .status(
                                    NotificationStatus.UNREAD
                            )
                            .title(title)
                            .message(message)
                            .referenceId(referenceId)
                            .retryCount(0)
                            .build();

            notificationRepository.save(
                    inAppNotification
            );
        }

        /*
         * Create EMAIL notification.
         */
        if (preference.isEmailEnabled()) {

            Notification emailNotification =
                    Notification.builder()
                            .user(user)
                            .type(type)
                            .channel(
                                    NotificationChannel.EMAIL
                            )
                            .status(
                                    NotificationStatus.PENDING
                            )
                            .title(title)
                            .message(message)
                            .referenceId(referenceId)
                            .retryCount(0)
                            .build();

            Notification savedNotification =
                    notificationRepository.save(
                            emailNotification
                    );

            /*
             * Send asynchronously.
             */
            notificationDeliveryService.sendEmail(
                    savedNotification.getId()
            );
        }
    }


    @Override
    @Transactional
    public List<NotificationPreferenceResponse>
    getPreferences(
            Long userId
    ) {

        User user = getUser(userId);

        initializeMissingPreferences(user);

        return notificationPreferenceRepository
                .findByUserIdOrderByNotificationTypeAsc(
                        userId
                )
                .stream()
                .map(preference ->
                        new NotificationPreferenceResponse(
                                preference.getNotificationType(),
                                preference.isInAppEnabled(),
                                preference.isEmailEnabled()
                        )
                )
                .toList();
    }


    @Override
    @Transactional
    public NotificationPreferenceResponse
    updatePreference(
            Long userId,
            NotificationType type,
            boolean inAppEnabled,
            boolean emailEnabled
    ) {

        User user = getUser(userId);

        NotificationPreference preference =
                notificationPreferenceRepository
                        .findByUserIdAndNotificationType(
                                userId,
                                type
                        )
                        .orElseGet(() ->
                                NotificationPreference.builder()
                                        .user(user)
                                        .notificationType(type)
                                        .inAppEnabled(true)
                                        .emailEnabled(true)
                                        .build()
                        );

        preference.setInAppEnabled(
                inAppEnabled
        );

        preference.setEmailEnabled(
                emailEnabled
        );

        NotificationPreference saved =
                notificationPreferenceRepository.save(
                        preference
                );

        return new NotificationPreferenceResponse(
                saved.getNotificationType(),
                saved.isInAppEnabled(),
                saved.isEmailEnabled()
        );
    }


    private void initializeMissingPreferences(
            User user
    ) {

        Arrays.stream(
                        NotificationType.values()
                )
                .forEach(type -> {

                    boolean exists =
                            notificationPreferenceRepository
                                    .findByUserIdAndNotificationType(
                                            user.getId(),
                                            type
                                    )
                                    .isPresent();

                    if (!exists) {

                        NotificationPreference preference =
                                NotificationPreference.builder()
                                        .user(user)
                                        .notificationType(type)
                                        .inAppEnabled(true)
                                        .emailEnabled(true)
                                        .build();

                        notificationPreferenceRepository.save(
                                preference
                        );
                    }
                });
    }


    private NotificationPreference getPreference(
            User user,
            NotificationType type
    ) {

        return notificationPreferenceRepository
                .findByUserIdAndNotificationType(
                        user.getId(),
                        type
                )
                .orElseGet(() -> {

                    NotificationPreference preference =
                            NotificationPreference.builder()
                                    .user(user)
                                    .notificationType(type)
                                    .inAppEnabled(true)
                                    .emailEnabled(true)
                                    .build();

                    return notificationPreferenceRepository.save(
                            preference
                    );
                });
    }


    private Notification getUserNotification(
            Long userId,
            Long notificationId
    ) {

        Notification notification =
                notificationRepository
                        .findById(notificationId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Notification not found"
                                )
                        );

        if (!notification
                .getUser()
                .getId()
                .equals(userId)) {

            throw new ResourceNotFoundException(
                    "Notification not found"
            );
        }

        return notification;
    }


    private User getUser(
            Long userId
    ) {

        return userRepository
                .findById(userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found with id: "
                                        + userId
                        )
                );
    }


    private NotificationResponse toResponse(
            Notification notification
    ) {

        return new NotificationResponse(
                notification.getId(),
                notification.getType(),
                notification.getChannel(),
                notification.getStatus(),
                notification.getTitle(),
                notification.getMessage(),
                notification.getReferenceId(),
                notification.getCreatedAt(),
                notification.getSentAt(),
                notification.getReadAt(),
                notification.getFailedAt()
        );
    }
}