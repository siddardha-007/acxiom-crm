package com.acxiomcrm.controller;

import com.acxiomcrm.dto.ConversionReportResponse;
import com.acxiomcrm.dto.PipelineReportResponse;
import com.acxiomcrm.dto.UserActivityResponse;
import com.acxiomcrm.service.ReportService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reports")
@PreAuthorize(
        "hasAnyRole('ADMIN','MANAGER','SALES_EXECUTIVE')"
)
public class ReportController {

    private final ReportService reportService;

    public ReportController(
            ReportService reportService
    ) {
        this.reportService = reportService;
    }

    @GetMapping("/pipeline")
    public ResponseEntity<List<PipelineReportResponse>>
    pipelineReport() {

        return ResponseEntity.ok(
                reportService.pipelineReport()
        );
    }

    @GetMapping("/conversion")
    public ResponseEntity<ConversionReportResponse>
    conversionReport() {

        return ResponseEntity.ok(
                reportService.conversionReport()
        );
    }

    @GetMapping("/customers")
    public ResponseEntity<Map<String, Object>>
    customerReport() {

        return ResponseEntity.ok(
                reportService.customerReport()
        );
    }

    @GetMapping("/leads")
    public ResponseEntity<Map<String, Object>>
    leadReport() {

        return ResponseEntity.ok(
                reportService.leadReport()
        );
    }

    @GetMapping("/opportunities")
    public ResponseEntity<Map<String, Object>>
    opportunityReport() {

        return ResponseEntity.ok(
                reportService.opportunityReport()
        );
    }

    @GetMapping("/followups")
    public ResponseEntity<Map<String, Object>>
    followUpReport() {

        return ResponseEntity.ok(
                reportService.followUpReport()
        );
    }

    @GetMapping("/users")
    public ResponseEntity<List<UserActivityResponse>>
    userActivityReport() {

        return ResponseEntity.ok(
                reportService.userActivityReport()
        );
    }

    @GetMapping("/audit")
    public ResponseEntity<?>
    auditReport() {

        return ResponseEntity.ok(
                reportService.auditReport()
        );
    }
}