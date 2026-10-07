package com.acxiomcrm.controller;

import com.acxiomcrm.dto.OpportunityRequest;
import com.acxiomcrm.dto.OpportunityResponse;
import com.acxiomcrm.dto.PageResponse;
import com.acxiomcrm.entity.Opportunity;
import com.acxiomcrm.enums.OpportunityStage;
import com.acxiomcrm.enums.OpportunityStatus;
import com.acxiomcrm.service.OpportunityService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/opportunities")
public class OpportunityController {

    private final OpportunityService opportunityService;

    public OpportunityController(
            OpportunityService opportunityService
    ) {
        this.opportunityService = opportunityService;
    }

    @GetMapping
    public ResponseEntity<List<Opportunity>> getAll() {

        return ResponseEntity.ok(
                opportunityService.getAll()
        );
    }

    @GetMapping("/page")
    public ResponseEntity<
            PageResponse<OpportunityResponse>
            > search(

            @RequestParam(required = false)
            String search,

            @RequestParam(required = false)
            OpportunityStage stage,

            @RequestParam(required = false)
            OpportunityStatus status,

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
                opportunityService.searchOpportunities(
                        search,
                        stage,
                        status,
                        assignedToId,
                        pageable
                )
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<Opportunity> getById(
            @PathVariable Long id
    ) {

        return ResponseEntity.ok(
                opportunityService.getById(id)
        );
    }

    @PostMapping
    public ResponseEntity<Opportunity> create(
            @Valid @RequestBody
            OpportunityRequest request
    ) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        opportunityService.create(
                                request
                        )
                );
    }

    @PutMapping("/{id}")
    public ResponseEntity<Opportunity> update(
            @PathVariable Long id,
            @Valid @RequestBody
            OpportunityRequest request
    ) {

        return ResponseEntity.ok(
                opportunityService.update(
                        id,
                        request
                )
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable Long id
    ) {

        opportunityService.delete(id);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/weighted-pipeline")
    public ResponseEntity<BigDecimal>
    weightedPipeline(
            @PathVariable Long id
    ) {

        Opportunity opportunity =
                opportunityService.getById(id);

        return ResponseEntity.ok(
                opportunityService
                        .calculateWeightedPipeline(
                                opportunity
                        )
        );
    }
}