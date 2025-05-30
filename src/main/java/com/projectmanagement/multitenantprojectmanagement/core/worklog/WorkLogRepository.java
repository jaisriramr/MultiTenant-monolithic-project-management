package com.projectmanagement.multitenantprojectmanagement.core.worklog;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WorkLogRepository extends JpaRepository<WorkLog, UUID> {
    Optional<WorkLog> findByIdAndOrganization_Auth0Id(UUID id, String auth0Id);
}
