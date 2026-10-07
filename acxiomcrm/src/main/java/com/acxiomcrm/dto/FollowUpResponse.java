package com.acxiomcrm.dto;

import com.acxiomcrm.enums.FollowUpStatus;
import com.acxiomcrm.enums.FollowUpType;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record FollowUpResponse(

        Long id,

        Long customerId,

        String customerName,

        Long leadId,

        String leadName,

        LocalDate followUpDate,

        FollowUpType followUpType,

        String subject,

        String remarks,

        FollowUpStatus status,

        Long assignedToId,

        String assignedToName,

        LocalDateTime createdDate
) {
}