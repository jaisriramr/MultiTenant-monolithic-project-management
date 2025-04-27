package com.projectmanagement.multitenantprojectmanagement.core.activity;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ActivityRepository extends JpaRepository<Activity, UUID> {
    Page<Activity> findAllByEntityId(UUID entityId, Pageable pageable);

    Page<Activity> findAllByProjectIdAndOrganizationId(UUID projectId, UUID organizationId, Pageable pageable);

}
