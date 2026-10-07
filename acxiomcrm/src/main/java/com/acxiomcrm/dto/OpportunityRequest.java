package com.acxiomcrm.dto;

import com.acxiomcrm.enums.OpportunityStage;
import com.acxiomcrm.enums.OpportunityStatus;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;

public record OpportunityRequest(

        @NotBlank(message = "Opportunity name is required")
        String opportunityName,

        Long customerId,

        Long leadId,

        @NotNull(message = "Amount is required")
        @DecimalMin(
                value = "0.01",
                message = "Opportunity Amount must be greater than 0."
        )
        BigDecimal amount,

        @NotNull(message = "Stage is required")
        OpportunityStage stage,

        @NotNull(message = "Probability is required")
        @Min(value = 0)
        @Max(value = 100)
        Integer probability,

        @NotNull(message = "Expected close date is required")
        LocalDate expectedCloseDate,

        @NotNull(message = "Status is required")
        OpportunityStatus status,

        Long assignedToId,

        String notes
) {
}