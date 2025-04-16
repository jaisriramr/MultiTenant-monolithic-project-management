package com.projectmanagement.multitenantprojectmanagement.organizationmembers;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.projectmanagement.multitenantprojectmanagement.organizations.Organizations;

import io.micrometer.common.lang.NonNull;

@Repository
public interface OrganizationMembersRepository extends JpaRepository<OrganizationMembers, UUID> {

    @Query("SELECT om.organization FROM OrganizationMembers om WHERE om.user.id = :userId")
    List<Organizations> findOrganizationsByUserId(UUID userId);

    @NonNull
    public Page<OrganizationMembers> findAll(Pageable pageable);

    Page<OrganizationMembers> findAllByUser_Auth0Id(String auth0Id, Pageable pageable);

    Page<OrganizationMembers> findAllByOrganizationId(UUID organizationId, Pageable pageable);

    Optional<OrganizationMembers> findByUser_Auth0IdAndOrganization_Auth0Id(String auth0UserId, String auth0OrganizationId);

    Page<OrganizationMembers> findAllByOrganization_Auth0IdAndRole_Auth0Id(String auth0OrganizationId, String auth0RoleId, Pageable pageble);

}
