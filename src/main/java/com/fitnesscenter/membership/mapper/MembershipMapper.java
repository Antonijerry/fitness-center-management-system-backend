package com.fitnesscenter.membership.mapper;

import com.fitnesscenter.membership.dto.MembershipPlanResponse;
import com.fitnesscenter.membership.dto.MembershipResponse;
import com.fitnesscenter.membership.entity.Membership;
import com.fitnesscenter.membership.entity.MembershipPlan;
import org.springframework.stereotype.Component;

@Component
public class MembershipMapper {

    public MembershipPlanResponse toPlanResponse(
            MembershipPlan plan
    ) {

        return new MembershipPlanResponse(
                plan.getId(),
                plan.getName(),
                plan.getDescription(),
                plan.getType(),
                plan.getPrice(),
                plan.getDurationInDays(),
                plan.getMaxVisitsPerMonth(),
                plan.isActive(),
                plan.isAutoRenewable()
        );
    }


    public MembershipResponse toMembershipResponse(
            Membership membership
    ) {

        return new MembershipResponse(
                membership.getId(),

                membership.getUser().getId(),

                membership.getUser().getFirstName()
                        + " "
                        + membership.getUser().getLastName(),

                membership.getPlan().getId(),

                membership.getPlan().getName(),

                membership.getStartDate(),

                membership.getEndDate(),

                membership.getStatus(),

                membership.getPrice(),

                membership.isAutoRenewable(),

                membership.getNotes()
        );
    }
}