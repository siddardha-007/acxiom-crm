package com.acxiomcrm.service;

import com.acxiomcrm.dto.*;
import com.acxiomcrm.entity.*;
import com.acxiomcrm.enums.*;
import com.acxiomcrm.repository.*;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.*;

@Service
public class ReportService {

    private final CustomerRepository customerRepository;
    private final LeadRepository leadRepository;
    private final OpportunityRepository opportunityRepository;
    private final FollowUpRepository followUpRepository;
    private final AuditLogRepository auditLogRepository;
    private final AppUserRepository userRepository;
    private final CurrentUserService currentUserService;

    public ReportService(
            CustomerRepository customerRepository,
            LeadRepository leadRepository,
            OpportunityRepository opportunityRepository,
            FollowUpRepository followUpRepository,
            AuditLogRepository auditLogRepository,
            AppUserRepository userRepository,
            CurrentUserService currentUserService
    ) {
        this.customerRepository = customerRepository;
        this.leadRepository = leadRepository;
        this.opportunityRepository = opportunityRepository;
        this.followUpRepository = followUpRepository;
        this.auditLogRepository = auditLogRepository;
        this.userRepository = userRepository;
        this.currentUserService = currentUserService;
    }

    public List<PipelineReportResponse>
    pipelineReport() {

        List<Opportunity> opportunities =
                getScopedOpportunities();

        List<PipelineReportResponse> result =
                new ArrayList<>();

        for (OpportunityStage stage :
                OpportunityStage.values()) {

            List<Opportunity> stageOpportunities =
                    opportunities.stream()
                            .filter(o ->
                                    o.getStage() == stage)
                            .toList();

            BigDecimal total =
                    stageOpportunities.stream()
                            .map(Opportunity::getAmount)
                            .filter(Objects::nonNull)
                            .reduce(
                                    BigDecimal.ZERO,
                                    BigDecimal::add
                            );

            BigDecimal weighted =
                    stageOpportunities.stream()
                            .map(this::weightedAmount)
                            .reduce(
                                    BigDecimal.ZERO,
                                    BigDecimal::add
                            );

            result.add(
                    new PipelineReportResponse(
                            stage.name(),
                            stageOpportunities.size(),
                            total,
                            weighted
                    )
            );
        }

        return result;
    }

    public ConversionReportResponse
    conversionReport() {

        List<Lead> leads =
                getScopedLeads();

        long total =
                leads.size();

        long converted =
                leads.stream()
                        .filter(l ->
                                l.getStatus()
                                        == LeadStatus.CONVERTED)
                        .count();

        long unconverted =
                total - converted;

        double rate =
                total == 0
                        ? 0
                        : ((double) converted
                        / total) * 100;

        return new ConversionReportResponse(
                total,
                converted,
                unconverted,
                Math.round(rate * 100.0) / 100.0
        );
    }

    public List<UserActivityResponse>
    userActivityReport() {

        List<AuditLog> logs =
                auditLogRepository.findAll();

        Map<Long, Long> counts =
                new HashMap<>();

        for (AuditLog log : logs) {

            if (log.getUser() == null) {
                continue;
            }

            Long userId =
                    log.getUser().getId();

            counts.put(
                    userId,
                    counts.getOrDefault(userId, 0L) + 1
            );
        }

        return userRepository.findAll()
                .stream()
                .map(user ->
                        new UserActivityResponse(
                                user.getId(),
                                user.getName(),
                                counts.getOrDefault(
                                        user.getId(),
                                        0L
                                )
                        )
                )
                .toList();
    }

    public Map<String, Object>
    followUpReport() {

        List<FollowUp> followUps =
                getScopedFollowUps();

        LocalDate today =
                LocalDate.now();

        long planned =
                followUps.stream()
                        .filter(f ->
                                f.getStatus()
                                        == FollowUpStatus.PLANNED)
                        .count();

        long completed =
                followUps.stream()
                        .filter(f ->
                                f.getStatus()
                                        == FollowUpStatus.COMPLETED)
                        .count();

        long missed =
                followUps.stream()
                        .filter(f ->
                                f.getStatus()
                                        == FollowUpStatus.MISSED)
                        .count();

        long overdue =
                followUps.stream()
                        .filter(f ->
                                f.getStatus()
                                        == FollowUpStatus.PLANNED
                                        &&
                                        f.getFollowUpDate()
                                                .isBefore(today))
                        .count();

        return Map.of(
                "planned", planned,
                "completed", completed,
                "missed", missed,
                "overdue", overdue
        );
    }

