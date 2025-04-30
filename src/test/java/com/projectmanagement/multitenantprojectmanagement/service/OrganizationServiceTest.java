package com.projectmanagement.multitenantprojectmanagement.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.projectmanagement.multitenantprojectmanagement.auth0.Auth0Service;
import com.projectmanagement.multitenantprojectmanagement.auth0.utils.JWTUtils;
import com.projectmanagement.multitenantprojectmanagement.exception.NotFoundException;
import com.projectmanagement.multitenantprojectmanagement.helper.MaskingString;
import com.projectmanagement.multitenantprojectmanagement.organizations.Organizations;
import com.projectmanagement.multitenantprojectmanagement.organizations.OrganizationsRepository;
import com.projectmanagement.multitenantprojectmanagement.organizations.OrganizationsService;
import com.projectmanagement.multitenantprojectmanagement.organizations.dto.request.CreateOrganizationRequest;
import com.projectmanagement.multitenantprojectmanagement.organizations.dto.request.UpdateOrganizationRequest;
import com.projectmanagement.multitenantprojectmanagement.organizations.dto.response.OrganizationResponse;

public class OrganizationServiceTest {

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
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        organizationId = UUID.fromString("c6e447db-1920-41a8-b728-bf831bf5f096");
        
        mockOrganization = new Organizations();
        mockOrganization.setId(organizationId);
        mockOrganization.setAuth0Id("orgId");
        mockOrganization.setName("Test Organization");
        mockOrganization.setDisplayName("Test Org");
        mockOrganization.setDomain("test.com");

        auth0Id = "orgId";

        when(jwtUtils.getAuth0OrgId()).thenReturn(auth0Id);
        when(maskingString.maskSensitive(anyString())).thenReturn("masked-string");
        
    }

    @Test
    public void testGetOrganizationById() {

        when(organizationsRepository.findById(organizationId)).thenReturn(java.util.Optional.of(mockOrganization));

        OrganizationResponse organization = organizationsService.getOrganizationById(organizationId);

        assert organization != null;
        assert organization.getId().equals(organizationId);
        assert organization.getName().equals("Test Organization");

    }

    @Test
    public void testGetOrganizationByAuth0Id() {

        when(organizationsRepository.findByAuth0Id(auth0Id)).thenReturn(java.util.Optional.of(mockOrganization));

        Organizations organization = organizationsService.getOrganizationByAuth0Id(auth0Id);

        assert organization != null;
        assert organization.getAuth0Id().equals(auth0Id);
        assert organization.getName().equals("Test Organization");

    }

    @Test
    public void testGetOrganizationByAuth0IdNotFound() {

        when(organizationsRepository.findByAuth0Id(auth0Id)).thenReturn(java.util.Optional.empty());

        try {
            organizationsService.getOrganizationByAuth0Id(auth0Id);
        } catch (Exception e) {
            assert e instanceof NotFoundException;
            assert e.getMessage().equals("Organization not found for the given Auth0 ID: " + auth0Id);
        }
    }

    @Test
    public void testGetOrganizationByIdNotFound() {

        when(organizationsRepository.findById(organizationId)).thenReturn(java.util.Optional.empty());

        try {
            organizationsService.getOrganizationById(organizationId);
        } catch (Exception e) {
            assert e instanceof NotFoundException;
            assert e.getMessage().equals("Organization not found for the given ID: " + organizationId);
        }
    }

    @Test
    public void testCreateAnOrganization() {
         
        when(organizationsRepository.findByName("Test Organization")).thenReturn(Optional.empty());
        when(organizationsRepository.findByDomain("test.com")).thenReturn(Optional.empty());

        Map<String, Object> mockBody = new HashMap<>();
        mockBody.put("id", "orgId");

        ResponseEntity<Map<String, Object>> mockResponse =
                new ResponseEntity<>(mockBody, HttpStatus.CREATED);

        when(auth0Service.createAnOrganization(anyString(), anyString()))
               .thenReturn(mockResponse);


        CreateOrganizationRequest createOrganizationRequest = CreateOrganizationRequest.builder()
                .name("Test Organization")
                .displayName("Test Org")
                .domain("test.com")
                .build();


        when(organizationsRepository.save(any(Organizations.class))).thenReturn(mockOrganization);
        
        Organizations createdOrganization = organizationsService.createAnOrganization(createOrganizationRequest);

        assert createdOrganization != null;
        assert createdOrganization.getName().equals("Test Organization");
        assert createdOrganization.getDisplayName().equals("Test Org");
        assert createdOrganization.getDomain().equals("test.com");

    }

    @Test
    public void testUpdateOrganization() {
        when(organizationsRepository.findById(organizationId)).thenReturn(Optional.of(mockOrganization));
        when(organizationsRepository.save(any(Organizations.class))).thenReturn(mockOrganization);

        UpdateOrganizationRequest updateOrganizationRequest = UpdateOrganizationRequest.builder()
                .id(organizationId)
                .name("Test Organization")
                .displayName("Updated Org")
                .build();

        when(organizationsRepository.findByDomain("updated.com")).thenReturn(Optional.empty());

        OrganizationResponse updatedOrganization = organizationsService.updateAnOrganiation(updateOrganizationRequest);

        assert updatedOrganization != null;
        assert updatedOrganization.getId().equals(organizationId);
        assert updatedOrganization.getName().equals("Test Organization");
    }

    @Test
    public void testDeleteOrganizationById() {
        when(organizationsRepository.findById(organizationId)).thenReturn(Optional.of(mockOrganization));
        when(organizationsRepository.save(any(Organizations.class))).thenReturn(mockOrganization);

        String organization = organizationsService.deleteOrganizationById(organizationId, organizationId);

        assert organization != null;
        assert organization.equals("Organization with Id " + organizationId + " is removed successfully!");
    }

}
