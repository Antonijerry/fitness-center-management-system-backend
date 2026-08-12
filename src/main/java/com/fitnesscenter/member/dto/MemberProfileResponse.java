package com.fitnesscenter.member.dto;

import com.fitnesscenter.member.entity.MemberStatus;

import java.time.LocalDate;

public record MemberProfileResponse(

        Long id,

        Long userId,

        String memberNumber,

        String firstName,

        String lastName,

        String email,

        String phone,

        String gender,

        LocalDate dateOfBirth,

        String address,

        EmergencyContactResponse emergencyContact,

        String fitnessGoals,

        String fitnessNotes,

        MemberStatus status
) {
}