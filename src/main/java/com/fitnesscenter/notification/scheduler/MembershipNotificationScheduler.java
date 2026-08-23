package com.fitnesscenter.notification.scheduler;

import com.fitnesscenter.membership.entity.Membership;
import com.fitnesscenter.membership.entity.MembershipStatus;
import com.fitnesscenter.membership.repository.MembershipRepository;
import com.fitnesscenter.notification.entity.NotificationType;
import com.fitnesscenter.notification.repository.NotificationRepository;
import com.fitnesscenter.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Component
@RequiredArgsConstructor
public class MembershipNotificationScheduler {

    private static final int EXPIRATION_WARNING_DAYS = 7;

    private final MembershipRepository membershipRepository;

    private final NotificationRepository notificationRepository;

    private final NotificationService notificationService;


    /*
     * Runs every day at 8:00 AM.
     *
     * The server's configured timezone is used.
     */
    @Scheduled(
            cron = "0 0 8 * * *"
    )
    @Transactional
    public void processMembershipNotifications() {

        sendExpirationNotifications();

        processExpiredMemberships();
    }


    /*
     * Send membership-expiring notifications.
     *
     * Example:
     *
     * Today: 17 August
     *
     * Membership expires:
     * 24 August
     *
     * The member receives an expiration warning.
     */
    private void sendExpirationNotifications() {

        LocalDate today =
                LocalDate.now();

        LocalDate expirationDate =
                today.plusDays(
                        EXPIRATION_WARNING_DAYS
                );

        List<Membership> memberships =
                membershipRepository
                        .findAllByStatusAndEndDateBetween(
                                MembershipStatus.ACTIVE,
                                today,
                                expirationDate
                        );

        for (Membership membership : memberships) {

            sendExpiringNotification(
                    membership
            );
        }
    }


    /*
     * Send notification for an individual
     * membership that is approaching expiration.
     */
    private void sendExpiringNotification(
            Membership membership
    ) {

        Long userId =
                membership
                        .getUser()
                        .getId();

        Long membershipId =
                membership.getId();

        boolean alreadySent =
                notificationRepository
                        .existsByUserIdAndTypeAndReferenceId(
                                userId,
                                NotificationType.MEMBERSHIP_EXPIRING,
                                membershipId
                        );

        if (alreadySent) {
            return;
        }

        String title =
                "Membership Expiring Soon";

        String message =
                "Your fitness center membership "
                        + "will expire on "
                        + membership.getEndDate()
                        + ". Please renew your membership "
                        + "to continue enjoying uninterrupted access.";

        notificationService.createNotification(
                userId,
                NotificationType.MEMBERSHIP_EXPIRING,
                title,
                message,
                membershipId
        );
    }


    /*
     * Process memberships whose expiration date
     * has already passed.
     */
    private void processExpiredMemberships() {

        LocalDate today =
                LocalDate.now();

        List<Membership> expiredMemberships =
                membershipRepository
                        .findAllByEndDateBeforeAndStatus(
                                today,
                                MembershipStatus.ACTIVE
                        );

        for (Membership membership : expiredMemberships) {

            processExpiredMembership(
                    membership
            );
        }
    }


    /*
     * Mark the membership as expired and send
     * an expiration notification.
     */
    private void processExpiredMembership(
            Membership membership
    ) {

        Long userId =
                membership
                        .getUser()
                        .getId();

        Long membershipId =
                membership.getId();

        /*
         * Change membership status.
         */
        membership.setStatus(
                MembershipStatus.EXPIRED
        );

        membershipRepository.save(
                membership
        );


        /*
         * Prevent duplicate expiration
         * notifications.
         */
        boolean alreadySent =
                notificationRepository
                        .existsByUserIdAndTypeAndReferenceId(
                                userId,
                                NotificationType.MEMBERSHIP_EXPIRED,
                                membershipId
                        );

        if (alreadySent) {
            return;
        }


        String title =
                "Membership Expired";

        String message =
                "Your fitness center membership "
                        + "expired on "
                        + membership.getEndDate()
                        + ". Please renew your membership "
                        + "to regain access to the fitness center.";


        notificationService.createNotification(
                userId,
                NotificationType.MEMBERSHIP_EXPIRED,
                title,
                message,
                membershipId
        );
    }
}