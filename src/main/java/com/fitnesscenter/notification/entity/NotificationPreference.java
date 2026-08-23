package com.fitnesscenter.notification.entity;

import com.fitnesscenter.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "notification_preferences",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_notification_preference_user_type",
                        columnNames = {
                                "user_id",
                                "notification_type"
                        }
                )
        }
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationPreference {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "user_id",
            nullable = false
    )
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "notification_type",
            nullable = false,
            length = 50
    )
    private NotificationType notificationType;

    @Column(
            name = "in_app_enabled",
            nullable = false
    )
    private boolean inAppEnabled;

    @Column(
            name = "email_enabled",
            nullable = false
    )
    private boolean emailEnabled;
}