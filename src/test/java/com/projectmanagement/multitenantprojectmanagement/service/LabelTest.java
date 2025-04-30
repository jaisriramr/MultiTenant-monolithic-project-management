package com.projectmanagement.multitenantprojectmanagement.service;

import com.projectmanagement.multitenantprojectmanagement.core.issue.Issue;
import com.projectmanagement.multitenantprojectmanagement.core.issue.IssueService;
import com.projectmanagement.multitenantprojectmanagement.core.label.*;
import com.projectmanagement.multitenantprojectmanagement.core.label.dto.request.CreateLabelRequest;
import com.projectmanagement.multitenantprojectmanagement.core.label.dto.response.LabelResponse;
import com.projectmanagement.multitenantprojectmanagement.core.label.mapper.LabelMapper;
import com.projectmanagement.multitenantprojectmanagement.core.project.ProjectService;
import com.projectmanagement.multitenantprojectmanagement.core.project.Projects;
import com.projectmanagement.multitenantprojectmanagement.exception.NotFoundException;
import com.projectmanagement.multitenantprojectmanagement.helper.MaskingString;
import com.projectmanagement.multitenantprojectmanagement.organizations.Organizations;
import com.projectmanagement.multitenantprojectmanagement.organizations.OrganizationsService;
import com.projectmanagement.multitenantprojectmanagement.organizations.dto.response.PaginatedResponseDto;
import com.projectmanagement.multitenantprojectmanagement.auth0.utils.JWTUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class LabelTest {

    @Mock
    private LabelRepository labelRepository;

    @Mock
    private MaskingString maskingString;

    @Mock
    private ProjectService projectService;

    @Mock
    private IssueService issueService;

    @Mock
    private JWTUtils jwtUtils;

    @Mock
    private OrganizationsService organizationsService;

    @InjectMocks
    private LabelService labelService;

    private UUID labelId;
    private Label mockLabel;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        labelId = UUID.randomUUID();
        mockLabel = new Label();
        mockLabel.setId(labelId);
        mockLabel.setName("Test Label");
    }

    @Test
    void testGetLabelEntity_Success() {
        String auth0OrgId = "auth0|12345";

        when(jwtUtils.getAuth0OrgId()).thenReturn(auth0OrgId);
        when(labelRepository.findByIdAndOrganization_Auth0Id(labelId, auth0OrgId)).thenReturn(Optional.of(mockLabel));
        when(maskingString.maskSensitive(anyString())).thenReturn("masked");

        Label result = labelService.getLabelEntity(labelId);

        assertNotNull(result);
        assertEquals(labelId, result.getId());
        assertEquals("Test Label", result.getName());
        verify(labelRepository, times(1)).findByIdAndOrganization_Auth0Id(labelId, auth0OrgId);
    }

    @Test
    void testGetLabelEntity_NotFound() {
        String auth0OrgId = "auth0|12345";

        when(jwtUtils.getAuth0OrgId()).thenReturn(auth0OrgId);
        when(labelRepository.findByIdAndOrganization_Auth0Id(labelId, auth0OrgId)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            labelService.getLabelEntity(labelId);
        });

        assertEquals("Label not found for the given ID: " + labelId, exception.getMessage());
        verify(labelRepository, times(1)).findByIdAndOrganization_Auth0Id(labelId, auth0OrgId);
    }

    @Test
    void testGetLabelById_Success() {
        when(labelRepository.findByIdAndOrganization_Auth0Id(any(UUID.class), anyString())).thenReturn(Optional.of(mockLabel));
        when(jwtUtils.getAuth0OrgId()).thenReturn("auth0|12345");

        LabelResponse response = labelService.getLabelById(labelId);

        assertNotNull(response);
        assertEquals("Test Label", response.getName());
        verify(labelRepository, times(1)).findByIdAndOrganization_Auth0Id(labelId, "auth0|12345");
    }

    @Test
    void testGetLabelsByProjectId_Success() {
        UUID projectId = UUID.randomUUID();
        Pageable pageable = Pageable.unpaged();
        Page<Label> labels = new PageImpl<>(Collections.singletonList(mockLabel));

        when(jwtUtils.getAuth0OrgId()).thenReturn("auth0|12345");
        when(labelRepository.findAllByProjectIdAndOrganization_Auth0Id(projectId, "auth0|12345", pageable)).thenReturn(labels);

        PaginatedResponseDto<LabelResponse> result = labelService.getLabelsByProjectId(projectId, pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(labelRepository, times(1)).findAllByProjectIdAndOrganization_Auth0Id(projectId, "auth0|12345", pageable);
    }

    @Test
    void testCreateLabel_Success() {
        CreateLabelRequest request = new CreateLabelRequest();
        request.setName("New Label");
        request.setProjectId(UUID.randomUUID());
        request.setIssueId(UUID.randomUUID());

        Projects project = new Projects();
        Issue issue = new Issue();
        Organizations organization = new Organizations();

        when(projectService.getProjectById(request.getProjectId())).thenReturn(project);
        when(issueService.getIssueById(request.getIssueId())).thenReturn(issue);
        when(jwtUtils.getAuth0OrgId()).thenReturn("auth0|12345");
        when(organizationsService.getOrganizationByAuth0Id("auth0|12345")).thenReturn(organization);
        when(labelRepository.save(any(Label.class))).thenReturn(mockLabel);

        Label result = labelService.createLabel(request);

        assertNotNull(result);
        assertEquals("Test Label", result.getName());
        verify(labelRepository, times(1)).save(any(Label.class));
    }

    @Test
    void testDeleteLabelById_Success() {
        when(labelRepository.findByIdAndOrganization_Auth0Id(any(UUID.class), anyString())).thenReturn(Optional.of(mockLabel));
        when(jwtUtils.getAuth0OrgId()).thenReturn("auth0|12345");

        LabelResponse response = labelService.deleteLabelById(labelId);

        assertNotNull(response);
        assertEquals("Test Label", response.getName());
        verify(labelRepository, times(1)).delete(mockLabel);
    }

    @Test
    void testDeleteLabelById_NotFound() {
        when(labelRepository.findByIdAndOrganization_Auth0Id(any(UUID.class), anyString())).thenReturn(Optional.empty());
        when(jwtUtils.getAuth0OrgId()).thenReturn("auth0|12345");

        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            labelService.deleteLabelById(labelId);
        });

        assertEquals("Label not found for the given ID: " + labelId, exception.getMessage());
        verify(labelRepository, times(1)).findByIdAndOrganization_Auth0Id(labelId, "auth0|12345");
    }
}
