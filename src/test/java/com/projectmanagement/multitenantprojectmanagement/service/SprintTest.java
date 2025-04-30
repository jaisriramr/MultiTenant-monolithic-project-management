package com.projectmanagement.multitenantprojectmanagement.service;

import com.projectmanagement.multitenantprojectmanagement.auth0.utils.JWTUtils;
import com.projectmanagement.multitenantprojectmanagement.core.project.ProjectService;
import com.projectmanagement.multitenantprojectmanagement.core.project.Projects;
import com.projectmanagement.multitenantprojectmanagement.core.sprint.*;
import com.projectmanagement.multitenantprojectmanagement.core.sprint.dto.request.CreateSprintRequest;
import com.projectmanagement.multitenantprojectmanagement.core.sprint.dto.request.UpdateSprintRequest;
import com.projectmanagement.multitenantprojectmanagement.core.sprint.dto.response.ListSprintResponse;
import com.projectmanagement.multitenantprojectmanagement.core.sprint.dto.response.MinimalSprintResponse;
import com.projectmanagement.multitenantprojectmanagement.core.sprint.dto.response.SprintDetailedResponse;
import com.projectmanagement.multitenantprojectmanagement.core.sprint.enums.SprintStatus;
import com.projectmanagement.multitenantprojectmanagement.exception.BadRequestException;
import com.projectmanagement.multitenantprojectmanagement.exception.NotFoundException;
import com.projectmanagement.multitenantprojectmanagement.helper.MaskingString;
import com.projectmanagement.multitenantprojectmanagement.organizations.Organizations;
import com.projectmanagement.multitenantprojectmanagement.organizations.OrganizationsService;
import com.projectmanagement.multitenantprojectmanagement.organizations.dto.response.PaginatedResponseDto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class SprintTest {

    @Mock
    private SprintRepository sprintRepository;

    @Mock
    private ProjectService projectService;

    @Mock
    private OrganizationsService organizationsService;

    @Mock
    private MaskingString maskingString;

    @Mock
    private JWTUtils jwtUtils;

    @InjectMocks
    private SprintService sprintService;

    private UUID sprintId;
    private Sprint mockSprint;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        sprintId = UUID.fromString("c6e447db-1920-41a8-b728-bf831bf5f096");;
        mockSprint = new Sprint();
        mockSprint.setId(sprintId);
        mockSprint.setName("Test Sprint");
        mockSprint.setStatus(SprintStatus.ACTIVE);
        mockSprint.setCreatedAt(Instant.now());
        mockSprint.setUpdatedAt(Instant.now());

        mockSprint.setStartDate(LocalDate.now());
        mockSprint.setEndDate(LocalDate.now().plusDays(10));
        
        Projects project = new Projects();
        project.setId(UUID.randomUUID());
        project.setName("Test Project");
        project.setCreatedAt(Instant.now());
        project.setUpdatedAt(Instant.now());

        mockSprint.setProject(project);
        Organizations organization = new Organizations();
        organization.setId(UUID.randomUUID());
        organization.setName("Test Organization");
        
        project.setOrganization(organization);
        mockSprint.setOrganization(organization);

        when(jwtUtils.getAuth0OrgId()).thenReturn("orgId");
        when(maskingString.maskSensitive(anyString())).thenReturn("masked-string");
    }

    @Test
    void testGetSprintEntity_Success() {
        String auth0OrgId = "auth0|12345";

        when(jwtUtils.getAuth0OrgId()).thenReturn(auth0OrgId);
        when(sprintRepository.findByIdAndOrganization_Auth0Id(sprintId, auth0OrgId)).thenReturn(Optional.of(mockSprint));
        when(maskingString.maskSensitive(anyString())).thenReturn("masked");

        Sprint result = sprintService.getSprintEntity(sprintId);

        assertNotNull(result);
        assertEquals(sprintId, result.getId());
        assertEquals("Test Sprint", result.getName());
        verify(sprintRepository, times(1)).findByIdAndOrganization_Auth0Id(sprintId, auth0OrgId);
    }

    @Test
    void testGetSprintEntity_NotFound() {
        String auth0OrgId = "auth0|12345";

        when(jwtUtils.getAuth0OrgId()).thenReturn(auth0OrgId);
        when(sprintRepository.findByIdAndOrganization_Auth0Id(sprintId, auth0OrgId)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            sprintService.getSprintEntity(sprintId);
        });

        assertEquals("Sprint not found for the given ID: " + sprintId, exception.getMessage());
        verify(sprintRepository, times(1)).findByIdAndOrganization_Auth0Id(sprintId, auth0OrgId);
    }

    @Test
    void testGetAllSprintByProjectId_Success() {
        UUID projectId = UUID.randomUUID();
        Pageable pageable = Pageable.unpaged();
        Page<Sprint> sprints = new PageImpl<>(Collections.singletonList(mockSprint));

        when(jwtUtils.getAuth0OrgId()).thenReturn("auth0|12345");
        when(sprintRepository.findAllByProjectIdAndOrganization_Auth0Id(projectId, "auth0|12345", pageable)).thenReturn(sprints);

        PaginatedResponseDto<ListSprintResponse> result = sprintService.getAllSprintByProjectId(projectId, pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(sprintRepository, times(1)).findAllByProjectIdAndOrganization_Auth0Id(projectId, "auth0|12345", pageable);
    }

    @Test
    void testCreateSprint_Success() {
        CreateSprintRequest request = new CreateSprintRequest();
        request.setName("New Sprint");
        request.setProjectId(UUID.randomUUID());
        request.setStartDate("01/01/2025");
        request.setEndDate("31/01/2025");
        request.setDescription("Sprint Description");

        Projects project = new Projects();
        Organizations organization = new Organizations();

        when(projectService.getProjectById(request.getProjectId())).thenReturn(project);
        when(jwtUtils.getAuth0OrgId()).thenReturn("auth0|12345");
        when(organizationsService.getOrganizationByAuth0Id("auth0|12345")).thenReturn(organization);
        when(sprintRepository.save(any(Sprint.class))).thenReturn(mockSprint);

        SprintDetailedResponse result = sprintService.createSprint(request);

        assertNotNull(result);
        assertEquals("Test Sprint", result.getName());
        verify(sprintRepository, times(1)).save(any(Sprint.class));
    }

    @Test
    void testCreateSprint_AlreadyExists() {
        CreateSprintRequest request = new CreateSprintRequest();
        request.setName("Existing Sprint");
        request.setProjectId(UUID.randomUUID());

        when(jwtUtils.getAuth0OrgId()).thenReturn("auth0|12345");
        when(sprintRepository.findByNameAndProjectIdAndOrganization_Auth0Id(request.getName(), request.getProjectId(), "auth0|12345"))
                .thenReturn(Optional.of(mockSprint));

        BadRequestException exception = assertThrows(BadRequestException.class, () -> {
            sprintService.createSprint(request);
        });

        assertEquals("Sprint with that name already exist in your project", exception.getMessage());
        verify(sprintRepository, never()).save(any(Sprint.class));
    }

    @Test
    void testUpdateSprint_Success() {
        UpdateSprintRequest request = new UpdateSprintRequest();
        request.setId(sprintId);
        request.setName("Updated Sprint");
        request.setDescription("Updated Description");
        request.setStartDate("01/01/2025");
        request.setEndDate("31/01/2025");

        when(sprintRepository.findByIdAndOrganization_Auth0Id(eq(sprintId), anyString())).thenReturn(Optional.of(mockSprint));
        when(sprintRepository.save(any(Sprint.class))).thenReturn(mockSprint);

        SprintDetailedResponse result = sprintService.updateSprint(request);

        assertNotNull(result);
        assertEquals("Updated Sprint", result.getName());
        verify(sprintRepository, times(1)).save(any(Sprint.class));
    }

    @Test
    void testDeleteSprint_Success() {
        when(sprintRepository.findByIdAndOrganization_Auth0Id(eq(sprintId), anyString())).thenReturn(Optional.of(mockSprint));

        MinimalSprintResponse result = sprintService.deleteSprint(sprintId);

        assertNotNull(result);
        verify(sprintRepository, times(1)).delete(mockSprint);
    }

    @Test
    void testDeleteSprint_NotFound() {
        when(sprintRepository.findByIdAndOrganization_Auth0Id(eq(sprintId), anyString())).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            sprintService.deleteSprint(sprintId);
        });

        assertEquals("Sprint not found for the given ID: " + sprintId, exception.getMessage());
    }
}
