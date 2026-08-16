package com.fitnesscenter.membership.repository;

import com.fitnesscenter.membership.entity.Membership;
import com.fitnesscenter.membership.entity.MembershipStatus;
import com.fitnesscenter.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface MembershipRepository
        extends JpaRepository<Membership, Long> {

    /*
     * Find all memberships belonging to a user.
     */
    List<Membership> findAllByUserId(
            Long userId
    );

    /*
     * Find memberships by status.
     */
    List<Membership> findAllByStatus(
            MembershipStatus status
    );

    /*
     * Find the most recent membership for a user.
     *
     * Membership has a 'user' property, not a 'member' property.
     */
    Optional<Membership> findFirstByUserOrderByEndDateDesc(
            User user
    );

    /*
     * Check whether a user has a membership with
     * the specified status.
     */
    boolean existsByUserIdAndStatus(
            Long userId,
            MembershipStatus status
    );

    /*
     * Find memberships that expired before a given date
     * and have the specified status.
     */
    List<Membership> findAllByEndDateBeforeAndStatus(
            LocalDate date,
            MembershipStatus status
    );
}