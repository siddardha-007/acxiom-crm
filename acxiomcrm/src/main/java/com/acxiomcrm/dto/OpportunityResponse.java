package com.acxiomcrm.dto;

import com.acxiomcrm.enums.OpportunityStage;
import com.acxiomcrm.enums.OpportunityStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record OpportunityResponse(

        Long id,

        String opportunityName,

        Long customerId,

        String customerName,

        Long leadId,

        String leadName,

        Long assignedToId,

        String assignedToName,

        BigDecimal amount,

        OpportunityStage stage,

        Integer probability,

        BigDecimal weightedAmount,

        LocalDate expectedCloseDate,

        OpportunityStatus status,

        String notes,

        LocalDateTime createdDate
) {
}