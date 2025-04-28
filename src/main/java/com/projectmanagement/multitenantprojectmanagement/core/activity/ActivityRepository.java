package com.projectmanagement.multitenantprojectmanagement.core.activity;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ActivityRepository extends JpaRepository<Activity, UUID> {

    Optional<Activity> findByIdAndOrganization_Auth0Id(UUID id, String auth0Id);

    Page<Activity> findAllByEntityIdAndOrganization_Auth0Id(UUID entityId, String auth0Id, Pageable pageable);

    Page<Activity> findAllByProjectIdAndOrganizationId(UUID projectId, UUID organizationId, Pageable pageable);

}
