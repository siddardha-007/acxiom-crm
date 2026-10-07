package com.acxiomcrm.service;

import com.acxiomcrm.dto.ActivityRequest;
import com.acxiomcrm.entity.Activity;
import com.acxiomcrm.entity.AppUser;
import com.acxiomcrm.entity.Customer;
import com.acxiomcrm.entity.Lead;
import com.acxiomcrm.repository.ActivityRepository;
import com.acxiomcrm.repository.AppUserRepository;
import com.acxiomcrm.repository.CustomerRepository;
import com.acxiomcrm.repository.LeadRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ActivityService {

    private final ActivityRepository activityRepository;
    private final CustomerRepository customerRepository;
    private final LeadRepository leadRepository;
    private final AppUserRepository userRepository;
    private final CurrentUserService currentUserService;
    private final AuditService auditService;

    public ActivityService(
            ActivityRepository activityRepository,
            CustomerRepository customerRepository,
            LeadRepository leadRepository,
            AppUserRepository userRepository,
            CurrentUserService currentUserService,
            AuditService auditService
    ) {
        this.activityRepository = activityRepository;
        this.customerRepository = customerRepository;
        this.leadRepository = leadRepository;
        this.userRepository = userRepository;
        this.currentUserService = currentUserService;
        this.auditService = auditService;
    }

    @Transactional
    public Activity create(
            ActivityRequest request
    ) {

        validate(request);

        AppUser current =
                currentUserService.getCurrentUser();

        Activity activity =
                new Activity();

        activity.setActivityType(
                request.activityType()
        );

        activity.setSubject(
                request.subject()
        );

        activity.setDescription(
                request.description()
        );

        activity.setActivityDate(
                request.activityDate()
        );

        activity.setStatus(
                request.status()
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

            activity.setCustomer(customer);
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

            activity.setLead(lead);
        }

        if (current.getRole().name()
                .equals("SALES_EXECUTIVE")) {

            activity.setAssignedTo(current);

        } else if (request.assignedToId() != null) {

            AppUser assigned =
                    userRepository.findById(
                            request.assignedToId()
                    ).orElseThrow(() ->
                            new IllegalArgumentException(
                                    "Assigned user not found"
                            )
                    );

            activity.setAssignedTo(assigned);

        } else {

            activity.setAssignedTo(current);
        }

        Activity saved =
                activityRepository.save(activity);

        auditService.log(
                current,
                "CREATE",
                "ACTIVITY",
                saved.getId(),
                null,
                saved.getSubject(),
                null
        );

        return saved;
    }

    public List<Activity> getAll() {

        AppUser current =
                currentUserService.getCurrentUser();

        if (current.getRole().name()
                .equals("SALES_EXECUTIVE")) {

            return activityRepository
                    .findByAssignedToId(
                            current.getId()
                    );
        }

        return activityRepository.findAll();
    }

    private void validate(
            ActivityRequest request
    ) {

        if (request.customerId() == null &&
                request.leadId() == null) {

            throw new IllegalArgumentException(
                    "Activity must be linked to a customer or lead."
            );
        }
    }
}