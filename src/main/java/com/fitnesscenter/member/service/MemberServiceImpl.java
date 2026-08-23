package com.fitnesscenter.member.service;

import com.fitnesscenter.common.exception.ConflictException;
import com.fitnesscenter.common.exception.ResourceNotFoundException;
import com.fitnesscenter.member.dto.CreateMemberProfileRequest;
import com.fitnesscenter.member.dto.EmergencyContactRequest;
import com.fitnesscenter.member.dto.MemberProfileResponse;
import com.fitnesscenter.member.dto.UpdateMemberProfileRequest;
import com.fitnesscenter.member.entity.MemberProfile;
import com.fitnesscenter.member.entity.MemberStatus;
import com.fitnesscenter.member.mapper.MemberMapper;
import com.fitnesscenter.member.repository.MemberProfileRepository;
import com.fitnesscenter.user.entity.User;
import com.fitnesscenter.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberServiceImpl
        implements MemberService {

    private final MemberProfileRepository memberRepository;

    private final UserRepository userRepository;

    private final MemberMapper memberMapper;


    @Override
    @Transactional
    public MemberProfileResponse create(
            CreateMemberProfileRequest request
    ) {

        User user =
                userRepository.findById(
                        request.userId()
                ).orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found"
                        )
                );

        if (
                memberRepository.existsByUserId(
                        request.userId()
                )
        ) {

            throw new ConflictException(
                    "This user already has a member profile"
            );
        }

        MemberProfile member =
                new MemberProfile();

        member.setUser(user);

        member.setMemberNumber(
                generateMemberNumber()
        );

        member.setPhone(
                request.phone().trim()
        );

        member.setGender(
                request.gender()
        );

        member.setDateOfBirth(
                request.dateOfBirth()
        );

        member.setAddress(
                request.address()
        );

        applyEmergencyContact(
                member,
                request.emergencyContact()
        );

        member.setFitnessGoals(
                request.fitnessGoals()
        );

        member.setFitnessNotes(
                request.fitnessNotes()
        );

        member.setStatus(
                MemberStatus.ACTIVE
        );

        return memberMapper.toResponse(
                memberRepository.save(member)
        );
    }


    @Override
    public MemberProfileResponse getById(
            Long id
    ) {

        return memberMapper.toResponse(
                getMember(id)
        );
    }


    @Override
    public MemberProfileResponse getByUserId(
            Long userId
    ) {

        MemberProfile member =
                memberRepository.findByUserId(
                        userId
                ).orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Member profile not found"
                        )
                );

        return memberMapper.toResponse(member);
    }


    @Override
    public MemberProfileResponse getByMemberNumber(
            String memberNumber
    ) {

        MemberProfile member =
                memberRepository
                        .findByMemberNumber(
                                memberNumber
                        )
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Member profile not found"
                                )
                        );

        return memberMapper.toResponse(member);
    }


    @Override
    public List<MemberProfileResponse> getAll() {

        return memberRepository.findAll()
                .stream()
                .map(memberMapper::toResponse)
                .toList();
    }


    @Override
    public List<MemberProfileResponse> getByStatus(
            MemberStatus status
    ) {

        return memberRepository
                .findAllByStatus(status)
                .stream()
                .map(memberMapper::toResponse)
                .toList();
    }



    @Override
    public List<MemberProfileResponse> search(
            String query
    ) {

        String normalized =
                query == null
                        ? ""
                        : query.trim();

        if (normalized.isBlank()) {
            return getAll();
        }

        List<MemberProfile> members =
                memberRepository
                        .findByMemberNumberContainingIgnoreCase(
                                normalized
                        );

        if (members.isEmpty()) {

            members =
                    memberRepository
                            .findByPhoneContaining(
                                    normalized
                            );
        }

        if (members.isEmpty()) {

            members =
                    memberRepository
                            .findByUserFirstNameContainingIgnoreCaseOrUserLastNameContainingIgnoreCaseOrUserEmailContainingIgnoreCase(
                                    normalized,
                                    normalized,
                                    normalized
                            );
        }

        return members
                .stream()
                .map(memberMapper::toResponse)
                .toList();
    }


    @Override
    @Transactional
    public MemberProfileResponse update(
            Long id,
            UpdateMemberProfileRequest request
    ) {

        MemberProfile member =
                getMember(id);

        member.setPhone(
                request.phone().trim()
        );

        member.setGender(
                request.gender()
        );

        member.setDateOfBirth(
                request.dateOfBirth()
        );

        member.setAddress(
                request.address()
        );

        applyEmergencyContact(
                member,
                request.emergencyContact()
        );

        member.setFitnessGoals(
                request.fitnessGoals()
        );

        member.setFitnessNotes(
                request.fitnessNotes()
        );

        return memberMapper.toResponse(member);
    }


    @Override
    @Transactional
    public MemberProfileResponse updateStatus(
            Long id,
            MemberStatus status
    ) {

        MemberProfile member =
                getMember(id);

        member.setStatus(status);

        return memberMapper.toResponse(member);
    }


    private MemberProfile getMember(
            Long id
    ) {

        return memberRepository
                .findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Member profile not found"
                        )
                );
    }


    private void applyEmergencyContact(
            MemberProfile member,
            EmergencyContactRequest request
    ) {

        if (request == null) {

            member.setEmergencyContactName(null);
            member.setEmergencyContactPhone(null);
            member.setEmergencyContactRelationship(null);

            return;
        }

        member.setEmergencyContactName(
                request.name().trim()
        );

        member.setEmergencyContactPhone(
                request.phone().trim()
        );

        member.setEmergencyContactRelationship(
                request.relationship()
        );
    }


    private String generateMemberNumber() {

        String memberNumber;

        do {

            memberNumber =
                    "FIT-"
                            + System.currentTimeMillis();

        } while (
                memberRepository.existsByMemberNumber(
                        memberNumber
                )
        );

        return memberNumber;
    }
}