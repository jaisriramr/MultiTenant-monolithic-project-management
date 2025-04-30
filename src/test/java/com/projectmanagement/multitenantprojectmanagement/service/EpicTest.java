package com.projectmanagement.multitenantprojectmanagement.service;

import com.projectmanagement.multitenantprojectmanagement.core.epic.Epic;
import com.projectmanagement.multitenantprojectmanagement.core.epic.EpicRepository;
import com.projectmanagement.multitenantprojectmanagement.core.epic.EpicService;
import com.projectmanagement.multitenantprojectmanagement.core.epic.dto.request.CreateEpicRequest;
import com.projectmanagement.multitenantprojectmanagement.core.project.ProjectService;
import com.projectmanagement.multitenantprojectmanagement.core.project.Projects;
import com.projectmanagement.multitenantprojectmanagement.exception.NotFoundException;
import com.projectmanagement.multitenantprojectmanagement.helper.MaskingString;
import com.projectmanagement.multitenantprojectmanagement.organizations.Organizations;
import com.projectmanagement.multitenantprojectmanagement.organizations.OrganizationsService;
import com.projectmanagement.multitenantprojectmanagement.auth0.utils.JWTUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class EpicTest {

    @Mock
    private EpicRepository epicRepository;

    @Mock
    private ProjectService projectService;

    @Mock
    private OrganizationsService organizationsService;

    @Mock
    private MaskingString maskingString;

    @Mock
    private JWTUtils jwtUtils;

    @InjectMocks
    private EpicService epicService;

    private UUID epicId;
    private Epic mockEpic;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        epicId = UUID.randomUUID();
        mockEpic = new Epic();
        mockEpic.setId(epicId);
        mockEpic.setName("Test Epic");
        mockEpic.setDescription("This is a test epic.");
    }

    @Test
    void testGetEpicEntity_Success() {
        String auth0OrgId = "auth0|12345";

        when(jwtUtils.getAuth0OrgId()).thenReturn(auth0OrgId);
        when(epicRepository.findByIdAndOrganization_Auth0Id(epicId, auth0OrgId)).thenReturn(Optional.of(mockEpic));
        when(maskingString.maskSensitive(anyString())).thenReturn("masked");

        Epic result = epicService.getEpicEntity(epicId);

        assertNotNull(result);
        assertEquals(epicId, result.getId());
        assertEquals("Test Epic", result.getName());
        verify(epicRepository, times(1)).findByIdAndOrganization_Auth0Id(epicId, auth0OrgId);
    }

    @Test
    void testGetEpicEntity_NotFound() {
        String auth0OrgId = "auth0|12345";

        when(jwtUtils.getAuth0OrgId()).thenReturn(auth0OrgId);
        when(epicRepository.findByIdAndOrganization_Auth0Id(epicId, auth0OrgId)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            epicService.getEpicEntity(epicId);
        });

        assertEquals("Epic not found for the given ID: " + epicId, exception.getMessage());
        verify(epicRepository, times(1)).findByIdAndOrganization_Auth0Id(epicId, auth0OrgId);
    }

    @Test
    void testCreateEpicEntity_Success() {
        CreateEpicRequest request = new CreateEpicRequest();
        request.setName("New Epic");
        request.setDescription("Epic description");
        request.setProjectId(UUID.randomUUID());

        Projects project = new Projects();
        Organizations organization = new Organizations();

        when(projectService.getProjectById(request.getProjectId())).thenReturn(project);
        when(jwtUtils.getAuth0OrgId()).thenReturn("auth0|12345");
        when(organizationsService.getOrganizationByAuth0Id("auth0|12345")).thenReturn(organization);
        when(epicRepository.save(any(Epic.class))).thenReturn(mockEpic);

        Epic result = epicService.createEpicEntity(request);

        assertNotNull(result);
        assertEquals("Test Epic", result.getName());
        verify(epicRepository, times(1)).save(any(Epic.class));
    }

    @Test
    void testCreateEpicEntity_ProjectNotFound() {
        CreateEpicRequest request = new CreateEpicRequest();
        request.setName("New Epic");
        request.setDescription("Epic description");
        request.setProjectId(UUID.randomUUID());

        when(projectService.getProjectById(request.getProjectId())).thenThrow(new NotFoundException("Project not found"));

        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            epicService.createEpicEntity(request);
        });

        assertEquals("Project not found", exception.getMessage());
        verify(projectService, times(1)).getProjectById(request.getProjectId());
        verify(epicRepository, never()).save(any(Epic.class));
    }
}
