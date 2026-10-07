package com.acxiomcrm.repository;

import com.acxiomcrm.entity.FollowUp;
import com.acxiomcrm.enums.FollowUpStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.time.LocalDate;
import java.util.List;

public interface FollowUpRepository
        extends JpaRepository<FollowUp, Long>,
        JpaSpecificationExecutor<FollowUp> {

    List<FollowUp> findByAssignedToId(
            Long userId
    );

    List<FollowUp> findByStatus(
            FollowUpStatus status
    );

    List<FollowUp>
    findByFollowUpDateBeforeAndStatus(
            LocalDate date,
            FollowUpStatus status
    );

    long countByStatus(
            FollowUpStatus status
    );
}