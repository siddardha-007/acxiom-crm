package com.acxiomcrm.dto;

public record UserActivityResponse(

        Long userId,

        String userName,

        long activityCount
) {
}