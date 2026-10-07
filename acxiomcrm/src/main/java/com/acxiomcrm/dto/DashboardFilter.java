package com.acxiomcrm.dto;

import java.time.LocalDate;

public record DashboardFilter(

        DateRangeType range,

        LocalDate startDate,

        LocalDate endDate
) {
}