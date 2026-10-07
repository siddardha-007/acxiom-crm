package com.acxiomcrm.controller;

import com.acxiomcrm.dto.*;
import com.acxiomcrm.entity.Lead;
import com.acxiomcrm.enums.LeadStatus;
import com.acxiomcrm.service.LeadService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/leads")
public class LeadController {

    private final LeadService leadService;

    public LeadController(LeadService leadService) {
        this.leadService = leadService;
    }

    @GetMapping
    public ResponseEntity<List<Lead>> getLeads(
            @RequestParam(required = false)
            String search
    ) {

        return ResponseEntity.ok(
                leadService.search(search)
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<Lead> getLead(
            @PathVariable Long id
    ) {

        return ResponseEntity.ok(
                leadService.getById(id)
        );
    }

    @PostMapping
    public ResponseEntity<Lead> createLead(
            @Valid @RequestBody LeadRequest request
    ) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(leadService.create(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Lead> updateLead(
            @PathVariable Long id,
            @Valid @RequestBody LeadRequest request
    ) {

        return ResponseEntity.ok(
                leadService.update(id, request)
        );
    }

    @PostMapping("/{id}/convert")
    public ResponseEntity<LeadConversionResponse> convertLead(
            @PathVariable Long id,
            @Valid @RequestBody
            LeadConversionRequest request
    ) {

        return ResponseEntity.ok(
                leadService.convertLead(
                        id,
                        request
                )
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLead(
            @PathVariable Long id
    ) {

        leadService.delete(id);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/page")
    public ResponseEntity<PageResponse<LeadResponse>>
    getLeads(

            @RequestParam(required = false)
            String search,

            @RequestParam(required = false)
            LeadStatus status,

            @RequestParam(required = false)
            Long assignedToId,

            @PageableDefault(
                    size = 10,
                    sort = "createdDate",
                    direction = Sort.Direction.DESC
            )
            Pageable pageable
    ) {

        return ResponseEntity.ok(
                leadService.searchLeads(
                        search,
                        status,
                        assignedToId,
                        pageable
                )
        );
    }
}