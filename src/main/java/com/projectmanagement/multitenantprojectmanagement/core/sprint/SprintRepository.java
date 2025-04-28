package com.projectmanagement.multitenantprojectmanagement.core.sprint;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;


@Repository
public interface SprintRepository extends JpaRepository<Sprint, UUID> {

    Optional<Sprint> findByIdAndOrganization_Auth0Id(UUID id, String auth0Id);

    Optional<Sprint> findByNameAndProjectIdAndOrganization_Auth0Id(String name,UUID projectId,String auth0Id);

    Page<Sprint> findAllByProjectIdAndOrganization_Auth0Id(UUID projectId, String auth0Id, Pageable pageable);

}
