package com.fitnesscenter.member.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import java.time.LocalDate;

public record CreateMemberProfileRequest(

        @NotNull(message = "User ID is required")
        Long userId,

        @NotBlank(message = "Phone number is required")
        @Pattern(
                regexp = "^[+]?[0-9\\s()-]{7,20}$",
                message = "Invalid phone number"
        )
        String phone,

        @Size(max = 20)
        String gender,

        @Past(
                message = "Date of birth must be in the past"
        )
        LocalDate dateOfBirth,

        @Size(max = 255)
        String address,

        @Valid
        EmergencyContactRequest emergencyContact,

        @Size(max = 1000)
        String fitnessGoals,

        @Size(max = 2000)
        String fitnessNotes
) {
}