package com.fitnesscenter.notification.event;

import com.fitnesscenter.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NotificationEventListener {

    private final NotificationService notificationService;

    @EventListener
    public void handleNotificationEvent(
            NotificationEvent event
    ) {

        notificationService.createNotification(
                event.userId(),
                event.type(),
                event.title(),
                event.message(),
                event.referenceId()
        );
    }
}