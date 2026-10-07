package com.acxiomcrm.dto;

import java.math.BigDecimal;
import java.util.List;

public record DashboardResponse(

        long totalCustomers,

        long totalLeads,

        long openLeads,

        long totalOpportunities,

        long openOpportunities,

        long wonOpportunities,

        long lostOpportunities,

        BigDecimal totalPipelineValue,

        List<ChartData> leadStatusChart,

        List<ChartData> opportunityPipelineChart,

        List<ChartData> monthlySalesChart
) {
}