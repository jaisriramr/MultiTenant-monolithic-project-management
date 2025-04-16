package com.projectmanagement.multitenantprojectmanagement.organizationinvitation;

import java.util.Map;
import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
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

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class OrganizationInvitationController {

    private final OrganizationInvitationService organizationInvitationService;

    @GetMapping("/v1/organization-invitation/all")
    public ResponseEntity<PaginatedResponseDto<OrganizationInvitation>> getAllInvitations(Pageable pageable) {
        PaginatedResponseDto<OrganizationInvitation> invitations = organizationInvitationService.getAllInvitations(pageable);
        return ResponseEntity.ok(invitations);
    }

    @GetMapping("/v1/organization-invitation/{id}")
    public ResponseEntity<OrganizationInvitation> getInvitationsById(@PathVariable UUID id) {
        OrganizationInvitation invitation = organizationInvitationService.getInvitationById(id);
        return ResponseEntity.ok(invitation);
    }

    @GetMapping("/v1/organization-invitation/by/organization/{orgId}")
    public ResponseEntity<PaginatedResponseDto<OrganizationInvitation>> getAllInvitationsByOrganizationAuthoId(@PathVariable String orgId, Pageable pageable) {
        PaginatedResponseDto<OrganizationInvitation>  invitations = organizationInvitationService.getAllInvitationToAnOrganization(orgId, pageable);
        return ResponseEntity.ok(invitations);
    }

    @GetMapping("/v1/organization-invitation/by/status")
    public ResponseEntity<PaginatedResponseDto<OrganizationInvitation>> getAllByStatus(@RequestParam StatusForInvitation status, Pageable pageable) {
        PaginatedResponseDto<OrganizationInvitation> invitations = organizationInvitationService.getAllByStatus(status, pageable);

        return ResponseEntity.ok(invitations);
    }

    @GetMapping("/v1/organization-invitation")
    public ResponseEntity<PaginatedResponseDto<OrganizationInvitation>> getAllByEmail(@RequestParam String email, Pageable pageable) {
        PaginatedResponseDto<OrganizationInvitation> invitations = organizationInvitationService.getAllInvitationsByEmail(email, pageable);

        return ResponseEntity.ok(invitations);
    }

    @PostMapping("/v1/organization-invitation")
    public ResponseEntity<OrganizationInvitation> inviteUserToAnOrganization(@RequestBody InviteUserToAnOrganization inviteUserToAnOrganization) {
        OrganizationInvitation response = organizationInvitationService.inviteUserToAnOrganization(inviteUserToAnOrganization);
        return ResponseEntity.ok(response);
    }

    @PutExchange("/v1/organization-invitation")
    public ResponseEntity<String> updateInvitationStatus(@RequestBody UpdateInvitationStatus updateInvitationStatus) {
        String response = organizationInvitationService.updateInvitationStatus(updateInvitationStatus.getInvitationId(), updateInvitationStatus.getStatus());

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/v1/organization-invitation")
    public ResponseEntity<String> revokeInvitationToAnUser(@RequestBody RevokeInvitationToAnUser revokeInvitationToAnUser){
        String response = organizationInvitationService.revokeInvitationToAnUser(revokeInvitationToAnUser.getOrgId(), revokeInvitationToAnUser.getInvitationId());
        return ResponseEntity.ok(response);
    }

}
