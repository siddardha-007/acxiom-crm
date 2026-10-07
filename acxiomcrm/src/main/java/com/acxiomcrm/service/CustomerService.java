package com.acxiomcrm.service;

import com.acxiomcrm.dto.CustomerResponse;
import com.acxiomcrm.dto.PageResponse;
import com.acxiomcrm.mapper.CustomerMapper;
import com.acxiomcrm.specification.CustomerSpecification;
import com.acxiomcrm.util.PageResponseMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.acxiomcrm.dto.CustomerRequest;
import com.acxiomcrm.entity.AppUser;
import com.acxiomcrm.entity.Customer;
import com.acxiomcrm.enums.CustomerStatus;
import com.acxiomcrm.repository.AppUserRepository;
import com.acxiomcrm.repository.CustomerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final AppUserRepository userRepository;
    private final CurrentUserService currentUserService;
    private final AuditService auditService;
    private final CustomerMapper customerMapper;

    public CustomerService(
            CustomerRepository customerRepository,
            AppUserRepository userRepository,
            CurrentUserService currentUserService,
            AuditService auditService,
            CustomerMapper customerMapper
    ) {
        this.customerRepository = customerRepository;
        this.userRepository = userRepository;
        this.currentUserService = currentUserService;
        this.auditService = auditService;
        this.customerMapper = customerMapper;
    }

    public PageResponse<CustomerResponse> searchCustomers(
            String search,
            Pageable pageable
    ) {

        AppUser current =
                currentUserService.getCurrentUser();

        var specification =
                CustomerSpecification.search(search);

        if (current.getRole().name()
                .equals("SALES_EXECUTIVE")) {

            specification =
                    specification.and(
                            (root, query, cb) ->
                                    cb.equal(
                                            root.get("assignedTo")
                                                    .get("id"),
                                            current.getId()
                                    )
                    );
        }

        Page<Customer> page =
                customerRepository.findAll(
                        specification,
                        pageable
                );

        return PageResponseMapper.map(
                page,
                customerMapper::toResponse
        );
    }

    @Transactional
    public Customer create(CustomerRequest request) {

        if (customerRepository.existsByEmail(
                request.email()
        )) {
            throw new IllegalArgumentException(
                    "Customer with this email already exists"
            );
        }

        if (customerRepository.existsByPhone(
                request.phone()
        )) {
            throw new IllegalArgumentException(
                    "Customer with this phone already exists"
            );
        }

        Customer customer = new Customer();

        customer.setCustomerCode(
                generateCustomerCode()
        );

        customer.setCustomerName(
                request.customerName()
        );

        customer.setEmail(
                request.email()
        );

        customer.setPhone(
                request.phone()
        );

        customer.setCompanyName(
                request.companyName()
        );

        customer.setAddress(
                request.address()
        );

        customer.setCity(
                request.city()
        );

        customer.setState(
                request.state()
        );

        customer.setStatus(
                request.status() == null
                        ? CustomerStatus.ACTIVE
                        : request.status()
        );

        AppUser currentUser =
                currentUserService.getCurrentUser();

        customer.setCreatedBy(currentUser);

        if (request.assignedToId() != null) {

            AppUser assignedUser =
                    userRepository.findById(
                            request.assignedToId()
                    ).orElseThrow(() ->
                            new IllegalArgumentException(
                                    "Assigned user not found"
                            )
                    );

            customer.setAssignedTo(assignedUser);

        } else {

            customer.setAssignedTo(currentUser);
        }

        Customer saved =
                customerRepository.save(customer);

        auditService.log(
                currentUser,
                "CREATE",
                "CUSTOMER",
                saved.getId(),
                null,
                saved.getCustomerName(),
                null
        );

        return saved;
    }

    public List<Customer> getAll() {

        AppUser user =
                currentUserService.getCurrentUser();

        return switch (user.getRole()) {

            case ADMIN, MANAGER ->
                    customerRepository.findAll();

            case SALES_EXECUTIVE ->
                    customerRepository
                            .findByAssignedToId(user.getId());
        };
    }

    public Customer getById(Long id) {

        Customer customer =
                customerRepository.findById(id)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Customer not found"
                                )
                        );

        checkAccess(customer);

        return customer;
    }

    @Transactional
    public Customer update(
            Long id,
            CustomerRequest request
    ) {

        Customer customer = getById(id);

        if (!customer.getEmail()
                .equals(request.email())
                && customerRepository.existsByEmail(
                request.email()
        )) {

            throw new IllegalArgumentException(
                    "Email already exists"
            );
        }

        if (!customer.getPhone()
                .equals(request.phone())
                && customerRepository.existsByPhone(
                request.phone()
        )) {

            throw new IllegalArgumentException(
                    "Phone already exists"
            );
        }

        customer.setCustomerName(
                request.customerName()
        );

        customer.setEmail(
                request.email()
        );

        customer.setPhone(
                request.phone()
        );

        customer.setCompanyName(
                request.companyName()
        );

        customer.setAddress(
                request.address()
        );

        customer.setCity(
                request.city()
        );

        customer.setState(
                request.state()
        );

        if (request.status() != null) {
            customer.setStatus(request.status());
        }

        if (request.assignedToId() != null) {

            AppUser assigned =
                    userRepository.findById(
                            request.assignedToId()
                    ).orElseThrow(() ->
                            new IllegalArgumentException(
                                    "Assigned user not found"
                            )
                    );

            customer.setAssignedTo(assigned);
        }

        Customer saved =
                customerRepository.save(customer);

        auditService.log(
                currentUserService.getCurrentUser(),
                "UPDATE",
                "CUSTOMER",
                id,
                null,
                saved.getCustomerName(),
                null
        );

        return saved;
    }

    @Transactional
    public void delete(Long id) {

        Customer customer = getById(id);

        // Deactivate rather than physical deletion.
        customer.setStatus(
                CustomerStatus.INACTIVE
        );

        customerRepository.save(customer);

        auditService.log(
                currentUserService.getCurrentUser(),
                "DELETE",
                "CUSTOMER",
                id,
                "ACTIVE",
                "INACTIVE",
                null
        );
    }

    public List<Customer> search(String name) {

        if (name == null || name.isBlank()) {
            return getAll();
        }

        return customerRepository
                .findByCustomerNameContainingIgnoreCase(name);
    }

    private void checkAccess(Customer customer) {

        AppUser current =
                currentUserService.getCurrentUser();

        if (current.getRole().name()
                .equals("SALES_EXECUTIVE")
                && (customer.getAssignedTo() == null
                || !customer.getAssignedTo()
                .getId()
                .equals(current.getId()))) {

            throw new SecurityException(
                    "You do not have access to this customer"
            );
        }
    }

    private String generateCustomerCode() {

        return "CUS-" +
                System.currentTimeMillis();
    }
}