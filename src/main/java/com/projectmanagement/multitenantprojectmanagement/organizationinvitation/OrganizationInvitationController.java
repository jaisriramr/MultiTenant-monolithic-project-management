package com.projectmanagement.multitenantprojectmanagement.organizationinvitation;

import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.service.annotation.PutExchange;

import com.projectmanagement.multitenantprojectmanagement.organizationinvitation.dto.request.InviteUserToAnOrganization;
import com.projectmanagement.multitenantprojectmanagement.organizationinvitation.dto.request.RevokeInvitationToAnUser;
import com.projectmanagement.multitenantprojectmanagement.organizationinvitation.dto.request.UpdateInvitationStatus;
import com.projectmanagement.multitenantprojectmanagement.organizationinvitation.enums.StatusForInvitation;
import com.projectmanagement.multitenantprojectmanagement.users.dto.response.PaginatedResponseDto;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class OrganizationInvitationController {

    private final OrganizationInvitationService organizationInvitationService;

    @GetMapping("/v1/organization-invitation/all")
    public ResponseEntity<PaginatedResponseDto<OrganizationInvitation>> getAllInvitations(Pageable pageable, @AuthenticationPrincipal Jwt jwt) {
        PaginatedResponseDto<OrganizationInvitation> invitations = organizationInvitationService.getAllInvitations(pageable);
        return ResponseEntity.ok(invitations);
    }

    @GetMapping("/v1/organization-invitation/{id}")
    public ResponseEntity<OrganizationInvitation> getInvitationsById(@PathVariable UUID id, @AuthenticationPrincipal Jwt jwt) {
        OrganizationInvitation invitation = organizationInvitationService.getInvitationById(id);
        return ResponseEntity.ok(invitation);
    }

    @GetMapping("/v1/organization-invitation/by/organization/{orgId}")
    public ResponseEntity<PaginatedResponseDto<OrganizationInvitation>> getAllInvitationsByOrganizationAuthoId(@PathVariable String orgId, Pageable pageable, @AuthenticationPrincipal Jwt jwt) {
        PaginatedResponseDto<OrganizationInvitation> invitations = organizationInvitationService.getAllInvitationToAnOrganization(orgId, pageable);
        return ResponseEntity.ok(invitations);
    }

    @GetMapping("/v1/organization-invitation/by/status")
    public ResponseEntity<PaginatedResponseDto<OrganizationInvitation>> getAllByStatus(@RequestParam StatusForInvitation status, Pageable pageable, @AuthenticationPrincipal Jwt jwt) {
        PaginatedResponseDto<OrganizationInvitation> invitations = organizationInvitationService.getAllByStatus(status, pageable);

        return ResponseEntity.ok(invitations);
    }

    @GetMapping("/v1/organization-invitation")
    public ResponseEntity<PaginatedResponseDto<OrganizationInvitation>> getAllByEmail(@RequestParam String email, Pageable pageable, @AuthenticationPrincipal Jwt jwt) {
        PaginatedResponseDto<OrganizationInvitation> invitations = organizationInvitationService.getAllInvitationsByEmail(email, pageable);

        return ResponseEntity.ok(invitations);
    }

    @PreAuthorize("@organizationMembersService.hasPermission(#jwt.subject, {\"invite:member\"}, #jwt.claims[\"org_id\"])")
    @PostMapping("/v1/organization-invitation")
    public ResponseEntity<OrganizationInvitation> inviteUserToAnOrganization(@Valid @RequestBody InviteUserToAnOrganization inviteUserToAnOrganization, @AuthenticationPrincipal Jwt jwt) {
        OrganizationInvitation response = organizationInvitationService.inviteUserToAnOrganization(inviteUserToAnOrganization);
        return ResponseEntity.ok(response);
    }

    @PutExchange("/v1/organization-invitation")
    public ResponseEntity<String> updateInvitationStatus(@Valid @RequestBody UpdateInvitationStatus updateInvitationStatus, @AuthenticationPrincipal Jwt jwt) {
        String response = organizationInvitationService.updateInvitationStatus(updateInvitationStatus.getInvitationId(), updateInvitationStatus.getStatus());

        return ResponseEntity.ok(response);
    }

    @PreAuthorize("@organizationMembersService.hasPermission(#jwt.subject, {\"revoke:invitation\"}, #jwt.claims[\"org_id\"])")
    @DeleteMapping("/v1/organization-invitation")
    public ResponseEntity<String> revokeInvitationToAnUser(@Valid @RequestBody RevokeInvitationToAnUser revokeInvitationToAnUser, @AuthenticationPrincipal Jwt jwt) {
        String response = organizationInvitationService.revokeInvitationToAnUser(revokeInvitationToAnUser.getOrgId(), revokeInvitationToAnUser.getInvitationId());
        return ResponseEntity.ok(response);
    }

}
