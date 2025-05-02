package com.projectmanagement.multitenantprojectmanagement.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.projectmanagement.multitenantprojectmanagement.auth0.utils.JWTUtils;
import com.projectmanagement.multitenantprojectmanagement.organizationinvitation.OrganizationInvitation;
import com.projectmanagement.multitenantprojectmanagement.organizationinvitation.OrganizationInvitationController;
import com.projectmanagement.multitenantprojectmanagement.organizationinvitation.OrganizationInvitationService;
import com.projectmanagement.multitenantprojectmanagement.organizationinvitation.dto.request.InviteUserToAnOrganization;
import com.projectmanagement.multitenantprojectmanagement.organizationinvitation.dto.request.RevokeInvitationToAnUser;
import com.projectmanagement.multitenantprojectmanagement.organizationinvitation.dto.request.UpdateInvitationStatus;
import com.projectmanagement.multitenantprojectmanagement.organizationinvitation.enums.StatusForInvitation;
import com.projectmanagement.multitenantprojectmanagement.organizations.Organizations;
import com.projectmanagement.multitenantprojectmanagement.users.dto.response.PaginatedResponseDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.Collections;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OrganizationInvitationController.class)
@AutoConfigureMockMvc(addFilters = false)
public class OrganizationInvitationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OrganizationInvitationService organizationInvitationService;

    @MockBean
    private JWTUtils jwtUtils;

    @Autowired
    private ObjectMapper objectMapper;

    private UUID invitationId;
    private UUID orgId;
    private OrganizationInvitation organizationInvitation;
    private PaginatedResponseDto<OrganizationInvitation> paginatedResponseDto;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        invitationId = UUID.randomUUID();
        orgId = UUID.randomUUID();

        organizationInvitation = new OrganizationInvitation();
        organizationInvitation.setId(invitationId);
        
        Organizations organization = new Organizations();
        organization.setId(orgId);

        organizationInvitation.setAuth0Id("123");
        organizationInvitation.setEmail("test@gmail.com");
        organizationInvitation.setOrganizationAuth0Id("orgId");

        paginatedResponseDto = PaginatedResponseDto.<OrganizationInvitation>builder()
                                .data(Collections.singletonList(organizationInvitation)) 
                                .totalElements(0)
                                .size(20)
                                .page(0)
                                .totalPages(0)
                                .build();

    }

    @Test
    void testGetInvitationById_Success() throws Exception {
        OrganizationInvitation invitation = new OrganizationInvitation();
        invitation.setId(invitationId);

        when(organizationInvitationService.getInvitationById(invitationId)).thenReturn(invitation);

        mockMvc.perform(get("/api/v1/organization-invitation/{id}", invitationId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(invitationId.toString()));

        verify(organizationInvitationService, times(1)).getInvitationById(invitationId);
    }

    @Test
    void testGetAllInvitationsByOrganizationAuthoId_Success() throws Exception {

        when(organizationInvitationService.getAllInvitationToAnOrganization(eq(orgId.toString()), any(Pageable.class))).thenReturn(paginatedResponseDto);

        mockMvc.perform(get("/api/v1/organization-invitation/by/organization/{orgId}", orgId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray());

        verify(organizationInvitationService, times(1)).getAllInvitationToAnOrganization(eq(orgId.toString()), any(Pageable.class));
    }

    @Test
    void testGetAllByStatus_Success() throws Exception {

        when(organizationInvitationService.getAllByStatus(eq(StatusForInvitation.PENDING), any(Pageable.class))).thenReturn(paginatedResponseDto);

        mockMvc.perform(get("/api/v1/organization-invitation/by/status")
                .param("status", "PENDING")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray());

        verify(organizationInvitationService, times(1)).getAllByStatus(eq(StatusForInvitation.PENDING), any(Pageable.class));
    }

    @Test
    void testGetAllByEmail_Success() throws Exception {

        when(organizationInvitationService.getAllInvitationsByEmail(eq("test@example.com"), any(Pageable.class))).thenReturn(paginatedResponseDto);

        mockMvc.perform(get("/api/v1/organization-invitation")
                .param("email", "test@example.com")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray());

        verify(organizationInvitationService, times(1)).getAllInvitationsByEmail(eq("test@example.com"), any(Pageable.class));
    }

    @Test
    void testInviteUserToAnOrganization_Success() throws Exception {
        InviteUserToAnOrganization request = new InviteUserToAnOrganization();
        OrganizationInvitation response = new OrganizationInvitation();
        response.setId(invitationId);

        when(organizationInvitationService.inviteUserToAnOrganization(any(InviteUserToAnOrganization.class))).thenReturn(response);

        mockMvc.perform(post("/api/v1/organization-invitation")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(invitationId.toString()));

        verify(organizationInvitationService, times(1)).inviteUserToAnOrganization(any(InviteUserToAnOrganization.class));
    }

    @Test
    void testUpdateInvitationStatus_Success() throws Exception {
        UpdateInvitationStatus request = new UpdateInvitationStatus();
        request.setInvitationId("invitationId");
        request.setStatus(StatusForInvitation.ACCEPTED.toString());

        when(organizationInvitationService.updateInvitationStatus(anyString(), anyString())).thenReturn("Status updated successfully");

        mockMvc.perform(put("/api/v1/organization-invitation")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string("Status updated successfully"));

        verify(organizationInvitationService, times(1)).updateInvitationStatus(anyString(), anyString());
    }

    @Test
    void testRevokeInvitationToAnUser_Success() throws Exception {
        RevokeInvitationToAnUser request = new RevokeInvitationToAnUser();
        request.setOrgId(orgId.toString());
        request.setInvitationId(invitationId.toString());

        when(organizationInvitationService.revokeInvitationToAnUser(eq(orgId.toString()), eq(invitationId.toString()))).thenReturn("Invitation revoked successfully");

        mockMvc.perform(delete("/api/v1/organization-invitation")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string("Invitation revoked successfully"));

        verify(organizationInvitationService, times(1)).revokeInvitationToAnUser(eq(orgId.toString()), eq(invitationId.toString()));
    }
}
