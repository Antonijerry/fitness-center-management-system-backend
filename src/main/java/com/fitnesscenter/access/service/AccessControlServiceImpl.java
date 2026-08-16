package com.fitnesscenter.access.service;

import com.fitnesscenter.access.dto.AccessValidationResponse;
import com.fitnesscenter.access.entity.AccessDecision;
import com.fitnesscenter.access.entity.AccessDenialReason;
import com.fitnesscenter.member.entity.MemberProfile;
import com.fitnesscenter.member.entity.MemberStatus;
import com.fitnesscenter.member.repository.MemberProfileRepository;
import com.fitnesscenter.membership.entity.Membership;
import com.fitnesscenter.membership.entity.MembershipStatus;
import com.fitnesscenter.membership.repository.MembershipRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AccessControlServiceImpl
        implements AccessControlService {

    private final MemberProfileRepository memberProfileRepository;

    private final MembershipRepository membershipRepository;

    @Override
    public AccessValidationResponse validateAccess(
            Long memberId
    ) {

        /*
         * 1. Find the member profile
         */
        MemberProfile member =
                memberProfileRepository.findById(memberId)
                        .orElse(null);

        if (member == null) {

            return denied(
                    memberId,
                    AccessDenialReason.MEMBER_NOT_FOUND,
                    "Member was not found"
            );
        }

        /*
         * 2. Check whether the member account is active
         */
        if (!isMemberActive(member)) {

            return denied(
                    memberId,
                    AccessDenialReason.MEMBER_INACTIVE,
                    "Member account is inactive"
            );
        }

        /*
         * 3. Find the member's most recent membership
         *
         * Membership is associated with User, not MemberProfile.
         *
         * MemberProfile -> User -> Membership
         */
        Membership membership =
                membershipRepository
                        .findFirstByUserOrderByEndDateDesc(
                                member.getUser()
                        )
                        .orElse(null);

        if (membership == null) {

            return denied(
                    memberId,
                    AccessDenialReason.NO_ACTIVE_MEMBERSHIP,
                    "Member does not have a membership"
            );
        }

        /*
         * 4. Check whether the membership is suspended
         */
        if (membership.getStatus()
                == MembershipStatus.SUSPENDED) {

            return denied(
                    memberId,
                    AccessDenialReason.MEMBERSHIP_SUSPENDED,
                    "Member membership is suspended"
            );
        }

        /*
         * 5. Check whether the membership is cancelled
         */
        if (membership.getStatus()
                == MembershipStatus.CANCELLED) {

            return denied(
                    memberId,
                    AccessDenialReason.NO_ACTIVE_MEMBERSHIP,
                    "Member membership has been cancelled"
            );
        }

        /*
         * 6. Check whether the membership has started
         */
        if (java.time.LocalDate.now()
                .isBefore(membership.getStartDate())) {

            return denied(
                    memberId,
                    AccessDenialReason.NO_ACTIVE_MEMBERSHIP,
                    "Member membership has not started"
            );
        }

        /*
         * 7. Check whether the membership has expired
         */
        if (java.time.LocalDate.now()
                .isAfter(membership.getEndDate())) {

            return denied(
                    memberId,
                    AccessDenialReason.MEMBERSHIP_EXPIRED,
                    "Member membership has expired"
            );
        }

        /*
         * 8. Use Membership's centralized validity check
         *
         * This verifies:
         *
         * - status == ACTIVE
         * - start date has been reached
         * - end date has not passed
         */
        if (!membership.isCurrentlyValid()) {

            return denied(
                    memberId,
                    AccessDenialReason.NO_ACTIVE_MEMBERSHIP,
                    "Member does not have an active membership"
            );
        }

        /*
         * 9. All access requirements have passed
         */
        return new AccessValidationResponse(
                memberId,
                AccessDecision.ALLOWED,
                null,
                "Member access granted"
        );
    }

    /**
     * Determines whether the member account itself is active.
     */
    private boolean isMemberActive(
            MemberProfile member
    ) {

        return member.getStatus()
                == MemberStatus.ACTIVE;
    }

    /**
     * Creates a denied access response.
     */
    private AccessValidationResponse denied(
            Long memberId,
            AccessDenialReason reason,
            String message
    ) {

        return new AccessValidationResponse(
                memberId,
                AccessDecision.DENIED,
                reason,
                message
        );
    }
}