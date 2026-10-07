package com.acxiomcrm.service;

import com.acxiomcrm.dto.OpportunityRequest;
import com.acxiomcrm.dto.OpportunityResponse;
import com.acxiomcrm.dto.PageResponse;
import com.acxiomcrm.entity.AppUser;
import com.acxiomcrm.entity.Customer;
import com.acxiomcrm.entity.Lead;
import com.acxiomcrm.entity.Opportunity;
import com.acxiomcrm.enums.OpportunityStage;
import com.acxiomcrm.enums.OpportunityStatus;
import com.acxiomcrm.mapper.OpportunityMapper;
import com.acxiomcrm.repository.AppUserRepository;
import com.acxiomcrm.repository.CustomerRepository;
import com.acxiomcrm.repository.LeadRepository;
import com.acxiomcrm.repository.OpportunityRepository;
import com.acxiomcrm.specification.OpportunitySpecification;
import com.acxiomcrm.util.PageResponseMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;

@Service
public class OpportunityService {

    private final OpportunityRepository opportunityRepository;
    private final CustomerRepository customerRepository;
    private final LeadRepository leadRepository;
    private final AppUserRepository userRepository;
    private final CurrentUserService currentUserService;
    private final AuditService auditService;
    private final OpportunityMapper opportunityMapper;

    public OpportunityService(
            OpportunityRepository opportunityRepository,
            CustomerRepository customerRepository,
            LeadRepository leadRepository,
            AppUserRepository userRepository,
            CurrentUserService currentUserService,
            AuditService auditService,
            OpportunityMapper opportunityMapper
    ) {
        this.opportunityRepository = opportunityRepository;
        this.customerRepository = customerRepository;
        this.leadRepository = leadRepository;
        this.userRepository = userRepository;
        this.currentUserService = currentUserService;
        this.auditService = auditService;
        this.opportunityMapper = opportunityMapper;
    }

    public PageResponse<OpportunityResponse>
    searchOpportunities(

            String search,

            OpportunityStage stage,

            OpportunityStatus status,

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
                OpportunitySpecification.search(
                        search,
                        stage,
                        status,
                        assignedToId
                );

        Page<Opportunity> page =
                opportunityRepository.findAll(
                        specification,
                        pageable
                );

        return PageResponseMapper.map(
                page,
                opportunityMapper::toResponse
        );
    }

    @Transactional
    public Opportunity create(
            OpportunityRequest request
    ) {

        validateBusinessRules(request);

        AppUser currentUser =
                currentUserService.getCurrentUser();

        Opportunity opportunity =
                new Opportunity();

        opportunity.setOpportunityName(
                request.opportunityName()
        );

        opportunity.setAmount(
                request.amount()
        );

        opportunity.setStage(
                request.stage()
        );

        opportunity.setProbability(
                request.probability()
        );

        opportunity.setExpectedCloseDate(
                request.expectedCloseDate()
        );

        opportunity.setStatus(
                request.status()
        );

        opportunity.setNotes(
                request.notes()
        );

        if (request.customerId() != null) {

            Customer customer =
                    customerRepository.findById(
                            request.customerId()
                    ).orElseThrow(() ->
                            new IllegalArgumentException(
                                    "Customer not found"
                            )
                    );

            opportunity.setCustomer(customer);
        }

        if (request.leadId() != null) {

            Lead lead =
                    leadRepository.findById(
                            request.leadId()
                    ).orElseThrow(() ->
                            new IllegalArgumentException(
                                    "Lead not found"
                            )
                    );

            opportunity.setLead(lead);
        }

        if (currentUser.getRole().name()
                .equals("SALES_EXECUTIVE")) {

            opportunity.setAssignedTo(currentUser);

        } else if (request.assignedToId() != null) {

            AppUser assignedUser =
                    userRepository.findById(
                            request.assignedToId()
                    ).orElseThrow(() ->
                            new IllegalArgumentException(
                                    "Assigned user not found"
                            )
                    );

            opportunity.setAssignedTo(
                    assignedUser
            );

        } else {

            opportunity.setAssignedTo(currentUser);
        }

        Opportunity saved =
                opportunityRepository.save(
                        opportunity
                );

        auditService.log(
                currentUser,
                "CREATE",
                "OPPORTUNITY",
                saved.getId(),
                null,
                saved.getOpportunityName(),
                null
        );

        return saved;
    }

