package com.acxiomcrm.dto;

import com.acxiomcrm.enums.FollowUpStatus;
import com.acxiomcrm.enums.FollowUpType;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record FollowUpRequest(

        Long customerId,

        Long leadId,

        @NotNull(message = "Follow-up date is required")
        LocalDate followUpDate,

        @NotNull(message = "Follow-up type is required")
        FollowUpType followUpType,

        String subject,

        String remarks,

        @NotNull(message = "Status is required")
        FollowUpStatus status,

        Long assignedToId
) {
}