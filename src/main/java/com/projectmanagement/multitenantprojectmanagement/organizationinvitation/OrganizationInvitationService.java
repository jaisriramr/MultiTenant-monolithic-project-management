package com.projectmanagement.multitenantprojectmanagement.organizationinvitation;

import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;

import com.projectmanagement.multitenantprojectmanagement.auth0.Auth0Service;
import com.projectmanagement.multitenantprojectmanagement.exception.NotFoundException;
import com.projectmanagement.multitenantprojectmanagement.helper.MaskingString;
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
    private static final Logger logger = LoggerFactory.getLogger(OrganizationInvitationService.class);
    private final MaskingString maskingString;

    public PaginatedResponseDto<OrganizationInvitation> getAllInvitations(Pageable pageable) {
        logger.info("Getting all invitations", pageable);
        Page<OrganizationInvitation> invitations = organiationInvitationRepository.findAll(pageable);

        logger.debug("Fetched {} invitations", invitations.getTotalElements());

        return OrganizationInvitationMapper.toPaginatedResponseDto(invitations);
    }

    public OrganizationInvitation getInvitationById(UUID id) {
        logger.info("Getting invitation for the given ID: {} ", maskingString.maskSensitive(id.toString()));

        OrganizationInvitation invitation = organiationInvitationRepository.findById(id).orElseThrow(() -> new NotFoundException("Invitation not found for the given ID: " + id));

        logger.debug("Fetched invitation ID: {} ", maskingString.maskSensitive(invitation.getId().toString()));

        return invitation;
    }

    public PaginatedResponseDto<OrganizationInvitation> getAllInvitationToAnOrganization(String auth0OrgId, Pageable pageable) {
        logger.info("Getting all invitations for the given Auth0 organization ID: {} ", maskingString.maskSensitive(auth0OrgId));

        Page<OrganizationInvitation> invitations = organiationInvitationRepository.findAllByOrganizationAuth0Id(auth0OrgId, pageable);

        logger.debug("Fetched {} invitations for the organization ID: {} ", invitations.getTotalElements(), maskingString.maskSensitive(auth0OrgId));

        return OrganizationInvitationMapper.toPaginatedResponseDto(invitations);
    }

    public PaginatedResponseDto<OrganizationInvitation> getAllInvitationsByEmail(String email, Pageable pageable) {
        logger.info("Getting all invitations for the given email: {} ", maskingString.maskSensitive(email));

        Page<OrganizationInvitation> invitations = organiationInvitationRepository.findAllByEmail(email, pageable);

        logger.debug("Fetched {} invitations for the email: {} ", invitations.getTotalElements(), maskingString.maskSensitive(email));

        return OrganizationInvitationMapper.toPaginatedResponseDto(invitations);
    }

    public PaginatedResponseDto<OrganizationInvitation> getAllByStatus(StatusForInvitation status, Pageable pageable) {

        logger.info("Getting all invitations for the given status: {} ", status);

        Page<OrganizationInvitation> invitations = organiationInvitationRepository.findAllByStatus(status, pageable);

        logger.debug("Fetched {} invitations for the status: {} ", invitations.getTotalElements(), status);

        return OrganizationInvitationMapper.toPaginatedResponseDto(invitations);
    }

    @Transactional
    public String revokeInvitationToAnUser(String orgId, String invitationId) {
        logger.info("Revoking invitation for the given ID: {} ", maskingString.maskSensitive(invitationId));
        try {
            OrganizationInvitation organizationInvitation = organiationInvitationRepository.findByAuth0Id(invitationId).orElseThrow(() -> new NotFoundException("Invitation not found for the given ID: " + invitationId));

            logger.debug("Fetched invitation ID: {} ", maskingString.maskSensitive(organizationInvitation.getId().toString()));

            auth0Service.revokeInvitationSentToAnUser(orgId, invitationId);

            organizationInvitation.setStatus(StatusForInvitation.REVOKED);

            organiationInvitationRepository.save(organizationInvitation);

            return "Invitation has been revoked successfully";
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            logger.error("Error calling Auth0 API for revoking invitation: {}", e.getMessage(), e);
            throw new RuntimeException("Error communicating with Auth0 while revoking invitation", e);
        } catch (NotFoundException e) {
            throw new NotFoundException("Invitation not found for the given ID: " + invitationId);
        } catch (Exception e) {
            throw new RuntimeException("Error while trying to revoke invitation to an user with invitation id " + invitationId, e);
        }
    }

    @Transactional
    public String updateInvitationStatus(String invitationId, String statusForInvitation) {
        logger.info("Updating invitation status for the given ID: {} ", maskingString.maskSensitive(invitationId));
        try {

            OrganizationInvitation organizationInvitation = organiationInvitationRepository.findByAuth0Id(invitationId).orElseThrow(() -> new NotFoundException("Invitation not found for the given ID: " + invitationId));

            logger.debug("Fetched invitation ID: {} ", maskingString.maskSensitive(organizationInvitation.getId().toString()));

            if (statusForInvitation == "EXPIRED") {
                organizationInvitation.setStatus(StatusForInvitation.EXPIRED);
            } else if (statusForInvitation == "ACCEPTED") {
                organizationInvitation.setStatus(StatusForInvitation.ACCEPTED);
            }

            organiationInvitationRepository.save(organizationInvitation);

            return "Invitation status is updated successfully";
        } catch (NotFoundException e) {
            throw new NotFoundException("Invitation not found for the given ID: " + invitationId);
        } catch (Exception e) {
            throw new RuntimeException("Error while trying to update invitation status", e);
        }
    }

    @Transactional
    public OrganizationInvitation inviteUserToAnOrganization(InviteUserToAnOrganization inviteUserToAnOrganization) {
        logger.info("Inviting user to an organization with ID: {} and email: {} and role ID: {}", maskingString.maskSensitive(inviteUserToAnOrganization.getAuth0OrgId()), maskingString.maskSensitive(inviteUserToAnOrganization.getEmai()), maskingString.maskSensitive(inviteUserToAnOrganization.getRoleId()));
        try {
            ResponseEntity<Map<String, Object>> auth0Reponse = auth0Service.inviteUserToOrganization(inviteUserToAnOrganization.getAuth0OrgId(), inviteUserToAnOrganization.getInviterName(), inviteUserToAnOrganization.getEmai(), inviteUserToAnOrganization.getRoleId());

            logger.debug("Auth0 response for inviting user: {}", auth0Reponse.getBody());

            RoleResponse role = rolesService.getRoleByAuth0Id(inviteUserToAnOrganization.getRoleId());

            logger.debug("Fetched role for the given ID: {} ", maskingString.maskSensitive(role.getId().toString()));

            OrganizationInvitation organizationInvitationEntity = OrganizationInvitationMapper.toOrganizationInvitationEntity(inviteUserToAnOrganization, role.getAuth0Id(), auth0Reponse.getBody());

            OrganizationInvitation organizationInvitation = organiationInvitationRepository.save(organizationInvitationEntity);

            logger.debug("Saved organization invitation with ID: {} ", maskingString.maskSensitive(organizationInvitation.getId().toString()));

            return organizationInvitation;
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            logger.error("Error calling Auth0 API for inviting user: {}", e.getMessage(), e);
            throw new RuntimeException("Error communicating with Auth0 while inviting user", e);
        } catch (NotFoundException e) {
            throw new NotFoundException("Role not found for the given ID: " + inviteUserToAnOrganization.getRoleId());
        } catch (Exception e) {
            throw new RuntimeException("Error while trying to invite an user to an organization", e);
        }
    }

}
