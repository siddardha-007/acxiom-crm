package com.acxiomcrm.dto;

public record ConversionReportResponse(

        long totalLeads,

        long convertedLeads,

        long unconvertedLeads,

        double conversionRate
) {
}