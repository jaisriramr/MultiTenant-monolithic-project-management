package com.projectmanagement.multitenantprojectmanagement.organizationmembers;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.projectmanagement.multitenantprojectmanagement.organizations.Organizations;

@Repository
public interface OrganizationMembersRepository extends JpaRepository<OrganizationMembers, UUID> {

    @Query("SELECT om.organization FROM OrganizationMembers om WHERE om.user.id = :userId")
    List<Organizations> findOrganizationsByUserId(UUID userId);

}
