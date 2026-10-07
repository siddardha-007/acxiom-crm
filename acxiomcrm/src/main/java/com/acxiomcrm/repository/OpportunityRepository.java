package com.acxiomcrm.repository;

import com.acxiomcrm.entity.Opportunity;
import com.acxiomcrm.enums.OpportunityStage;
import com.acxiomcrm.enums.OpportunityStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.util.List;

public interface OpportunityRepository
        extends JpaRepository<Opportunity, Long>,
        JpaSpecificationExecutor<Opportunity> {

    List<Opportunity> findByAssignedToId(
            Long userId
    );

    List<Opportunity> findByStatus(
            OpportunityStatus status
    );

    List<Opportunity> findByStage(
            OpportunityStage stage
    );

    long countByStatus(
            OpportunityStatus status
    );

    @Query("""
           SELECT COALESCE(SUM(o.amount), 0)
           FROM Opportunity o
           WHERE o.status = :status
           """)
    BigDecimal sumAmountByStatus(
            OpportunityStatus status
    );
}