package com.acxiomcrm.repository;

import com.acxiomcrm.entity.Customer;
import com.acxiomcrm.enums.CustomerStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;

public interface CustomerRepository
        extends JpaRepository<Customer, Long>,
        JpaSpecificationExecutor<Customer> {

    boolean existsByEmail(String email);

    boolean existsByPhone(String phone);

    Optional<Customer> findByEmail(String email);

    List<Customer> findByCustomerNameContainingIgnoreCase(
            String name
    );

    List<Customer> findByStatus(
            CustomerStatus status
    );

    List<Customer> findByAssignedToId(
            Long userId
    );

    long countByStatus(
            CustomerStatus status
    );
}