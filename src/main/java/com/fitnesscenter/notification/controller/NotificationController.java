package com.fitnesscenter.notification.controller;

import com.fitnesscenter.common.response.ApiResponse;
import com.fitnesscenter.notification.dto.NotificationPreferenceResponse;
import com.fitnesscenter.notification.dto.NotificationResponse;
import com.fitnesscenter.notification.dto.UnreadNotificationCountResponse;
import com.fitnesscenter.notification.dto.UpdateNotificationPreferenceRequest;
import com.fitnesscenter.notification.entity.NotificationType;
import com.fitnesscenter.notification.service.NotificationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;


    /*
     * ---------------------------------------------------------
     * GET USER NOTIFICATIONS
     * ---------------------------------------------------------
     *
     * GET /api/v1/notifications
     *
     * Optional pagination:
     *
     * ?page=0&size=20&sort=createdAt,desc
     */
    @GetMapping
    public ResponseEntity<
            ApiResponse<Page<NotificationResponse>>
            > getNotifications(
            Authentication authentication,
            Pageable pageable
    ) {

        Long userId =
                getAuthenticatedUserId(
                        authentication
                );

        Page<NotificationResponse> notifications =
                notificationService.getUserNotifications(
                        userId,
                        pageable
                );

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Notifications retrieved successfully",
                        notifications
                )
        );
    }


    /*
     * ---------------------------------------------------------
     * GET UNREAD NOTIFICATION COUNT
     * ---------------------------------------------------------
     *
     * GET /api/v1/notifications/unread/count
     */
    @GetMapping("/unread/count")
    public ResponseEntity<
            ApiResponse<UnreadNotificationCountResponse>
            > getUnreadCount(
            Authentication authentication
    ) {

        Long userId =
                getAuthenticatedUserId(
                        authentication
                );

        long count =
                notificationService.getUnreadCount(
                        userId
                );

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Unread notification count retrieved successfully",
                        new UnreadNotificationCountResponse(
                                count
                        )
                )
        );
    }


    /*
     * ---------------------------------------------------------
     * MARK ONE NOTIFICATION AS READ
     * ---------------------------------------------------------
     *
     * PATCH /api/v1/notifications/{notificationId}/read
     */
    @PatchMapping("/{notificationId}/read")
    public ResponseEntity<
            ApiResponse<NotificationResponse>
            > markAsRead(
            Authentication authentication,
            @PathVariable Long notificationId
    ) {

        Long userId =
                getAuthenticatedUserId(
                        authentication
                );

        NotificationResponse response =
                notificationService.markAsRead(
                        userId,
                        notificationId
                );

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Notification marked as read",
                        response
                )
        );
    }


    /*
     * ---------------------------------------------------------
     * MARK ALL NOTIFICATIONS AS READ
     * ---------------------------------------------------------
     *
     * PATCH /api/v1/notifications/read-all
     */
    @PatchMapping("/read-all")
    public ResponseEntity<
            ApiResponse<Void>
            > markAllAsRead(
            Authentication authentication
    ) {

        Long userId =
                getAuthenticatedUserId(
                        authentication
                );

        notificationService.markAllAsRead(
                userId
        );

        return ResponseEntity.ok(
                ApiResponse.success(
                        "All notifications marked as read",
                        null
                )
        );
    }


    /*
     * ---------------------------------------------------------
     * GET NOTIFICATION PREFERENCES
     * ---------------------------------------------------------
     *
     * GET /api/v1/notifications/preferences
     */
    @GetMapping("/preferences")
    public ResponseEntity<
            ApiResponse<List<NotificationPreferenceResponse>>
            > getPreferences(
            Authentication authentication
    ) {

        Long userId =
                getAuthenticatedUserId(
                        authentication
                );

        List<NotificationPreferenceResponse> preferences =
                notificationService.getPreferences(
                        userId
                );

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Notification preferences retrieved successfully",
                        preferences
                )
        );
    }


    /*
     * ---------------------------------------------------------
     * UPDATE NOTIFICATION PREFERENCE
     * ---------------------------------------------------------
     *
     * PUT /api/v1/notifications/preferences/{type}
     *
     * Example:
     *
     * PUT /api/v1/notifications/preferences/PAYMENT_SUCCESSFUL
     */
    @PutMapping("/preferences/{type}")
    public ResponseEntity<
            ApiResponse<NotificationPreferenceResponse>
            > updatePreference(
            Authentication authentication,

            @PathVariable
            NotificationType type,

            @Valid
            @RequestBody
            UpdateNotificationPreferenceRequest request
    ) {

        Long userId =
                getAuthenticatedUserId(
                        authentication
                );

        NotificationPreferenceResponse response =
                notificationService.updatePreference(
                        userId,
                        type,
                        request.inAppEnabled(),
                        request.emailEnabled()
                );

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Notification preference updated successfully",
                        response
                )
        );
    }


    /*
     * ---------------------------------------------------------
     * AUTHENTICATED USER ID
     * ---------------------------------------------------------
     *
     * Your JWT authentication must place the user's
     * database ID in authentication.getName().
     */
    private Long getAuthenticatedUserId(
            Authentication authentication
    ) {

        if (authentication == null
                || !authentication.isAuthenticated()) {

            throw new IllegalStateException(
                    "User is not authenticated"
            );
        }

        try {

            return Long.valueOf(
                    authentication.getName()
            );

        } catch (NumberFormatException exception) {

            throw new IllegalStateException(
                    "Authenticated user ID is invalid"
            );
        }
    }
}