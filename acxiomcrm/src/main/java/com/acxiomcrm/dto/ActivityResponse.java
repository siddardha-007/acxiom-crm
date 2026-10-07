package com.acxiomcrm.dto;

import com.acxiomcrm.enums.ActivityStatus;
import com.acxiomcrm.enums.ActivityType;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record ActivityResponse(

        Long id,

        ActivityType activityType,

        String subject,

        String description,

        LocalDate activityDate,

        Long customerId,

        String customerName,

        Long leadId,

        String leadName,

        ActivityStatus status,

        Long assignedToId,

        String assignedToName,

        LocalDateTime createdDate
) {
}