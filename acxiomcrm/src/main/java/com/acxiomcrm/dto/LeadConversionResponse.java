package com.acxiomcrm.dto;

public record LeadConversionResponse(

        Long leadId,

        Long customerId,

        Long opportunityId,

        String message
) {
}