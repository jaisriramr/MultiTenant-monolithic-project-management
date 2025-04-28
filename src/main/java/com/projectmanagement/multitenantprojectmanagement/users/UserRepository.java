package com.projectmanagement.multitenantprojectmanagement.users;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<Users, UUID> {
    Optional<Users> findByIdAndOrganization_Auth0Id(UUID id, String auth0IdOrgId);

    Optional<List<Users>> findAllByNameContainingIgnoreCaseAndIsDeletedFalseAndOrganization_Auth0Id(String name, String auth0IdOrgId);

    Optional<Users> findByAuth0IdAndOrganization_Auth0Id(String auth0Id, String auth0OrgId);

    Optional<Users> findByEmailAndOrganization_Auth0Id(String email, String auth0OrgId);
}
