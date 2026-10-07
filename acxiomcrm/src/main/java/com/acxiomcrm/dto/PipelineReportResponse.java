package com.acxiomcrm.dto;

import java.math.BigDecimal;

public record PipelineReportResponse(

        String stage,

        long opportunityCount,

        BigDecimal totalAmount,

        BigDecimal weightedAmount
) {
}