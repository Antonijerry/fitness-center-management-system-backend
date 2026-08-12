package com.fitnesscenter.membership.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class MembershipExpirationScheduler {

    private final MembershipService membershipService;

    @Scheduled(
            cron = "0 5 0 * * *"
    )
    public void expireMemberships() {

        log.info(
                "Starting membership expiration job"
        );

        membershipService.expireMemberships();

        log.info(
                "Membership expiration job completed"
        );
    }
}