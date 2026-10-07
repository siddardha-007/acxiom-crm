package com.acxiomcrm.dto;

import com.acxiomcrm.enums.ActivityStatus;
import com.acxiomcrm.enums.ActivityType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record ActivityRequest(

        @NotNull(message = "Activity type is required")
        ActivityType activityType,

        @NotBlank(message = "Subject is required")
        String subject,

        String description,

        @NotNull(message = "Activity date is required")
        LocalDate activityDate,

        Long customerId,

        Long leadId,

        @NotNull(message = "Status is required")
        ActivityStatus status,

        Long assignedToId
) {
}