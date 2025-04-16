package com.projectmanagement.multitenantprojectmanagement.organizationinvitation;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.projectmanagement.multitenantprojectmanagement.organizationinvitation.enums.StatusForInvitation;

@Repository
public interface OrganiationInvitationRepository extends JpaRepository<OrganizationInvitation, UUID> {

    Page<OrganizationInvitation> findAllByOrganizationAuth0Id(String orgId, Pageable pageable);

    Page<OrganizationInvitation> findAllByEmail(String email, Pageable pageable);

    Page<OrganizationInvitation> findAllByStatus(StatusForInvitation status, Pageable pageable);

    Optional<OrganizationInvitation> findByAuth0Id(String invitationId);
}
