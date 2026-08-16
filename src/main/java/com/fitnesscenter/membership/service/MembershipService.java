package com.fitnesscenter.membership.service;

import com.fitnesscenter.membership.dto.CreateMembershipRequest;
import com.fitnesscenter.membership.dto.MembershipResponse;

import java.util.List;

public interface MembershipService {

    MembershipResponse create(
            CreateMembershipRequest request
    );

    MembershipResponse getById(
            Long id
    );

    List<MembershipResponse> getAll();

    List<MembershipResponse> getByUserId(
            Long userId
    );

    MembershipResponse cancel(
            Long id
    );

    MembershipResponse suspend(
            Long id
    );

    MembershipResponse activate(
            Long id
    );

    void expireMemberships();

    //added during payment...
    void activateMembership(Long membershipId);
}