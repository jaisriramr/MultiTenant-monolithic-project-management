package com.projectmanagement.multitenantprojectmanagement.core.epic;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EpicRepository extends JpaRepository<Epic, UUID> {

    Optional<Epic> findByIdAndOrganization_Auth0Id(UUID id, String auth0Id);

    Page<Epic> findAllByProjectId(UUID projectId, Pageable pageable);

}
