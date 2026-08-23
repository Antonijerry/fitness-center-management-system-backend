package com.fitnesscenter.notification.sender;

import com.fitnesscenter.notification.entity.Notification;

public interface NotificationSender {

    void send(Notification notification);
}