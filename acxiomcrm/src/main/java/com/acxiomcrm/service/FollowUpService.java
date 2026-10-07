package com.acxiomcrm.service;

import com.acxiomcrm.dto.FollowUpRequest;
import com.acxiomcrm.dto.FollowUpResponse;
import com.acxiomcrm.dto.PageResponse;
import com.acxiomcrm.entity.AppUser;
import com.acxiomcrm.entity.Customer;
import com.acxiomcrm.entity.FollowUp;
import com.acxiomcrm.entity.Lead;
import com.acxiomcrm.enums.FollowUpStatus;
import com.acxiomcrm.mapper.FollowUpMapper;
import com.acxiomcrm.repository.AppUserRepository;
import com.acxiomcrm.repository.CustomerRepository;
import com.acxiomcrm.repository.FollowUpRepository;
import com.acxiomcrm.repository.LeadRepository;
import com.acxiomcrm.specification.FollowUpSpecification;
import com.acxiomcrm.util.PageResponseMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FollowUpService {

    private final FollowUpRepository followUpRepository;
    private final CustomerRepository customerRepository;
    private final LeadRepository leadRepository;
    private final AppUserRepository userRepository;
    private final CurrentUserService currentUserService;
    private final AuditService auditService;

    private final FollowUpMapper followUpMapper;



    @Transactional
    public FollowUp create(
            FollowUpRequest request
    ) {

        validateBusinessRules(request);

        AppUser currentUser =
                currentUserService.getCurrentUser();

        FollowUp followUp =
                new FollowUp();

        if (request.customerId() != null) {

            Customer customer =
                    customerRepository.findById(
                            request.customerId()
                    ).orElseThrow(() ->
                            new IllegalArgumentException(
                                    "Customer not found"
                            )
                    );

            followUp.setCustomer(customer);
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

            followUp.setLead(lead);
        }

        followUp.setFollowUpDate(
                request.followUpDate()
        );

        followUp.setFollowUpType(
                request.followUpType()
        );

        followUp.setSubject(
                request.subject()
        );

        followUp.setRemarks(
                request.remarks()
        );

        followUp.setStatus(
                request.status()
        );

        if (currentUser.getRole().name()
                .equals("SALES_EXECUTIVE")) {

            followUp.setAssignedTo(
                    currentUser
            );

        } else if (request.assignedToId() != null) {

            AppUser assigned =
                    userRepository.findById(
                            request.assignedToId()
                    ).orElseThrow(() ->
                            new IllegalArgumentException(
                                    "Assigned user not found"
                            )
                    );

            followUp.setAssignedTo(
                    assigned
            );

        } else {

            followUp.setAssignedTo(
                    currentUser
            );
        }

        FollowUp saved =
                followUpRepository.save(
                        followUp
                );

        auditService.log(
                currentUser,
                "CREATE",
                "FOLLOW_UP",
                saved.getId(),
                null,
                saved.getSubject(),
                null
        );

        return saved;
    }

    public List<FollowUp> getAll() {

        AppUser current =
                currentUserService.getCurrentUser();

        if (current.getRole().name()
                .equals("SALES_EXECUTIVE")) {

            return followUpRepository
                    .findByAssignedToId(
                            current.getId()
                    );
        }

        return followUpRepository.findAll();
    }

    public FollowUp getById(Long id) {

        FollowUp followUp =
                followUpRepository.findById(id)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Follow-up not found"
                                )
                        );

        checkAccess(followUp);

        return followUp;
    }

    @Transactional
    public FollowUp update(
            Long id,
            FollowUpRequest request
    ) {

        validateBusinessRules(request);

        FollowUp followUp =
                getById(id);

        if (request.customerId() != null) {

            Customer customer =
                    customerRepository.findById(
                            request.customerId()
                    ).orElseThrow(() ->
                            new IllegalArgumentException(
                                    "Customer not found"
                            )
                    );

            followUp.setCustomer(customer);
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

            followUp.setLead(lead);
        }

        followUp.setFollowUpDate(
                request.followUpDate()
        );

        followUp.setFollowUpType(
                request.followUpType()
        );

        followUp.setSubject(
                request.subject()
        );

        followUp.setRemarks(
                request.remarks()
        );

        followUp.setStatus(
                request.status()
        );

        AppUser current =
                currentUserService.getCurrentUser();

        if (!current.getRole().name()
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

            followUp.setAssignedTo(
                    assigned
            );
        }

        FollowUp saved =
                followUpRepository.save(
                        followUp
                );

        auditService.log(
                current,
                "UPDATE",
                "FOLLOW_UP",
                id,
                null,
                saved.getStatus().name(),
                null
        );

        return saved;
    }

    @Transactional
    public FollowUp complete(Long id) {

        FollowUp followUp =
                getById(id);

        followUp.setStatus(
                FollowUpStatus.COMPLETED
        );

        FollowUp saved =
                followUpRepository.save(
                        followUp
                );

        auditService.log(
                currentUserService.getCurrentUser(),
                "COMPLETE",
                "FOLLOW_UP",
                id,
                "PLANNED",
                "COMPLETED",
                null
        );

        return saved;
    }

    @Transactional
    public FollowUp cancel(Long id) {

        FollowUp followUp =
                getById(id);

        followUp.setStatus(
                FollowUpStatus.CANCELLED
        );

        return followUpRepository.save(
                followUp
        );
    }

    public List<FollowUp> getUpcoming() {

        AppUser current =
                currentUserService.getCurrentUser();

        List<FollowUp> followUps;

        if (current.getRole().name()
                .equals("SALES_EXECUTIVE")) {

            followUps =
                    followUpRepository
                            .findByAssignedToId(
                                    current.getId()
                            );

        } else {

            followUps =
                    followUpRepository.findAll();
        }

        LocalDate today =
                LocalDate.now();

        return followUps.stream()
                .filter(f ->
                        !f.getFollowUpDate()
                                .isBefore(today)
                )
                .filter(f ->
                        f.getStatus() ==
                                FollowUpStatus.PLANNED
                )
                .toList();
    }

    public List<FollowUp> getOverdue() {

        LocalDate today =
                LocalDate.now();

        return followUpRepository
                .findByFollowUpDateBeforeAndStatus(
                        today,
                        FollowUpStatus.PLANNED
                );
    }

    private void validateBusinessRules(
            FollowUpRequest request
    ) {

        if (request.customerId() == null &&
                request.leadId() == null) {

            throw new IllegalArgumentException(
                    "Follow-up must be linked to a customer or lead."
            );
        }

        if (request.followUpDate() == null) {

            throw new IllegalArgumentException(
                    "Follow-up date is required."
            );
        }

        if (request.status() ==
                FollowUpStatus.PLANNED &&
                request.followUpDate()
                        .isBefore(LocalDate.now())) {

            throw new IllegalArgumentException(
                    "Follow-up date cannot be earlier than today."
            );
        }
    }


    public PageResponse<FollowUpResponse>
    searchFollowUps(

            LocalDate date,

            FollowUpStatus status,

            Long assignedToId,

            Long customerId,

            Long leadId,

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
                FollowUpSpecification.search(
                        date,
                        status,
                        assignedToId,
                        customerId,
                        leadId
                );

        Page<FollowUp> page =
                followUpRepository.findAll(
                        specification,
                        pageable
                );

        return PageResponseMapper.map(
                page,
                followUpMapper::toResponse
        );
    }

    private void checkAccess(
            FollowUp followUp
    ) {

        AppUser current =
                currentUserService.getCurrentUser();

        if (current.getRole().name()
                .equals("SALES_EXECUTIVE")) {

            if (followUp.getAssignedTo() == null ||
                    !followUp.getAssignedTo()
                            .getId()
                            .equals(current.getId())) {

                throw new SecurityException(
                        "You do not have access to this follow-up"
                );
            }
        }
    }
}