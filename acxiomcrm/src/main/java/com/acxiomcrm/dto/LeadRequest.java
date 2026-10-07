package com.acxiomcrm.dto;

import com.acxiomcrm.enums.LeadPriority;
import com.acxiomcrm.enums.LeadStatus;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record LeadRequest(

        @NotBlank(message = "Lead name is required")
        String leadName,

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

        String source,

        @NotNull(message = "Status is required")
        LeadStatus status,

        @NotNull(message = "Priority is required")
        LeadPriority priority,

        @PositiveOrZero(message = "Expected value cannot be negative")
        BigDecimal expectedValue,

        Long assignedToId
) {
}