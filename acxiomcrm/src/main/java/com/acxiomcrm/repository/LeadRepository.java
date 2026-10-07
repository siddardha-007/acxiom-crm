package com.acxiomcrm.repository;

import com.acxiomcrm.entity.Lead;
import com.acxiomcrm.enums.LeadStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface LeadRepository
        extends JpaRepository<Lead, Long>,
        JpaSpecificationExecutor<Lead> {

    List<Lead> findByLeadNameContainingIgnoreCase(
            String name
    );

    List<Lead> findByStatus(
            LeadStatus status
    );

    List<Lead> findByAssignedToId(
            Long userId
    );

    long countByStatus(
            LeadStatus status
    );
}