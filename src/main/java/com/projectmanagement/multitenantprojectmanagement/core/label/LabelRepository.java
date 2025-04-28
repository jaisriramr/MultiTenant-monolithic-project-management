package com.projectmanagement.multitenantprojectmanagement.core.label;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LabelRepository extends JpaRepository<Label, UUID> {

    Optional<Label> findByIdAndOrganization_Auth0Id(UUID id, String auth0Id);

    Page<Label> findAllByProjectIdAndOrganization_Auth0Id(UUID projectId, String auth0Id,Pageable pageable);
}
