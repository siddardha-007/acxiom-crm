package com.acxiomcrm.controller;

import com.acxiomcrm.dto.FollowUpRequest;
import com.acxiomcrm.dto.FollowUpResponse;
import com.acxiomcrm.dto.PageResponse;
import com.acxiomcrm.entity.FollowUp;
import com.acxiomcrm.enums.FollowUpStatus;
import com.acxiomcrm.service.FollowUpService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/followups")
public class FollowUpController {

    private final FollowUpService followUpService;

    public FollowUpController(
            FollowUpService followUpService
    ) {
        this.followUpService = followUpService;
    }

    @GetMapping
    public ResponseEntity<List<FollowUp>> getAll() {

        return ResponseEntity.ok(
                followUpService.getAll()
        );
    }

    @GetMapping("/upcoming")
    public ResponseEntity<List<FollowUp>>
    getUpcoming() {

        return ResponseEntity.ok(
                followUpService.getUpcoming()
        );
    }

    @GetMapping("/page")
    public ResponseEntity<
            PageResponse<FollowUpResponse>
            > search(

            @RequestParam(required = false)
            @DateTimeFormat(
                    iso = DateTimeFormat.ISO.DATE
            )
            LocalDate date,

            @RequestParam(required = false)
            FollowUpStatus status,

            @RequestParam(required = false)
            Long assignedToId,

            @RequestParam(required = false)
            Long customerId,

            @RequestParam(required = false)
            Long leadId,

            @PageableDefault(
                    size = 10,
                    sort = "followUpDate",
                    direction = Sort.Direction.ASC
            )
            Pageable pageable
    ) {

        return ResponseEntity.ok(
                followUpService.searchFollowUps(
                        date,
                        status,
                        assignedToId,
                        customerId,
                        leadId,
                        pageable
                )
        );
    }

    @GetMapping("/overdue")
    public ResponseEntity<List<FollowUp>>
    getOverdue() {

        return ResponseEntity.ok(
                followUpService.getOverdue()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<FollowUp> getById(
            @PathVariable Long id
    ) {

        return ResponseEntity.ok(
                followUpService.getById(id)
        );
    }

    @PostMapping
    public ResponseEntity<FollowUp> create(
            @Valid @RequestBody
            FollowUpRequest request
    ) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        followUpService.create(
                                request
                        )
                );
    }

    @PutMapping("/{id}")
    public ResponseEntity<FollowUp> update(
            @PathVariable Long id,
            @Valid @RequestBody
            FollowUpRequest request
    ) {

        return ResponseEntity.ok(
                followUpService.update(
                        id,
                        request
                )
        );
    }

    @PutMapping("/{id}/complete")
    public ResponseEntity<FollowUp> complete(
            @PathVariable Long id
    ) {

        return ResponseEntity.ok(
                followUpService.complete(id)
        );
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<FollowUp> cancel(
            @PathVariable Long id
    ) {

        return ResponseEntity.ok(
                followUpService.cancel(id)
        );
    }
}