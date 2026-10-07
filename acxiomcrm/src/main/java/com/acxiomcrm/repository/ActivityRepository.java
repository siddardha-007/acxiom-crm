package com.acxiomcrm.repository;

import com.acxiomcrm.entity.Activity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ActivityRepository
        extends JpaRepository<Activity, Long> {

    List<Activity> findByAssignedToId(Long userId);
}