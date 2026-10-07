package com.acxiomcrm.controller;

import com.acxiomcrm.dto.AuditLogResponse;
import com.acxiomcrm.service.AuditQueryService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/audit")
@PreAuthorize("hasRole('ADMIN')")
public class AuditController {

    private final AuditQueryService auditQueryService;

    public AuditController(
            AuditQueryService auditQueryService
    ) {
        this.auditQueryService = auditQueryService;
    }

    @GetMapping
    public ResponseEntity<List<AuditLogResponse>>
    getAuditLogs(
            @RequestParam(required = false)
            Long userId,

            @RequestParam(required = false)
            String action,

            @RequestParam(required = false)
            String entityName,

            @RequestParam(required = false)
            @DateTimeFormat(
                    iso = DateTimeFormat.ISO.DATE_TIME
            )
            LocalDateTime start,

            @RequestParam(required = false)
            @DateTimeFormat(
                    iso = DateTimeFormat.ISO.DATE_TIME
            )
            LocalDateTime end
    ) {

        return ResponseEntity.ok(
                auditQueryService.filter(
                        userId,
                        action,
                        entityName,
                        start,
                        end
                )
        );
    }
}