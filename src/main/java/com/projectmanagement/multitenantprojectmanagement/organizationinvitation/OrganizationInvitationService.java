package com.projectmanagement.multitenantprojectmanagement.organizationinvitation;

import java.util.Map;
import java.util.UUID;

import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.projectmanagement.multitenantprojectmanagement.auth0.Auth0Service;
import com.projectmanagement.multitenantprojectmanagement.organizationinvitation.dto.request.InviteUserToAnOrganization;
import com.projectmanagement.multitenantprojectmanagement.organizationinvitation.enums.StatusForInvitation;
import com.projectmanagement.multitenantprojectmanagement.organizationinvitation.mapper.OrganizationInvitationMapper;
import com.projectmanagement.multitenantprojectmanagement.roles.RolesService;
import com.projectmanagement.multitenantprojectmanagement.roles.dto.response.RoleResponse;
import com.projectmanagement.multitenantprojectmanagement.users.dto.response.PaginatedResponseDto;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrganizationInvitationService {

    private final OrganiationInvitationRepository organiationInvitationRepository;
    private final Auth0Service auth0Service;
    private final RolesService rolesService;

    public PaginatedResponseDto<OrganizationInvitation> getAllInvitations(Pageable pageable) {
        try {
            Page<OrganizationInvitation> invitations = organiationInvitationRepository.findAll(pageable);

            return OrganizationInvitationMapper.toPaginatedResponseDto(invitations);
        }catch(Exception e) {
            throw new RuntimeException("Error while trying to get all invitations",e);
        }
    }

    public OrganizationInvitation getInvitationById(UUID id) {
        try {
            OrganizationInvitation invitation = organiationInvitationRepository.findById(id).orElseThrow(() -> new NotFoundException());
            return invitation;
        }catch(Exception e) {
            throw new RuntimeException("Error while trying to fetch invitation with Id " + id,e);
        }
    }

    public PaginatedResponseDto<OrganizationInvitation> getAllInvitationToAnOrganization(String auth0OrgId, Pageable pageable) {
        try {
            Page<OrganizationInvitation> invitations = organiationInvitationRepository.findAllByOrganizationAuth0Id(auth0OrgId, pageable);

            return OrganizationInvitationMapper.toPaginatedResponseDto(invitations);
        }catch(Exception e) {
            throw new RuntimeException("Error while trying to get all invitation with orgId " + auth0OrgId, e);
        }
    }

    public PaginatedResponseDto<OrganizationInvitation> getAllInvitationsByEmail(String email, Pageable pageable) {
        try {
            Page<OrganizationInvitation> invitations = organiationInvitationRepository.findAllByEmail(email, pageable);

            return OrganizationInvitationMapper.toPaginatedResponseDto(invitations);
        }catch(Exception e) {
            throw new RuntimeException("Error while trying to all invitation by email " + email, e);
        }
    }

    public PaginatedResponseDto<OrganizationInvitation> getAllByStatus(StatusForInvitation status, Pageable pageable) {
        try {
            Page<OrganizationInvitation> invitations = organiationInvitationRepository.findAllByStatus(status, pageable);

            return OrganizationInvitationMapper.toPaginatedResponseDto(invitations);
        }catch(Exception e) {
            throw new RuntimeException("Error while trying to all invitation by status " + status, e);
        }
    }

    @Transactional
    public String revokeInvitationToAnUser(String orgId, String invitationId) {
        try {
            auth0Service.revokeInvitationSentToAnUser(orgId, invitationId);

            OrganizationInvitation organizationInvitation = organiationInvitationRepository.findByAuth0Id(invitationId).orElseThrow(() -> new NotFoundException());

            organizationInvitation.setStatus(StatusForInvitation.REVOKED);

            organiationInvitationRepository.save(organizationInvitation);

            return "Invitation has been revoked successfully";
        }catch(Exception e) {
            throw new RuntimeException("Error while trying to revoke invitation to an user with invitation id " + invitationId, e);
        }
    }

    @Transactional
    public String updateInvitationStatus(String invitationId, String statusForInvitation) {
        try {

            OrganizationInvitation organizationInvitation = organiationInvitationRepository.findByAuth0Id(invitationId).orElseThrow(() -> new NotFoundException());
            if(statusForInvitation == "EXPIRED") {
                organizationInvitation.setStatus(StatusForInvitation.EXPIRED);
            }else if(statusForInvitation == "ACCEPTED") {
                organizationInvitation.setStatus(StatusForInvitation.ACCEPTED);
            }

            organiationInvitationRepository.save(organizationInvitation);

            return "Invitation status is updated successfully";
        }catch(Exception e) {
            throw new RuntimeException("Error while trying to update invitation status",e);
        }
    }

    @Transactional
    public OrganizationInvitation inviteUserToAnOrganization(InviteUserToAnOrganization inviteUserToAnOrganization) {
        try {
            
            ResponseEntity<Map<String, Object>> auth0Reponse =  auth0Service.inviteUserToOrganization(inviteUserToAnOrganization.getAuth0OrgId(), inviteUserToAnOrganization.getInviterName(), inviteUserToAnOrganization.getEmai(), inviteUserToAnOrganization.getRoleId());

            RoleResponse role = rolesService.getRoleByAuth0Id(inviteUserToAnOrganization.getRoleId());

            OrganizationInvitation organizationInvitationEntity = OrganizationInvitationMapper.toOrganizationInvitationEntity(inviteUserToAnOrganization, role.getAuth0Id(), auth0Reponse.getBody());

            OrganizationInvitation organizationInvitation = organiationInvitationRepository.save(organizationInvitationEntity);

            return organizationInvitation;
        }catch(Exception e) {
            throw new RuntimeException("Error while trying to invite an user to an organization", e);
        }
    }


}
