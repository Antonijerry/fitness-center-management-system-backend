package com.fitnesscenter.membership.dto;

import com.fitnesscenter.membership.entity.MembershipStatus;

import java.math.BigDecimal;
import java.time.LocalDate;

public record MembershipResponse(

        Long id,

        Long userId,

        String userName,

        Long planId,

        String planName,

        LocalDate startDate,

        LocalDate endDate,

        MembershipStatus status,

        BigDecimal price,

        boolean autoRenewable,

        String notes
) {
}