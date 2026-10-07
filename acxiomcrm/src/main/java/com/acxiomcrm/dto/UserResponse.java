package com.acxiomcrm.dto;

import com.acxiomcrm.enums.Role;

import java.time.LocalDateTime;

public record UserResponse(

        Long id,

        String name,

        String email,

        Role role,

        boolean active,

        int failedLoginAttempts,

        boolean locked,

        LocalDateTime lockoutEnd,

        LocalDateTime createdDate
) {
}