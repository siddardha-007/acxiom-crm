package com.acxiomcrm.dto;

import com.acxiomcrm.enums.CustomerStatus;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CustomerRequest(

        @NotBlank(message = "Customer name is required")
        @Size(max = 100)
        String customerName,

        @NotBlank(message = "Email is required")
        @Email(message = "Enter a valid email address")
        String email,

        @NotBlank(message = "Phone is required")
        @Pattern(
                regexp = "^[6-9][0-9]{9}$",
                message = "Enter a valid phone number"
        )
        String phone,

        String companyName,

        String address,

        String city,

        String state,

        CustomerStatus status,

        Long assignedToId
) {
}