    public List<Opportunity> getAll() {

        AppUser currentUser =
                currentUserService.getCurrentUser();

        if (currentUser.getRole().name()
                .equals("SALES_EXECUTIVE")) {

            return opportunityRepository
                    .findByAssignedToId(
                            currentUser.getId()
                    );
        }

        return opportunityRepository.findAll();
    }

    public Opportunity getById(Long id) {

        Opportunity opportunity =
                opportunityRepository.findById(id)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Opportunity not found"
                                )
                        );

        checkAccess(opportunity);

        return opportunity;
    }

    @Transactional
    public Opportunity update(
            Long id,
            OpportunityRequest request
    ) {

        validateBusinessRules(request);

        Opportunity opportunity =
                getById(id);

        opportunity.setOpportunityName(
                request.opportunityName()
        );

        opportunity.setAmount(
                request.amount()
        );

        opportunity.setStage(
                request.stage()
        );

        opportunity.setProbability(
                request.probability()
        );

        opportunity.setExpectedCloseDate(
                request.expectedCloseDate()
        );

        opportunity.setStatus(
                request.status()
        );

        opportunity.setNotes(
                request.notes()
        );

        if (request.customerId() != null) {

            Customer customer =
                    customerRepository.findById(
                            request.customerId()
                    ).orElseThrow(() ->
                            new IllegalArgumentException(
                                    "Customer not found"
                            )
                    );

            opportunity.setCustomer(customer);
        }

        if (request.leadId() != null) {

            Lead lead =
                    leadRepository.findById(
                            request.leadId()
                    ).orElseThrow(() ->
                            new IllegalArgumentException(
                                    "Lead not found"
                            )
                    );

            opportunity.setLead(lead);
        }

        AppUser currentUser =
                currentUserService.getCurrentUser();

        if (!currentUser.getRole().name()
                .equals("SALES_EXECUTIVE")
                && request.assignedToId() != null) {

            AppUser assigned =
                    userRepository.findById(
                            request.assignedToId()
                    ).orElseThrow(() ->
                            new IllegalArgumentException(
                                    "Assigned user not found"
                            )
                    );

            opportunity.setAssignedTo(assigned);
        }

        Opportunity saved =
                opportunityRepository.save(
                        opportunity
                );

        auditService.log(
                currentUser,
                "UPDATE",
                "OPPORTUNITY",
                id,
                null,
                saved.getStage().name(),
                null
        );

        return saved;
    }

    @Transactional
    public void delete(Long id) {

        Opportunity opportunity =
                getById(id);

        opportunity.setStatus(
                OpportunityStatus.LOST
        );

        opportunity.setStage(
                OpportunityStage.LOST
        );

        opportunityRepository.save(
                opportunity
        );

        auditService.log(
                currentUserService.getCurrentUser(),
                "DELETE",
                "OPPORTUNITY",
                id,
                "OPEN",
                "LOST",
                null
        );
    }

    public BigDecimal calculateWeightedPipeline(
            Opportunity opportunity
    ) {

        return opportunity
                .getAmount()
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

    private void validateBusinessRules(
            OpportunityRequest request
    ) {

        if (request.amount() == null ||
                request.amount()
                        .compareTo(BigDecimal.ZERO) <= 0) {

            throw new IllegalArgumentException(
                    "Opportunity Amount must be greater than 0."
            );
        }

        if (request.probability() == null ||
                request.probability() < 0 ||
                request.probability() > 100) {

            throw new IllegalArgumentException(
                    "Probability must be between 0 and 100."
            );
        }

        if (request.status() == OpportunityStatus.OPEN &&
                request.expectedCloseDate()
                        .isBefore(LocalDate.now())) {

            throw new IllegalArgumentException(
                    "Expected Close Date cannot be in the past."
            );
        }

        if (request.customerId() == null &&
                request.leadId() == null) {

            throw new IllegalArgumentException(
                    "Opportunity must be linked to a customer or lead."
            );
        }
    }

    private void checkAccess(
            Opportunity opportunity
    ) {

        AppUser current =
                currentUserService.getCurrentUser();

        if (current.getRole().name()
                .equals("SALES_EXECUTIVE")) {

            if (opportunity.getAssignedTo() == null ||
                    !opportunity.getAssignedTo()
                            .getId()
                            .equals(current.getId())) {

                throw new SecurityException(
                        "You do not have access to this opportunity"
                );
            }
        }
    }
}