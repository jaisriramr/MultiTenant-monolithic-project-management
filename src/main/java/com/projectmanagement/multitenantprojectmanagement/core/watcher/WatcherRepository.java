package com.projectmanagement.multitenantprojectmanagement.core.watcher;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WatcherRepository extends JpaRepository<Watcher, UUID> {
    Optional<Watcher> findByIdAndOrganization_Auth0Id(UUID id, String auth0Id);

    List<Watcher> findAllByIssueIdAndOrganization_Auth0Id(UUID issueId, String auth0Id);
}
