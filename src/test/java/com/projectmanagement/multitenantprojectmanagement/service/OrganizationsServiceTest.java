package com.projectmanagement.multitenantprojectmanagement.service;

import com.projectmanagement.multitenantprojectmanagement.auth0.Auth0Service;
import com.projectmanagement.multitenantprojectmanagement.auth0.utils.JWTUtils;
import com.projectmanagement.multitenantprojectmanagement.exception.ConflictException;
import com.projectmanagement.multitenantprojectmanagement.exception.NotFoundException;
import com.projectmanagement.multitenantprojectmanagement.helper.MaskingString;
import com.projectmanagement.multitenantprojectmanagement.organizations.Organizations;
import com.projectmanagement.multitenantprojectmanagement.organizations.OrganizationsRepository;
import com.projectmanagement.multitenantprojectmanagement.organizations.OrganizationsService;
import com.projectmanagement.multitenantprojectmanagement.organizations.dto.request.CreateOrganizationRequest;
import com.projectmanagement.multitenantprojectmanagement.organizations.dto.response.OrganizationResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class OrganizationsServiceTest {

    @Mock
    private OrganizationsRepository organizationsRepository;

    @Mock
    private Auth0Service auth0Service;

    @Mock
    private MaskingString maskingString;

    @Mock
    private JWTUtils jwtUtils;

    @InjectMocks
    private OrganizationsService organizationsService;

    private UUID organizationId;
    private Organizations mockOrganization;
    private String auth0Id;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        organizationId = UUID.fromString("c6e447db-1920-41a8-b728-bf831bf5f096");
        Organizations organization = new Organizations();
        organization.setId(organizationId);
        organization.setName("Test Organization");
        auth0Id = "auth0|12345";
        mockOrganization = organization;
        mockOrganization.setAuth0Id(auth0Id);

        when(jwtUtils.getAuth0OrgId()).thenReturn("orgId");
        when(maskingString.maskSensitive(anyString())).thenReturn("masked-string");
    }

    @Test
    void testGetOrganizationById_Success() {

        when(organizationsRepository.findById(organizationId)).thenReturn(Optional.of(mockOrganization));
        when(maskingString.maskSensitive(anyString())).thenReturn("masked");

        OrganizationResponse response = organizationsService.getOrganizationById(organizationId);

        assertNotNull(response);
        assertEquals(organizationId, response.getId());
        assertEquals("Test Organization", response.getName());
        verify(organizationsRepository, times(1)).findById(organizationId);
    }

    @Test
    void testGetOrganizationById_NotFound() {
        when(organizationsRepository.findById(organizationId)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            organizationsService.getOrganizationById(organizationId);
        });

        assertEquals("Organization not found for the given ID: " + organizationId, exception.getMessage());
        verify(organizationsRepository, times(1)).findById(organizationId);
    }

    @Test
    void testGetOrganizationByAuth0Id_Success() {
        

        when(organizationsRepository.findByAuth0Id(auth0Id)).thenReturn(Optional.of(mockOrganization));
        when(maskingString.maskSensitive(anyString())).thenReturn("masked");

        Organizations result = organizationsService.getOrganizationByAuth0Id(auth0Id);

        assertNotNull(result);
        assertEquals(auth0Id, result.getAuth0Id());
        verify(organizationsRepository, times(1)).findByAuth0Id(auth0Id);
    }

    @Test
    void testGetOrganizationByAuth0Id_NotFound() {
        when(organizationsRepository.findByAuth0Id(auth0Id)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            organizationsService.getOrganizationByAuth0Id(auth0Id);
        });

        assertEquals("Organization not found for the given Auth0 ID: " + auth0Id, exception.getMessage());
        verify(organizationsRepository, times(1)).findByAuth0Id(auth0Id);
    }

    @Test
    void testCreateAnOrganization_Success() {
        CreateOrganizationRequest request = new CreateOrganizationRequest();
        request.setName("Test Organization");
        request.setDisplayName("Test Organization");
        request.setDomain("test.org");

        Map<String, Object> auth0ResponseBody = new HashMap<>();
        auth0ResponseBody.put("id", "auth0|12345");
        ResponseEntity<Map<String, Object>> auth0Response = ResponseEntity.ok(auth0ResponseBody);

        when(organizationsRepository.findByName(request.getName())).thenReturn(Optional.empty());
        when(organizationsRepository.findByDomain(request.getDomain())).thenReturn(Optional.empty());
        when(auth0Service.createAnOrganization(request.getName(), request.getDisplayName())).thenReturn(auth0Response);
        when(organizationsRepository.save(any(Organizations.class))).thenReturn(mockOrganization);

        Organizations result = organizationsService.createAnOrganization(request);

        assertNotNull(result);
        assertEquals(request.getName(), result.getName());
        verify(organizationsRepository, times(1)).save(any(Organizations.class));
    }

    @Test
    void testCreateAnOrganization_Conflict() {
        CreateOrganizationRequest request = new CreateOrganizationRequest();
        request.setName("Test Org");
        request.setDomain("test.org");

        when(organizationsRepository.findByName(request.getName())).thenReturn(Optional.of(new Organizations()));

        ConflictException exception = assertThrows(ConflictException.class, () -> {
            organizationsService.createAnOrganization(request);
        });

        assertEquals("Organization with the same name already exists", exception.getMessage());
        verify(organizationsRepository, times(1)).findByName(request.getName());
    }

    @Test
    void testDeleteOrganizationById_Success() {
        UUID organizationId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        Organizations organization = new Organizations();
        organization.setId(organizationId);

        when(organizationsRepository.findById(organizationId)).thenReturn(Optional.of(organization));
        when(jwtUtils.getCurrentUserId()).thenReturn("auth0|12345");

        String result = organizationsService.deleteOrganizationById(organizationId, userId);

        assertEquals("Organization with Id " + organizationId + " is removed successfully!", result);
        verify(organizationsRepository, times(1)).save(any(Organizations.class));
    }

    @Test
    void testDeleteOrganizationById_NotFound() {
        UUID organizationId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        when(organizationsRepository.findById(organizationId)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            organizationsService.deleteOrganizationById(organizationId, userId);
        });

        assertEquals("Organization not found for the given ID: " + organizationId, exception.getMessage());
        verify(organizationsRepository, times(1)).findById(organizationId);
    }
}
