package com.fitnesscenter.membership.service;

import com.fitnesscenter.membership.dto.CreateMembershipPlanRequest;
import com.fitnesscenter.membership.dto.MembershipPlanResponse;
import com.fitnesscenter.membership.dto.UpdateMembershipPlanRequest;

import java.util.List;

public interface MembershipPlanService {

    MembershipPlanResponse create(
            CreateMembershipPlanRequest request
    );

    MembershipPlanResponse update(
            Long id,
            UpdateMembershipPlanRequest request
    );

    MembershipPlanResponse getById(
            Long id
    );

    List<MembershipPlanResponse> getAll();

    List<MembershipPlanResponse> getActive();

    void delete(
            Long id
    );
}