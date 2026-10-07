package com.acxiomcrm.dto;

import com.acxiomcrm.enums.CustomerStatus;

import java.time.LocalDateTime;

public record CustomerResponse(

        Long id,

        String customerCode,

        String customerName,

        String email,

        String phone,

        String companyName,

        String address,

        String city,

        String state,

        CustomerStatus status,

        Long assignedToId,

        String assignedToName,

        Long createdById,

        String createdByName,

        LocalDateTime createdDate
) {
}