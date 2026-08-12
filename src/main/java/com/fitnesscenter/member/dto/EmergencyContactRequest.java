package com.fitnesscenter.member.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record EmergencyContactRequest(

        @NotBlank(message = "Emergency contact name is required")
        @Size(max = 150)
        String name,

        @NotBlank(message = "Emergency contact phone is required")
        @Pattern(
                regexp = "^[+]?[0-9\\s()-]{7,20}$",
                message = "Invalid emergency contact phone number"
        )
        String phone,

        @Size(max = 100)
        String relationship
) {
}