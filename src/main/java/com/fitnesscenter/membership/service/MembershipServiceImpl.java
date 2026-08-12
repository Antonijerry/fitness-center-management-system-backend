package com.fitnesscenter.membership.service;

import com.fitnesscenter.common.exception.BadRequestException;
import com.fitnesscenter.common.exception.ConflictException;
import com.fitnesscenter.common.exception.ResourceNotFoundException;
import com.fitnesscenter.membership.dto.CreateMembershipRequest;
import com.fitnesscenter.membership.dto.MembershipResponse;
import com.fitnesscenter.membership.entity.Membership;
import com.fitnesscenter.membership.entity.MembershipPlan;
import com.fitnesscenter.membership.entity.MembershipStatus;
import com.fitnesscenter.membership.mapper.MembershipMapper;
import com.fitnesscenter.membership.repository.MembershipPlanRepository;
import com.fitnesscenter.membership.repository.MembershipRepository;
import com.fitnesscenter.user.entity.User;
import com.fitnesscenter.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MembershipServiceImpl
        implements MembershipService {

    private final MembershipRepository membershipRepository;

    private final MembershipPlanRepository planRepository;

    private final UserRepository userRepository;

    private final MembershipMapper membershipMapper;


    @Override
    @Transactional
    public MembershipResponse create(
            CreateMembershipRequest request
    ) {

        User user =
                userRepository
                        .findById(request.userId())
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "User not found"
                                )
                        );

        MembershipPlan plan =
                planRepository
                        .findById(request.planId())
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Membership plan not found"
                                )
                        );

        if (!plan.isActive()) {

            throw new BadRequestException(
                    "Membership plan is inactive"
            );
        }

        if (request.startDate().isBefore(
                LocalDate.now()
        )) {

            throw new BadRequestException(
                    "Membership start date cannot be in the past"
            );
        }

        boolean alreadyActive =
                membershipRepository
                        .existsByUserIdAndStatus(
                                user.getId(),
                                MembershipStatus.ACTIVE
                        );

        if (alreadyActive) {

            throw new ConflictException(
                    "User already has an active membership"
            );
        }

        LocalDate endDate =
                request.startDate()
                        .plusDays(
                                plan.getDurationInDays()
                        )
                        .minusDays(1);

        Membership membership =
                new Membership();

        membership.setUser(user);

        membership.setPlan(plan);

        membership.setStartDate(
                request.startDate()
        );

        membership.setEndDate(
                endDate
        );

        membership.setStatus(
                MembershipStatus.PENDING
        );

        membership.setPrice(
                plan.getPrice()
        );

        membership.setAutoRenewable(
                request.autoRenewable()
        );

        membership.setNotes(
                request.notes()
        );

        Membership saved =
                membershipRepository.save(
                        membership
                );

        return membershipMapper.toMembershipResponse(
                saved
        );
    }


    @Override
    public MembershipResponse getById(
            Long id
    ) {

        return membershipMapper.toMembershipResponse(
                getMembership(id)
        );
    }


    @Override
    public List<MembershipResponse> getAll() {

        return membershipRepository
                .findAll()
                .stream()
                .map(
                        membershipMapper::toMembershipResponse
                )
                .toList();
    }


    @Override
    public List<MembershipResponse> getByUserId(
            Long userId
    ) {

        if (!userRepository.existsById(userId)) {

            throw new ResourceNotFoundException(
                    "User not found"
            );
        }

        return membershipRepository
                .findAllByUserId(userId)
                .stream()
                .map(
                        membershipMapper::toMembershipResponse
                )
                .toList();
    }


    @Override
    @Transactional
    public MembershipResponse cancel(
            Long id
    ) {

        Membership membership =
                getMembership(id);

        if (
                membership.getStatus()
                        == MembershipStatus.CANCELLED
        ) {

            throw new BadRequestException(
                    "Membership is already cancelled"
            );
        }

        membership.setStatus(
                MembershipStatus.CANCELLED
        );

        return membershipMapper.toMembershipResponse(
                membership
        );
    }


    @Override
    @Transactional
    public MembershipResponse suspend(
            Long id
    ) {

        Membership membership =
                getMembership(id);

        if (
                membership.getStatus()
                        != MembershipStatus.ACTIVE
        ) {

            throw new BadRequestException(
                    "Only active memberships can be suspended"
            );
        }

        membership.setStatus(
                MembershipStatus.SUSPENDED
        );

        return membershipMapper.toMembershipResponse(
                membership
        );
    }

// this check whether the user already has another active membership.


//    @Override
//    @Transactional
//    public MembershipResponse activate(Long id) {
//
//        Membership membership = getMembership(id);
//
//        if (membership.getStatus() == MembershipStatus.CANCELLED) {
//            throw new BadRequestException(
//                    "Cancelled memberships cannot be activated"
//            );
//        }
//
//        if (membership.getEndDate().isBefore(LocalDate.now())) {
//            throw new BadRequestException(
//                    "Expired membership cannot be activated"
//            );
//        }
//
//        boolean alreadyActive =
//                membershipRepository.existsByUserIdAndStatus(
//                        membership.getUser().getId(),
//                        MembershipStatus.ACTIVE
//                );
//
//        if (alreadyActive) {
//            throw new ConflictException(
//                    "User already has an active membership"
//            );
//        }
//
//        membership.setStatus(MembershipStatus.ACTIVE);
//
//        return membershipMapper.toMembershipResponse(membership);
//    }
//



    @Override
    @Transactional
    public MembershipResponse activate(
            Long id
    ) {

        Membership membership =
                getMembership(id);

        if (
                membership.getStatus()
                        == MembershipStatus.CANCELLED
        ) {

            throw new BadRequestException(
                    "Cancelled memberships cannot be activated"
            );
        }

        if (
                membership.getEndDate()
                        .isBefore(LocalDate.now())
        ) {

            throw new BadRequestException(
                    "Expired membership cannot be activated"
            );
        }

        membership.setStatus(
                MembershipStatus.ACTIVE
        );

        return membershipMapper.toMembershipResponse(
                membership
        );
    }


    @Override
    @Transactional
    public void expireMemberships() {

        LocalDate today =
                LocalDate.now();

        List<Membership> memberships =
                membershipRepository
                        .findAllByEndDateBeforeAndStatus(
                                today,
                                MembershipStatus.ACTIVE
                        );

        memberships.forEach(
                membership ->
                        membership.setStatus(
                                MembershipStatus.EXPIRED
                        )
        );
    }


    private Membership getMembership(
            Long id
    ) {

        return membershipRepository
                .findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Membership not found"
                        )
                );
    }
}