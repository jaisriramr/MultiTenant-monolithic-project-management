package com.projectmanagement.multitenantprojectmanagement.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.projectmanagement.multitenantprojectmanagement.organizations.Organizations;
import com.projectmanagement.multitenantprojectmanagement.organizations.OrganizationsController;
import com.projectmanagement.multitenantprojectmanagement.organizations.OrganizationsService;
import com.projectmanagement.multitenantprojectmanagement.organizations.dto.request.CreateOrganizationRequest;
import com.projectmanagement.multitenantprojectmanagement.organizations.dto.request.UpdateOrganizationRequest;
import com.projectmanagement.multitenantprojectmanagement.organizations.dto.response.OrganizationResponse;
import com.projectmanagement.multitenantprojectmanagement.organizations.dto.response.OrganizationsResponse;
import com.projectmanagement.multitenantprojectmanagement.organizations.mapper.OrganizationMapper;

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

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OrganizationsController.class)
@AutoConfigureMockMvc(addFilters = false)
public class OrganizationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OrganizationsService organizationsService;

    @Autowired
    private ObjectMapper objectMapper;

    private UUID organizationId;
    private UUID userId;

    private Organizations mockOrganizations;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        organizationId = UUID.randomUUID();
        userId = UUID.randomUUID();

        mockOrganizations = new Organizations();
        mockOrganizations.setId(organizationId);
        mockOrganizations.setName("Test Organization");
        mockOrganizations.setAuth0Id("org_123");
        mockOrganizations.setDomain("test.com");
        mockOrganizations.setDisplayName("Test Display Name");
        mockOrganizations.setIsDeleted(false);
        mockOrganizations.setDeletedBy(null);
        mockOrganizations.setDeletedAt(null);
        mockOrganizations.setCreatedAt(Instant.now());
        mockOrganizations.setUpdatedAt(Instant.now());

    }

    @Test
    void testGetAllOrganizations_Success() throws Exception {
        OrganizationsResponse response = new OrganizationsResponse();
        response.setId(organizationId);
        response.setName("Test Organization");

        when(organizationsService.getOrganizations()).thenReturn(Collections.singletonList(response));

        mockMvc.perform(get("/api/v1/organizations")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(organizationId.toString()))
                .andExpect(jsonPath("$[0].name").value("Test Organization"));

        verify(organizationsService, times(1)).getOrganizations();
    }

    @Test
    void testGetOrganizationById_Success() throws Exception {
        OrganizationResponse response = new OrganizationResponse();
        response.setId(organizationId);
        response.setName("Test Organization");

        when(organizationsService.getOrganizationById(organizationId)).thenReturn(response);

        mockMvc.perform(get("/api/v1/organization/{id}", organizationId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(organizationId.toString()))
                .andExpect(jsonPath("$.name").value("Test Organization"));

        verify(organizationsService, times(1)).getOrganizationById(organizationId);
    }

    @Test
    void testCreateOrganization_Success() throws Exception {
        CreateOrganizationRequest request = new CreateOrganizationRequest();
        request.setName("New Organization");

        OrganizationResponse response = new OrganizationResponse();
        response.setId(organizationId);
        response.setName("New Organization");

        when(organizationsService.createAnOrganization(any(CreateOrganizationRequest.class))).thenReturn(mockOrganizations);
        when(organizationsService.getOrganizationById(any(UUID.class))).thenReturn(response);

        mockMvc.perform(post("/api/v1/organization")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(organizationId.toString()))
                .andExpect(jsonPath("$.name").value("Test Organization"));

        verify(organizationsService, times(1)).createAnOrganization(any(CreateOrganizationRequest.class));
    }

    @Test
    void testUpdateOrganization_Success() throws Exception {
        UpdateOrganizationRequest request = new UpdateOrganizationRequest();
        request.setId(organizationId);
        request.setName("Updated Organization");

        OrganizationResponse response = new OrganizationResponse();
        response.setId(organizationId);
        response.setName("Updated Organization");

        when(organizationsService.updateAnOrganiation(any(UpdateOrganizationRequest.class))).thenReturn(response);

        mockMvc.perform(put("/api/v1/organization")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(organizationId.toString()))
                .andExpect(jsonPath("$.name").value("Updated Organization"));

        verify(organizationsService, times(1)).updateAnOrganiation(any(UpdateOrganizationRequest.class));
    }

    @Test
    void testDeleteOrganizationById_Success() throws Exception {
        String responseMessage = "Organization deleted successfully";

        when(organizationsService.deleteOrganizationById(organizationId, userId)).thenReturn(responseMessage);

        mockMvc.perform(delete("/api/v1/organization/{id}/by/{userId}", organizationId, userId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(responseMessage));

        verify(organizationsService, times(1)).deleteOrganizationById(organizationId, userId);
    }
}
