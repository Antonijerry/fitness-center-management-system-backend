package com.fitnesscenter.membership.service;

import com.fitnesscenter.common.exception.ConflictException;
import com.fitnesscenter.common.exception.ResourceNotFoundException;
import com.fitnesscenter.membership.dto.CreateMembershipPlanRequest;
import com.fitnesscenter.membership.dto.MembershipPlanResponse;
import com.fitnesscenter.membership.dto.UpdateMembershipPlanRequest;
import com.fitnesscenter.membership.entity.MembershipPlan;
import com.fitnesscenter.membership.mapper.MembershipMapper;
import com.fitnesscenter.membership.repository.MembershipPlanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MembershipPlanServiceImpl
        implements MembershipPlanService {

    private final MembershipPlanRepository planRepository;

    private final MembershipMapper membershipMapper;


    @Override
    @Transactional
    public MembershipPlanResponse create(
            CreateMembershipPlanRequest request
    ) {

        if (
                planRepository.existsByNameIgnoreCase(
                        request.name().trim()
                )
        ) {

            throw new ConflictException(
                    "A membership plan with this name already exists"
            );
        }

        MembershipPlan plan =
                new MembershipPlan();

        plan.setName(
                request.name().trim()
        );

        plan.setDescription(
                request.description()
        );

        plan.setType(
                request.type()
        );

        plan.setPrice(
                request.price()
        );

        plan.setDurationInDays(
                request.durationInDays()
        );

        plan.setMaxVisitsPerMonth(
                request.maxVisitsPerMonth()
        );

        plan.setAutoRenewable(
                request.autoRenewable()
        );

        plan.setActive(true);

        return membershipMapper.toPlanResponse(
                planRepository.save(plan)
        );
    }


    @Override
    @Transactional
    public MembershipPlanResponse update(
            Long id,
            UpdateMembershipPlanRequest request
    ) {

        MembershipPlan plan =
                getPlan(id);

        planRepository
                .findByNameIgnoreCase(
                        request.name().trim()
                )
                .ifPresent(existing -> {

                    if (
                            !existing.getId()
                                    .equals(id)
                    ) {

                        throw new ConflictException(
                                "A membership plan with this name already exists"
                        );
                    }
                });

        plan.setName(
                request.name().trim()
        );

        plan.setDescription(
                request.description()
        );

        plan.setType(
                request.type()
        );

        plan.setPrice(
                request.price()
        );

        plan.setDurationInDays(
                request.durationInDays()
        );

        plan.setMaxVisitsPerMonth(
                request.maxVisitsPerMonth()
        );

        plan.setActive(
                request.active()
        );

        plan.setAutoRenewable(
                request.autoRenewable()
        );

        return membershipMapper.toPlanResponse(
                plan
        );
    }


    @Override
    public MembershipPlanResponse getById(
            Long id
    ) {

        return membershipMapper.toPlanResponse(
                getPlan(id)
        );
    }


    @Override
    public List<MembershipPlanResponse> getAll() {

        return planRepository.findAll()
                .stream()
                .map(
                        membershipMapper::toPlanResponse
                )
                .toList();
    }


    @Override
    public List<MembershipPlanResponse> getActive() {

        return planRepository
                .findAllByActiveTrue()
                .stream()
                .map(
                        membershipMapper::toPlanResponse
                )
                .toList();
    }


    @Override
    @Transactional
    public void delete(
            Long id
    ) {

        MembershipPlan plan =
                getPlan(id);

        /*
         * We will eventually prevent deletion when
         * historical memberships reference this plan.
         *
         * For now, deactivate it instead of physically
         * deleting it.
         */
        plan.setActive(false);
    }


    private MembershipPlan getPlan(
            Long id
    ) {

        return planRepository
                .findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Membership plan not found"
                        )
                );
    }
}