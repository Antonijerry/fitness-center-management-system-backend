package com.fitnesscenter.notification.scheduler;

import com.fitnesscenter.notification.entity.Notification;
import com.fitnesscenter.notification.entity.NotificationChannel;
import com.fitnesscenter.notification.entity.NotificationStatus;
import com.fitnesscenter.notification.repository.NotificationRepository;
import com.fitnesscenter.notification.service.NotificationDeliveryService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class NotificationRetryScheduler {

    private static final int MAX_RETRIES = 3;

    private final NotificationRepository notificationRepository;

    private final NotificationDeliveryService
            notificationDeliveryService;


    /*
     * Retry failed email notifications every 5 minutes.
     *
     * fixedDelay means the next execution starts
     * 5 minutes after the previous execution finishes.
     */
    @Scheduled(
            fixedDelay = 300000
    )
    public void retryFailedNotifications() {

        List<Notification> notifications =
                notificationRepository
                        .findTop100ByStatusAndChannelAndRetryCountLessThanOrderByCreatedAtAsc(
                                NotificationStatus.FAILED,
                                NotificationChannel.EMAIL,
                                MAX_RETRIES
                        );

        notifications.forEach(
                notification ->
                        notificationDeliveryService
                                .sendEmail(
                                        notification.getId()
                                )
        );
    }
}