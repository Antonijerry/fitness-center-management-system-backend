package com.fitnesscenter.notification.service;

import com.fitnesscenter.notification.entity.Notification;
import com.fitnesscenter.notification.entity.NotificationChannel;
import com.fitnesscenter.notification.entity.NotificationStatus;
import com.fitnesscenter.notification.repository.NotificationRepository;
import com.fitnesscenter.notification.sender.NotificationSender;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class NotificationDeliveryService {

    private final NotificationRepository notificationRepository;

    private final NotificationSender notificationSender;


    @Async("notificationTaskExecutor")
    @Transactional
    public void sendEmail(
            Long notificationId
    ) {

        Notification notification =
                notificationRepository
                        .findById(notificationId)
                        .orElse(null);

        if (notification == null) {
            return;
        }

        /*
         * Make sure this method is only used
         * for EMAIL notifications.
         */
        if (notification.getChannel()
                != NotificationChannel.EMAIL) {

            return;
        }

        try {

            notification.setStatus(
                    NotificationStatus.PENDING
            );

            notification.setFailedAt(null);
            notification.setFailureReason(null);

            notificationRepository.save(
                    notification
            );

            /*
             * Actual email delivery.
             */
            notificationSender.send(
                    notification
            );

            /*
             * Email successfully sent.
             */
            notification.setStatus(
                    NotificationStatus.SENT
            );

            notification.setSentAt(
                    LocalDateTime.now()
            );

            notificationRepository.save(
                    notification
            );

        } catch (Exception exception) {

            notification.setStatus(
                    NotificationStatus.FAILED
            );

            notification.setFailedAt(
                    LocalDateTime.now()
            );

            String reason =
                    exception.getMessage();

            notification.setFailureReason(
                    reason != null
                            ? reason
                            : "Unknown email delivery error"
            );

            Integer retryCount =
                    notification.getRetryCount();

            notification.setRetryCount(
                    retryCount == null
                            ? 1
                            : retryCount + 1
            );

            notificationRepository.save(
                    notification
            );
        }
    }
}