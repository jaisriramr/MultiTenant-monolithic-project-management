package com.projectmanagement.multitenantprojectmanagement.roles;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import io.micrometer.common.lang.NonNull;

@Repository
public interface RolesRepository extends JpaRepository<Roles, UUID> {
    Optional<Roles> findByName(String name);
    
    @Override
    @NonNull
    Page<Roles> findAll(Pageable pageable);

    Optional<Roles> findByAuth0Id(String auth0Id);

    List<Roles> findAllByAuth0IdIn(List<String> auth0Ids);

    List<Roles> findAllByOrganizationId(String orgId);
    
}
