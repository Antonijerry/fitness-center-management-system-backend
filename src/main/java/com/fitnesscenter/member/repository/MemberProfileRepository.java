package com.fitnesscenter.member.repository;

import com.fitnesscenter.member.entity.MemberProfile;
import com.fitnesscenter.member.entity.MemberStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MemberProfileRepository
        extends JpaRepository<MemberProfile, Long> {

    Optional<MemberProfile> findByUserId(
            Long userId
    );

    Optional<MemberProfile> findByMemberNumber(
            String memberNumber
    );

    boolean existsByUserId(
            Long userId
    );

    boolean existsByMemberNumber(
            String memberNumber
    );

    List<MemberProfile> findAllByStatus(
            MemberStatus status
    );

    List<MemberProfile> findByMemberNumberContainingIgnoreCase(
            String memberNumber
    );

    List<MemberProfile> findByPhoneContaining(
            String phone
    );

    //added for dashboard package
    long countByStatus(MemberStatus status);

    //
    List<MemberProfile> findByUserFirstNameContainingIgnoreCaseOrUserLastNameContainingIgnoreCaseOrUserEmailContainingIgnoreCase(
            String firstName, String lastName, String email
    );
}