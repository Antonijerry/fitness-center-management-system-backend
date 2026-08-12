package com.fitnesscenter.member.service;

import com.fitnesscenter.member.dto.CreateMemberProfileRequest;
import com.fitnesscenter.member.dto.MemberProfileResponse;
import com.fitnesscenter.member.dto.UpdateMemberProfileRequest;
import com.fitnesscenter.member.entity.MemberStatus;

import java.util.List;

public interface MemberService {

    MemberProfileResponse create(
            CreateMemberProfileRequest request
    );

    MemberProfileResponse getById(
            Long id
    );

    MemberProfileResponse getByUserId(
            Long userId
    );

    MemberProfileResponse getByMemberNumber(
            String memberNumber
    );

    List<MemberProfileResponse> getAll();

    List<MemberProfileResponse> getByStatus(
            MemberStatus status
    );

    List<MemberProfileResponse> search(
            String query
    );

    MemberProfileResponse update(
            Long id,
            UpdateMemberProfileRequest request
    );

    MemberProfileResponse updateStatus(
            Long id,
            MemberStatus status
    );
}