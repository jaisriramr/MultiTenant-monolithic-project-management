package com.projectmanagement.multitenantprojectmanagement.organizationmembers;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import com.projectmanagement.multitenantprojectmanagement.organizations.Organizations;

@Repository
public interface OrganizationMembersRepository extends JpaRepository<OrganizationMembers, UUID> {

    @Query("SELECT om.organization FROM OrganizationMembers om WHERE om.user.id = :userId")
    List<Organizations> findOrganizationsByUserId(UUID userId);

    public Page<OrganizationMembers> findAllByOrganization_Auth0Id(String auth0OrgId ,Pageable pageable);

    Optional<OrganizationMembers> findByIdAndOrganization_Auth0Id(UUID id, String auth0OrgId);

    Page<OrganizationMembers> findAllByUser_Auth0IdAndOrganization_Auth0Id(String auth0Id, String auth0OrgId,Pageable pageable);

    Page<OrganizationMembers> findAllByOrganizationIdAndOrganization_Auth0Id(UUID organizationId, String auth0OrgId,Pageable pageable);

    Optional<OrganizationMembers> findByUser_Auth0IdAndOrganization_Auth0Id(String auth0UserId, String auth0OrganizationId);

    Page<OrganizationMembers> findAllByOrganization_Auth0IdAndRole_Auth0Id(String auth0OrganizationId, String auth0RoleId, Pageable pageble);

}
