package com.acxiomcrm.controller;

import com.acxiomcrm.dto.ActivityRequest;
import com.acxiomcrm.entity.Activity;
import com.acxiomcrm.service.ActivityService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/activities")
public class ActivityController {

    private final ActivityService activityService;

    public ActivityController(
            ActivityService activityService
    ) {
        this.activityService = activityService;
    }

    @GetMapping
    public ResponseEntity<List<Activity>> getAll() {

        return ResponseEntity.ok(
                activityService.getAll()
        );
    }

    @PostMapping
    public ResponseEntity<Activity> create(
            @Valid @RequestBody
            ActivityRequest request
    ) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        activityService.create(
                                request
                        )
                );
    }
}