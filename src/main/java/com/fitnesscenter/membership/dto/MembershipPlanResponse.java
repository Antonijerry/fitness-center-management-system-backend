package com.fitnesscenter.membership.dto;

import com.fitnesscenter.membership.entity.MembershipType;

import java.math.BigDecimal;

public record MembershipPlanResponse(

        Long id,

        String name,

        String description,

        MembershipType type,

        BigDecimal price,

        Integer durationInDays,

        Integer maxVisitsPerMonth,

        boolean active,

        boolean autoRenewable
) {
}