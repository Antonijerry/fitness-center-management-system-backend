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

    List<Membership> findAllByUserId(
            Long userId
    );

    List<Membership> findAllByStatus(
            MembershipStatus status
    );

    Optional<Membership> findFirstByUserOrderByEndDateDesc(
            User user
    );

    boolean existsByUserIdAndStatus(
            Long userId,
            MembershipStatus status
    );

    List<Membership> findAllByEndDateBeforeAndStatus(
            LocalDate date,
            MembershipStatus status
    );
}