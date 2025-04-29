package com.projectmanagement.multitenantprojectmanagement.core.project;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ProjectRepository extends JpaRepository<Projects, UUID> {
    @Query("SELECT p FROM Projects p WHERE p.id = :id AND p.organization.auth0Id = :auth0OrgId")
    Optional<Projects> findByIdAndOrganization_Auth0Id(UUID id, String auth0OrgId);

    Optional<Projects> findByName(String name);

    Page<Projects> findAllByOrganizationId(UUID orgId, Pageable pageable);

}
