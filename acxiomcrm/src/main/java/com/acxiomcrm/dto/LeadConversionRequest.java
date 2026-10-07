package com.acxiomcrm.dto;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;

public record LeadConversionRequest(

        @NotBlank(message = "Opportunity name is required")
        String opportunityName,

        @NotNull(message = "Opportunity amount is required")
        @DecimalMin(
                value = "0.01",
                message = "Opportunity amount must be greater than 0"
        )
        BigDecimal amount,

        @NotNull(message = "Probability is required")
        @Min(0)
        @Max(100)
        Integer probability,

        @NotNull(message = "Expected close date is required")
        LocalDate expectedCloseDate,

        String customerAddress,

        String city,

        String state
) {
}