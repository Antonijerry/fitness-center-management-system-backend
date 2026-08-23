package com.fitnesscenter.notification.sender;

import com.fitnesscenter.notification.entity.Notification;
import com.fitnesscenter.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
@RequiredArgsConstructor
public class EmailNotificationSender
        implements NotificationSender {

    private final JavaMailSender mailSender;


    @Override
    public void send(
            Notification notification
    ) {

        if (notification == null) {

            throw new IllegalArgumentException(
                    "Notification cannot be null"
            );
        }

        User user =
                notification.getUser();

        if (user == null) {

            throw new IllegalStateException(
                    "Notification does not have an associated user"
            );
        }

        String email =
                user.getEmail();

        if (!StringUtils.hasText(email)) {

            throw new IllegalStateException(
                    "User email address is missing"
            );
        }

        SimpleMailMessage message =
                new SimpleMailMessage();

        message.setTo(email);

        message.setSubject(
                notification.getTitle()
        );

        message.setText(
                notification.getMessage()
        );

        mailSender.send(message);
    }
}