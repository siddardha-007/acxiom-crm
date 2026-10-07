package com.acxiomcrm.service;

import com.acxiomcrm.dto.ChartData;
import com.acxiomcrm.dto.DashboardResponse;
import com.acxiomcrm.entity.AppUser;
import com.acxiomcrm.entity.Lead;
import com.acxiomcrm.entity.Opportunity;
import com.acxiomcrm.enums.LeadStatus;
import com.acxiomcrm.enums.OpportunityStage;
import com.acxiomcrm.enums.OpportunityStatus;
import com.acxiomcrm.repository.CustomerRepository;
import com.acxiomcrm.repository.LeadRepository;
import com.acxiomcrm.repository.OpportunityRepository;
import com.acxiomcrm.repository.FollowUpRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.*;

@Service
public class DashboardService {

    private final CustomerRepository customerRepository;
    private final LeadRepository leadRepository;
    private final OpportunityRepository opportunityRepository;
    private final FollowUpRepository followUpRepository;
    private final CurrentUserService currentUserService;

    public DashboardService(
            CustomerRepository customerRepository,
            LeadRepository leadRepository,
            OpportunityRepository opportunityRepository,
            FollowUpRepository followUpRepository,
            CurrentUserService currentUserService
    ) {
        this.customerRepository = customerRepository;
        this.leadRepository = leadRepository;
        this.opportunityRepository = opportunityRepository;
        this.followUpRepository = followUpRepository;
        this.currentUserService = currentUserService;
    }

    public DashboardResponse getDashboard() {

        AppUser current =
                currentUserService.getCurrentUser();

        boolean salesExecutive =
                current.getRole().name()
                        .equals("SALES_EXECUTIVE");

        long totalCustomers;

        long totalLeads;

        long openLeads;

        long totalOpportunities;

        long openOpportunities;

        long wonOpportunities;

        long lostOpportunities;

        BigDecimal pipelineValue;

        List<Lead> leads;

        List<Opportunity> opportunities;

        if (salesExecutive) {

            var customers =
                    customerRepository
                            .findByAssignedToId(
                                    current.getId()
                            );

            leads =
                    leadRepository.findByAssignedToId(
                            current.getId()
                    );

            opportunities =
                    opportunityRepository
                            .findByAssignedToId(
                                    current.getId()
                            );

            totalCustomers =
                    customers.size();

            totalLeads =
                    leads.size();

            totalOpportunities =
                    opportunities.size();

        } else {

            totalCustomers =
                    customerRepository.count();

            leads =
                    leadRepository.findAll();

            opportunities =
                    opportunityRepository.findAll();

            totalLeads =
                    leads.size();

            totalOpportunities =
                    opportunities.size();
        }

        openLeads =
                leads.stream()
                        .filter(this::isOpenLead)
                        .count();

        openOpportunities =
                opportunities.stream()
                        .filter(o ->
                                o.getStatus()
                                        == OpportunityStatus.OPEN
                        )
                        .count();

        wonOpportunities =
                opportunities.stream()
                        .filter(o ->
                                o.getStatus()
                                        == OpportunityStatus.WON
                        )
                        .count();

        lostOpportunities =
                opportunities.stream()
                        .filter(o ->
                                o.getStatus()
                                        == OpportunityStatus.LOST
                        )
                        .count();

        pipelineValue =
                opportunities.stream()
                        .filter(o ->
                                o.getStatus()
                                        == OpportunityStatus.OPEN
                        )
                        .map(Opportunity::getAmount)
                        .filter(Objects::nonNull)
                        .reduce(
                                BigDecimal.ZERO,
                                BigDecimal::add
                        );

        return new DashboardResponse(
                totalCustomers,
                totalLeads,
                openLeads,
                totalOpportunities,
                openOpportunities,
                wonOpportunities,
                lostOpportunities,
                pipelineValue,
                buildLeadStatusChart(leads),
                buildOpportunityPipelineChart(
                        opportunities
                ),
                buildMonthlySalesChart(
                        opportunities
                )
        );
    }

    private boolean isOpenLead(Lead lead) {

        return lead.getStatus() != LeadStatus.CONVERTED
                && lead.getStatus() != LeadStatus.LOST
                && lead.getStatus() != LeadStatus.UNQUALIFIED;
    }

    private List<ChartData> buildLeadStatusChart(
            List<Lead> leads
    ) {

        List<ChartData> result =
                new ArrayList<>();

        for (LeadStatus status :
                LeadStatus.values()) {

            long count =
                    leads.stream()
                            .filter(lead ->
                                    lead.getStatus()
                                            == status)
                            .count();

            result.add(
                    new ChartData(
                            status.name(),
                            BigDecimal.valueOf(count)
                    )
            );
        }

        return result;
    }

    private List<ChartData>
    buildOpportunityPipelineChart(
            List<Opportunity> opportunities
    ) {

        List<ChartData> result =
                new ArrayList<>();

        for (OpportunityStage stage :
                OpportunityStage.values()) {

            BigDecimal total =
                    opportunities.stream()
                            .filter(o ->
                                    o.getStage()
                                            == stage)
                            .map(Opportunity::getAmount)
                            .filter(Objects::nonNull)
                            .reduce(
                                    BigDecimal.ZERO,
                                    BigDecimal::add
                            );

            result.add(
                    new ChartData(
                            stage.name(),
                            total
                    )
            );
        }

        return result;
    }

    private List<ChartData>
    buildMonthlySalesChart(
            List<Opportunity> opportunities
    ) {

        Map<String, BigDecimal> monthly =
                new LinkedHashMap<>();

        LocalDate now =
                LocalDate.now();

        for (int i = 5; i >= 0; i--) {

            LocalDate month =
                    now.minusMonths(i);

            String key =
                    month.getMonth()
                            .getDisplayName(
                                    TextStyle.SHORT,
                                    Locale.ENGLISH
                            );

            monthly.put(
                    key,
                    BigDecimal.ZERO
            );
        }

        for (Opportunity opportunity :
                opportunities) {

            if (opportunity.getCreatedDate() == null) {
                continue;
            }

            LocalDate date =
                    opportunity.getCreatedDate()
                            .toLocalDate();

            String key =
                    date.getMonth()
                            .getDisplayName(
                                    TextStyle.SHORT,
                                    Locale.ENGLISH
                            );

            if (monthly.containsKey(key)
                    && opportunity.getStatus()
                    == OpportunityStatus.WON) {

                monthly.put(
                        key,
                        monthly.get(key)
                                .add(
                                        opportunity.getAmount()
                                )
                );
            }
        }

        return monthly.entrySet()
                .stream()
                .map(entry ->
                        new ChartData(
                                entry.getKey(),
                                entry.getValue()
                        )
                )
                .toList();
    }
}