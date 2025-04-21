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
    Optional<Roles> findByName(String name);
    
    @NonNull
    @Override
    Page<Roles> findAll(@NonNull Pageable pageable);

    Optional<Roles> findByAuth0Id(String auth0Id);

    List<Roles> findAllByAuth0IdIn(List<String> auth0Ids);

    List<Roles> findAllByOrganizationId(String orgId);
    
}
