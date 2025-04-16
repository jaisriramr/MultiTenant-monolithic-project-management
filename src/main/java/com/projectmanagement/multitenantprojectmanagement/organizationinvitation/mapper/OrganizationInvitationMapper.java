package com.projectmanagement.multitenantprojectmanagement.organizationinvitation.mapper;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.data.domain.Page;

import com.projectmanagement.multitenantprojectmanagement.organizationinvitation.OrganizationInvitation;
import com.projectmanagement.multitenantprojectmanagement.organizationinvitation.dto.request.InviteUserToAnOrganization;
import com.projectmanagement.multitenantprojectmanagement.users.dto.response.PaginatedResponseDto;

public class OrganizationInvitationMapper {

    public static OrganizationInvitation toOrganizationInvitationEntity(InviteUserToAnOrganization inviteUserToAnOrganization, String roleName, Map<String, Object> auth0Response) {
        OrganizationInvitation organizationInvitation = new OrganizationInvitation();
        organizationInvitation.setRoleName(roleName);
        organizationInvitation.setAuth0Id(auth0Response.get("id").toString());
        organizationInvitation.setInvitationUrl(auth0Response.get("invitation_url").toString());
        organizationInvitation.setEmail(inviteUserToAnOrganization.getEmai());
        organizationInvitation.setInvitedBy(inviteUserToAnOrganization.getInviterName());
        organizationInvitation.setExpiresAt(Instant.parse(auth0Response.get("expires_at").toString()));
        organizationInvitation.setRoleId(inviteUserToAnOrganization.getRoleId());
        organizationInvitation.setOrganizationAuth0Id(inviteUserToAnOrganization.getAuth0OrgId());

        return organizationInvitation;
    }

    public static PaginatedResponseDto<OrganizationInvitation> toPaginatedResponseDto(Page<OrganizationInvitation> invitations) {

        return PaginatedResponseDto.<OrganizationInvitation>builder()
                                    .data(invitations.getContent())
                                    .page(invitations.getNumber())
                                    .totalElements(invitations.getTotalElements())
                                    .totalPages(invitations.getTotalPages())
                                    .size(invitations.getSize())
                                    .build();

    }

}
