package com.acxiomcrm.dto;

import java.time.LocalDateTime;

public record AuditLogResponse(

        Long id,

        Long userId,

        String userName,

        String action,

        String entityName,

        Long recordId,

        String oldValue,

        String newValue,

        LocalDateTime createdDate,

        String ipAddress
) {
}