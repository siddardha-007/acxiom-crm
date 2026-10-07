package com.acxiomcrm.dto;

import com.acxiomcrm.enums.LeadStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record LeadResponse(

        Long id,

        String leadCode,

        String leadName,

        String email,

        String phone,

        String companyName,

        String source,

        LeadStatus status,

        String priority,

        BigDecimal expectedValue,

        Long assignedToId,

        String assignedToName,

        LocalDateTime createdDate
) {
}