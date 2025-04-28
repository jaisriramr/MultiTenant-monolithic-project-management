package com.projectmanagement.multitenantprojectmanagement.roles;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

@Repository
public interface RolesRepository extends JpaRepository<Roles, UUID> {
    Optional<Roles> findByIdAndOrganization_Auth0Id(UUID id, String auth0OrgId);
    
    Optional<Roles> findByNameAndOrganization_Auth0Id(String name, String auth0OrgId);
    
    @NonNull
    @Override
    Page<Roles> findAll(@NonNull Pageable pageable);

    Optional<Roles> findByAuth0IdAndOrganization_Auth0Id(String auth0Id, String auth0OrgId);

    List<Roles> findAllByAuth0IdIn(List<String> auth0Ids);

    List<Roles> findAllByOrganization_Auth0Id(String orgId);
    
}
