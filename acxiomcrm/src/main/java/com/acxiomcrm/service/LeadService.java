package com.acxiomcrm.service;


import com.acxiomcrm.dto.LeadResponse;
import com.acxiomcrm.dto.PageResponse;
import com.acxiomcrm.mapper.LeadMapper;
import com.acxiomcrm.specification.LeadSpecification;
import com.acxiomcrm.util.PageResponseMapper;
import com.acxiomcrm.enums.LeadStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.acxiomcrm.dto.LeadConversionRequest;
import com.acxiomcrm.dto.LeadConversionResponse;
import com.acxiomcrm.entity.Customer;
import com.acxiomcrm.entity.Opportunity;
import com.acxiomcrm.enums.CustomerStatus;
import com.acxiomcrm.enums.OpportunityStage;
import com.acxiomcrm.enums.OpportunityStatus;
import com.acxiomcrm.repository.CustomerRepository;
import com.acxiomcrm.repository.OpportunityRepository;
import com.acxiomcrm.dto.LeadRequest;
import com.acxiomcrm.entity.AppUser;
import com.acxiomcrm.entity.Lead;
import com.acxiomcrm.enums.LeadStatus;
import com.acxiomcrm.repository.AppUserRepository;
import com.acxiomcrm.repository.LeadRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class LeadService {

    private final LeadRepository leadRepository;
    private final AppUserRepository userRepository;
    private final CurrentUserService currentUserService;
    private final AuditService auditService;
    private final CustomerRepository customerRepository;
    private final OpportunityRepository opportunityRepository;
    private final LeadMapper leadMapper;

    public LeadService(
            LeadRepository leadRepository,
            AppUserRepository userRepository,
            CurrentUserService currentUserService,
            AuditService auditService,
            CustomerRepository customerRepository,
            OpportunityRepository opportunityRepository,
            LeadMapper leadMapper
    ) {
        this.leadRepository = leadRepository;
        this.userRepository = userRepository;
        this.currentUserService = currentUserService;
        this.auditService = auditService;
        this.customerRepository = customerRepository;
        this.opportunityRepository = opportunityRepository;
        this.leadMapper = leadMapper;
    }

    @Transactional
    public LeadConversionResponse convertLead(
            Long leadId,
            LeadConversionRequest request
    ) {

        Lead lead =
                leadRepository.findById(leadId)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Lead not found"
                                )
                        );

        checkAccess(lead);

        if (lead.getStatus() != LeadStatus.QUALIFIED) {

            throw new IllegalArgumentException(
                    "Only qualified leads can be converted"
            );
        }

        if (request.expectedCloseDate()
                .isBefore(java.time.LocalDate.now())) {

            throw new IllegalArgumentException(
                    "Expected Close Date cannot be in the past"
            );
        }

        AppUser current =
                currentUserService.getCurrentUser();

        /*
         * Check whether customer already exists.
         */
        Customer customer =
                customerRepository
                        .findByEmail(lead.getEmail())
                        .orElse(null);

        if (customer == null) {

            if (customerRepository.existsByPhone(
                    lead.getPhone()
            )) {

                throw new IllegalArgumentException(
                        "A customer with this phone already exists"
                );
            }

            customer = new Customer();

            customer.setCustomerCode(
                    "CUS-" + System.currentTimeMillis()
            );

            customer.setCustomerName(
                    lead.getLeadName()
            );

            customer.setEmail(
                    lead.getEmail()
            );

            customer.setPhone(
                    lead.getPhone()
            );

            customer.setCompanyName(
                    lead.getCompanyName()
            );

            customer.setAddress(
                    request.customerAddress()
            );

            customer.setCity(
                    request.city()
            );

            customer.setState(
                    request.state()
            );

            customer.setStatus(
                    CustomerStatus.ACTIVE
            );

            customer.setCreatedBy(current);

            customer.setAssignedTo(
                    lead.getAssignedTo() != null
                            ? lead.getAssignedTo()
                            : current
            );

            customer =
                    customerRepository.save(customer);
        }

        /*
         * Create Opportunity.
         */
        Opportunity opportunity =
                new Opportunity();

        opportunity.setOpportunityName(
                request.opportunityName()
        );

        opportunity.setCustomer(customer);

        opportunity.setLead(lead);

        opportunity.setAmount(
                request.amount()
        );

        opportunity.setStage(
                OpportunityStage.QUALIFICATION
        );

        opportunity.setProbability(
                request.probability()
        );

        opportunity.setExpectedCloseDate(
                request.expectedCloseDate()
        );

        opportunity.setStatus(
                OpportunityStatus.OPEN
        );

        opportunity.setAssignedTo(
                lead.getAssignedTo() != null
                        ? lead.getAssignedTo()
                        : current
        );

        Opportunity savedOpportunity =
                opportunityRepository.save(
                        opportunity
                );

        /*
         * Mark lead converted.
         */
        lead.setStatus(
                LeadStatus.CONVERTED
        );

        leadRepository.save(lead);

        auditService.log(
                current,
                "CONVERT",
                "LEAD",
                leadId,
                "QUALIFIED",
                "CONVERTED",
                null
        );

        auditService.log(
                current,
                "CREATE",
                "OPPORTUNITY",
                savedOpportunity.getId(),
                null,
                "Created from lead " + leadId,
                null
        );

        return new LeadConversionResponse(
                lead.getId(),
                customer.getId(),
                savedOpportunity.getId(),
                "Lead successfully converted"
        );
    }

    public PageResponse<LeadResponse> searchLeads(
            String search,
            LeadStatus status,
            Long assignedToId,
            Pageable pageable
    ) {

        AppUser current =
                currentUserService.getCurrentUser();

        if (current.getRole().name()
                .equals("SALES_EXECUTIVE")) {

            assignedToId =
                    current.getId();
        }

        var specification =
                LeadSpecification.search(
                        search,
                        status,
                        assignedToId
                );

        Page<Lead> page =
                leadRepository.findAll(
                        specification,
                        pageable
                );

        return PageResponseMapper.map(
                page,
                leadMapper::toResponse
        );
    }

    @Transactional
    public Lead create(LeadRequest request) {

        AppUser currentUser =
                currentUserService.getCurrentUser();

        Lead lead = new Lead();

        lead.setLeadCode(generateLeadCode());
        lead.setLeadName(request.leadName());
        lead.setEmail(request.email());
        lead.setPhone(request.phone());
        lead.setCompanyName(request.companyName());
        lead.setSource(request.source());
        lead.setStatus(request.status());
        lead.setPriority(request.priority());
        lead.setExpectedValue(request.expectedValue());

        /*
         * Sales Executive cannot assign leads
         * to somebody else.
         */
        if (currentUser.getRole().name()
                .equals("SALES_EXECUTIVE")) {

            lead.setAssignedTo(currentUser);

        } else if (request.assignedToId() != null) {

            AppUser assignedUser =
                    userRepository.findById(
                            request.assignedToId()
                    ).orElseThrow(() ->
                            new IllegalArgumentException(
                                    "Assigned user not found"
                            )
                    );

            lead.setAssignedTo(assignedUser);

        } else {

            lead.setAssignedTo(currentUser);
        }

        Lead saved =
                leadRepository.save(lead);

        auditService.log(
                currentUser,
                "CREATE",
                "LEAD",
                saved.getId(),
                null,
                saved.getLeadName(),
                null
        );

        return saved;
    }

    public List<Lead> getAll() {

        AppUser currentUser =
                currentUserService.getCurrentUser();

        if (currentUser.getRole().name()
                .equals("SALES_EXECUTIVE")) {

            return leadRepository
                    .findByAssignedToId(
                            currentUser.getId()
                    );
        }

        return leadRepository.findAll();
    }

    public Lead getById(Long id) {

        Lead lead =
                leadRepository.findById(id)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Lead not found"
                                )
                        );

        checkAccess(lead);

        return lead;
    }

    @Transactional
    public Lead update(
            Long id,
            LeadRequest request
    ) {

        Lead lead = getById(id);

        validateStatusTransition(
                lead.getStatus(),
                request.status()
        );

        lead.setLeadName(request.leadName());
        lead.setEmail(request.email());
        lead.setPhone(request.phone());
        lead.setCompanyName(request.companyName());
        lead.setSource(request.source());
        lead.setStatus(request.status());
        lead.setPriority(request.priority());
        lead.setExpectedValue(request.expectedValue());

        AppUser currentUser =
                currentUserService.getCurrentUser();

        if (!currentUser.getRole().name()
                .equals("SALES_EXECUTIVE")
                && request.assignedToId() != null) {

            AppUser assignedUser =
                    userRepository.findById(
                            request.assignedToId()
                    ).orElseThrow(() ->
                            new IllegalArgumentException(
                                    "Assigned user not found"
                            )
                    );

            lead.setAssignedTo(assignedUser);
        }

        Lead saved =
                leadRepository.save(lead);

        auditService.log(
                currentUser,
                "UPDATE",
                "LEAD",
                id,
                null,
                saved.getStatus().name(),
                null
        );

        return saved;
    }

    @Transactional
    public void delete(Long id) {

        Lead lead = getById(id);

        lead.setStatus(LeadStatus.LOST);

        leadRepository.save(lead);

        auditService.log(
                currentUserService.getCurrentUser(),
                "DELETE",
                "LEAD",
                id,
                null,
                "LOST",
                null
        );
    }

    public List<Lead> search(String search) {

        if (search == null || search.isBlank()) {
            return getAll();
        }

        AppUser currentUser =
                currentUserService.getCurrentUser();

        if (currentUser.getRole().name()
                .equals("SALES_EXECUTIVE")) {

            return leadRepository
                    .findByLeadNameContainingIgnoreCase(
                            search
                    )
                    .stream()
                    .filter(lead ->
                            lead.getAssignedTo() != null
                                    &&
                                    lead.getAssignedTo()
                                            .getId()
                                            .equals(currentUser.getId())
                    )
                    .toList();
        }

        return leadRepository
                .findByLeadNameContainingIgnoreCase(
                        search
                );
    }

    private void checkAccess(Lead lead) {

        AppUser currentUser =
                currentUserService.getCurrentUser();

        if (currentUser.getRole().name()
                .equals("SALES_EXECUTIVE")) {

            if (lead.getAssignedTo() == null ||
                    !lead.getAssignedTo()
                            .getId()
                            .equals(currentUser.getId())) {

                throw new SecurityException(
                        "You do not have access to this lead"
                );
            }
        }
    }

    private void validateStatusTransition(
            LeadStatus current,
            LeadStatus next
    ) {

        if (current == LeadStatus.CONVERTED &&
                next != LeadStatus.CONVERTED) {

            throw new IllegalArgumentException(
                    "A converted lead cannot be moved to another status"
            );
        }

        if (current == LeadStatus.LOST &&
                next != LeadStatus.LOST) {

            throw new IllegalArgumentException(
                    "A lost lead cannot be reopened"
            );
        }
    }

    private String generateLeadCode() {

        return "LEAD-" +
                System.currentTimeMillis();
    }
}