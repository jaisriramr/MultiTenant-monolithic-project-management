package com.projectmanagement.multitenantprojectmanagement.service;

import com.projectmanagement.multitenantprojectmanagement.auth0.Auth0Service;
import com.projectmanagement.multitenantprojectmanagement.auth0.utils.JWTUtils;
import com.projectmanagement.multitenantprojectmanagement.exception.NotFoundException;
import com.projectmanagement.multitenantprojectmanagement.helper.MaskingString;
import com.projectmanagement.multitenantprojectmanagement.organizationinvitation.*;
import com.projectmanagement.multitenantprojectmanagement.organizationinvitation.dto.request.InviteUserToAnOrganization;
import com.projectmanagement.multitenantprojectmanagement.organizationinvitation.enums.StatusForInvitation;
import com.projectmanagement.multitenantprojectmanagement.roles.RolesService;
import com.projectmanagement.multitenantprojectmanagement.roles.dto.response.RoleResponse;
import com.projectmanagement.multitenantprojectmanagement.users.dto.response.PaginatedResponseDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class OrganizationInvitationTest {

    @Mock
    private OrganiationInvitationRepository organiationInvitationRepository;

    @Mock
    private Auth0Service auth0Service;

    @Mock
    private RolesService rolesService;

    @Mock
    private MaskingString maskingString;

    @Mock
    private JWTUtils jwtUtils;

    @InjectMocks
    private OrganizationInvitationService organizationInvitationService;

    private UUID invitationId;
    private OrganizationInvitation mockInvitation;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        invitationId = UUID.randomUUID();
        mockInvitation = new OrganizationInvitation();
        mockInvitation.setId(invitationId);
        mockInvitation.setStatus(StatusForInvitation.PENDING);
    }

    @Test
    void testGetAllInvitations_Success() {
        Pageable pageable = Pageable.unpaged();
        Page<OrganizationInvitation> invitations = new PageImpl<>(Collections.singletonList(mockInvitation));

        when(organiationInvitationRepository.findAll(pageable)).thenReturn(invitations);

        PaginatedResponseDto<OrganizationInvitation> result = organizationInvitationService.getAllInvitations(pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(organiationInvitationRepository, times(1)).findAll(pageable);
    }

    @Test
    void testGetInvitationById_Success() {
        when(organiationInvitationRepository.findById(invitationId)).thenReturn(Optional.of(mockInvitation));
        when(maskingString.maskSensitive(anyString())).thenReturn("masked");

        OrganizationInvitation result = organizationInvitationService.getInvitationById(invitationId);

        assertNotNull(result);
        assertEquals(invitationId, result.getId());
        verify(organiationInvitationRepository, times(1)).findById(invitationId);
    }

    @Test
    void testGetInvitationById_NotFound() {
        when(organiationInvitationRepository.findById(invitationId)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            organizationInvitationService.getInvitationById(invitationId);
        });

        assertEquals("Invitation not found for the given ID: " + invitationId, exception.getMessage());
        verify(organiationInvitationRepository, times(1)).findById(invitationId);
    }

    @Test
    void testRevokeInvitationToAnUser_Success() {
        String auth0OrgId = "auth0|12345";
        String orgId = "org123";

        when(jwtUtils.getAuth0OrgId()).thenReturn(auth0OrgId);
        when(organiationInvitationRepository.findByAuth0IdAndOrganizationAuth0Id(anyString(), eq(auth0OrgId)))
                .thenReturn(Optional.of(mockInvitation));

        String result = organizationInvitationService.revokeInvitationToAnUser(orgId, invitationId.toString());

        assertEquals("Invitation has been revoked successfully", result);
        assertEquals(StatusForInvitation.REVOKED, mockInvitation.getStatus());
        verify(auth0Service, times(1)).revokeInvitationSentToAnUser(orgId, invitationId.toString());
        verify(organiationInvitationRepository, times(1)).save(mockInvitation);
    }

    @Test
    void testRevokeInvitationToAnUser_NotFound() {
        String auth0OrgId = "auth0|12345";

        when(jwtUtils.getAuth0OrgId()).thenReturn(auth0OrgId);
        when(organiationInvitationRepository.findByAuth0IdAndOrganizationAuth0Id(anyString(), eq(auth0OrgId)))
                .thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            organizationInvitationService.revokeInvitationToAnUser("org123", invitationId.toString());
        });

        assertEquals("Invitation not found for the given ID: " + invitationId, exception.getMessage());
        verify(auth0Service, never()).revokeInvitationSentToAnUser(anyString(), anyString());
    }

    @Test
    void testUpdateInvitationStatus_Success() {
        String auth0OrgId = "auth0|12345";

        when(jwtUtils.getAuth0OrgId()).thenReturn(auth0OrgId);
        when(organiationInvitationRepository.findByAuth0IdAndOrganizationAuth0Id(anyString(), eq(auth0OrgId)))
                .thenReturn(Optional.of(mockInvitation));

        String result = organizationInvitationService.updateInvitationStatus(invitationId.toString(), "ACCEPTED");

        assertEquals("Invitation status is updated successfully", result);
        assertEquals(StatusForInvitation.ACCEPTED, mockInvitation.getStatus());
        verify(organiationInvitationRepository, times(1)).save(mockInvitation);
    }

    @Test
    void testInviteUserToAnOrganization_Success() {
        InviteUserToAnOrganization request = new InviteUserToAnOrganization();
        request.setAuth0OrgId("auth0|12345");
        request.setEmai("test@example.com");
        request.setRoleId("role123");
        request.setInviterName("Admin");

        RoleResponse roleResponse = new RoleResponse();
        roleResponse.setId(invitationId);
        roleResponse.setAuth0Id("role123");

        ResponseEntity<Map<String, Object>> auth0Response = ResponseEntity.ok(Map.of("id", "invitation123", "invitation_url", "http://example.com/invite", "expires_at", "2023-01-01T00:00:00Z"));

        when(jwtUtils.getAuth0OrgId()).thenReturn("auth0|12345");
        when(organiationInvitationRepository.findByAuth0IdAndOrganizationAuth0Id(anyString(), anyString())).thenReturn(Optional.empty());

        when(auth0Service.inviteUserToOrganization(anyString(), anyString(), anyString(), anyString())).thenReturn(auth0Response);
        when(rolesService.getRoleByAuth0Id(anyString())).thenReturn(roleResponse);
        when(organiationInvitationRepository.save(any(OrganizationInvitation.class))).thenReturn(mockInvitation);

        OrganizationInvitation result = organizationInvitationService.inviteUserToAnOrganization(request);

        assertNotNull(result);
        assertEquals(invitationId, result.getId());
        verify(auth0Service, times(1)).inviteUserToOrganization(anyString(), anyString(), anyString(), anyString());
        verify(organiationInvitationRepository, times(1)).save(any(OrganizationInvitation.class));
    }
}
