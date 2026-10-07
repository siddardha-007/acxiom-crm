package com.acxiomcrm.dto;

import java.time.LocalDateTime;
import java.util.Map;

public record ApiError(

        int status,

        String error,

        String message,

        String path,

        LocalDateTime timestamp,

        Map<String, String> validationErrors
) {
}