package com.fitnesscenter.member.mapper;

import com.fitnesscenter.member.dto.EmergencyContactResponse;
import com.fitnesscenter.member.dto.MemberProfileResponse;
import com.fitnesscenter.member.entity.MemberProfile;
import org.springframework.stereotype.Component;

@Component
public class MemberMapper {

    public MemberProfileResponse toResponse(
            MemberProfile member
    ) {

        EmergencyContactResponse emergencyContact = null;

        if (
                member.getEmergencyContactName() != null
                        || member.getEmergencyContactPhone() != null
                        || member.getEmergencyContactRelationship() != null
        ) {

            emergencyContact =
                    new EmergencyContactResponse(
                            member.getEmergencyContactName(),
                            member.getEmergencyContactPhone(),
                            member.getEmergencyContactRelationship()
                    );
        }

        return new MemberProfileResponse(
                member.getId(),
                member.getUser().getId(),
                member.getMemberNumber(),
                member.getUser().getFirstName(),
                member.getUser().getLastName(),
                member.getUser().getEmail(),
                member.getPhone(),
                member.getGender(),
                member.getDateOfBirth(),
                member.getAddress(),
                emergencyContact,
                member.getFitnessGoals(),
                member.getFitnessNotes(),
                member.getStatus()
        );
    }
}