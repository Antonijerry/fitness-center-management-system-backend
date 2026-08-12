package com.fitnesscenter.membership.repository;

import com.fitnesscenter.membership.entity.MembershipPlan;
import com.fitnesscenter.membership.entity.MembershipType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MembershipPlanRepository
        extends JpaRepository<MembershipPlan, Long> {

    Optional<MembershipPlan> findByNameIgnoreCase(
            String name
    );

    boolean existsByNameIgnoreCase(
            String name
    );

    List<MembershipPlan> findAllByActiveTrue();

    List<MembershipPlan> findAllByType(
            MembershipType type
    );
}