package com.fitnesscenter.notification.entity;

import com.fitnesscenter.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "notifications",
        indexes = {
                @Index(
                        name = "idx_notification_user",
                        columnList = "user_id"
                ),
                @Index(
                        name = "idx_notification_status",
                        columnList = "status"
                ),
                @Index(
                        name = "idx_notification_created_at",
                        columnList = "created_at"
                ),
                @Index(
                        name = "idx_notification_type",
                        columnList = "type"
                ),
                @Index(
                        name = "idx_notification_user_status",
                        columnList = "user_id,status"
                )
        }
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(
            fetch = FetchType.LAZY,
            optional = false
    )
    @JoinColumn(
            name = "user_id",
            nullable = false,
            foreignKey = @ForeignKey(
                    name = "fk_notifications_user"
            )
    )
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(
            nullable = false,
            length = 50
    )
    private NotificationType type;

    @Enumerated(EnumType.STRING)
    @Column(
            nullable = false,
            length = 20
    )
    private NotificationChannel channel;

    @Enumerated(EnumType.STRING)
    @Column(
            nullable = false,
            length = 20
    )
    private NotificationStatus status;

    @Column(
            nullable = false,
            length = 255
    )
    private String title;

    @Column(
            nullable = false,
            columnDefinition = "TEXT"
    )
    private String message;

    @Column(
            name = "reference_id"
    )
    private Long referenceId;

    @Column(
            name = "sent_at"
    )
    private LocalDateTime sentAt;

    @Column(
            name = "failed_at"
    )
    private LocalDateTime failedAt;

    @Column(
            name = "failure_reason",
            columnDefinition = "TEXT"
    )
    private String failureReason;

    @Column(
            name = "retry_count",
            nullable = false
    )
    @Builder.Default
    private Integer retryCount = 0;

    @Column(
            name = "created_at",
            nullable = false,
            updatable = false
    )
    private LocalDateTime createdAt;

    @Column(
            name = "read_at"
    )
    private LocalDateTime readAt;

    @PrePersist
    protected void onCreate() {

        LocalDateTime now = LocalDateTime.now();

        createdAt = now;

        if (retryCount == null) {
            retryCount = 0;
        }
    }
}