    public Map<String, Object>
    opportunityReport() {

        List<Opportunity> opportunities =
                getScopedOpportunities();

        BigDecimal totalAmount =
                opportunities.stream()
                        .map(Opportunity::getAmount)
                        .filter(Objects::nonNull)
                        .reduce(
                                BigDecimal.ZERO,
                                BigDecimal::add
                        );

        BigDecimal openAmount =
                opportunities.stream()
                        .filter(o ->
                                o.getStatus()
                                        == OpportunityStatus.OPEN)
                        .map(Opportunity::getAmount)
                        .filter(Objects::nonNull)
                        .reduce(
                                BigDecimal.ZERO,
                                BigDecimal::add
                        );

        BigDecimal wonAmount =
                opportunities.stream()
                        .filter(o ->
                                o.getStatus()
                                        == OpportunityStatus.WON)
                        .map(Opportunity::getAmount)
                        .filter(Objects::nonNull)
                        .reduce(
                                BigDecimal.ZERO,
                                BigDecimal::add
                        );

        return Map.of(
                "totalOpportunities",
                opportunities.size(),

                "totalAmount",
                totalAmount,

                "openAmount",
                openAmount,

                "wonAmount",
                wonAmount
        );
    }

    public Map<String, Object>
    customerReport() {

        List<Customer> customers =
                getScopedCustomers();

        Map<String, Long> statusCounts =
                new LinkedHashMap<>();

        for (CustomerStatus status :
                CustomerStatus.values()) {

            long count =
                    customers.stream()
                            .filter(c ->
                                    c.getStatus() == status)
                            .count();

            statusCounts.put(
                    status.name(),
                    count
            );
        }

        return Map.of(
                "totalCustomers",
                customers.size(),

                "statusCounts",
                statusCounts
        );
    }

    public Map<String, Object>
    leadReport() {

        List<Lead> leads =
                getScopedLeads();

        Map<String, Long> statusCounts =
                new LinkedHashMap<>();

        for (LeadStatus status :
                LeadStatus.values()) {

            long count =
                    leads.stream()
                            .filter(l ->
                                    l.getStatus() == status)
                            .count();

            statusCounts.put(
                    status.name(),
                    count
            );
        }

        return Map.of(
                "totalLeads",
                leads.size(),

                "statusCounts",
                statusCounts
        );
    }

    public List<AuditLog> auditReport() {

        return auditLogRepository.findAll();
    }

    private BigDecimal weightedAmount(
            Opportunity opportunity
    ) {

        if (opportunity.getAmount() == null ||
                opportunity.getProbability() == null) {

            return BigDecimal.ZERO;
        }

        return opportunity.getAmount()
                .multiply(
                        BigDecimal.valueOf(
                                opportunity.getProbability()
                        )
                )
                .divide(
                        BigDecimal.valueOf(100),
                        2,
                        RoundingMode.HALF_UP
                );
    }

    private List<Customer>
    getScopedCustomers() {

        AppUser current =
                currentUserService.getCurrentUser();

        if (current.getRole()
                == Role.SALES_EXECUTIVE) {

            return customerRepository
                    .findByAssignedToId(
                            current.getId()
                    );
        }

        return customerRepository.findAll();
    }

    private List<Lead>
    getScopedLeads() {

        AppUser current =
                currentUserService.getCurrentUser();

        if (current.getRole()
                == Role.SALES_EXECUTIVE) {

            return leadRepository
                    .findByAssignedToId(
                            current.getId()
                    );
        }

        return leadRepository.findAll();
    }

    private List<Opportunity>
    getScopedOpportunities() {

        AppUser current =
                currentUserService.getCurrentUser();

        if (current.getRole()
                == Role.SALES_EXECUTIVE) {

            return opportunityRepository
                    .findByAssignedToId(
                            current.getId()
                    );
        }

        return opportunityRepository.findAll();
    }

    private List<FollowUp>
    getScopedFollowUps() {

        AppUser current =
                currentUserService.getCurrentUser();

        if (current.getRole()
                == Role.SALES_EXECUTIVE) {

            return followUpRepository
                    .findByAssignedToId(
                            current.getId()
                    );
        }

        return followUpRepository.findAll();
    }
}