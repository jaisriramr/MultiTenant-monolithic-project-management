package com.projectmanagement.multitenantprojectmanagement.service;

import com.projectmanagement.multitenantprojectmanagement.auth0.utils.JWTUtils;
import com.projectmanagement.multitenantprojectmanagement.core.project.*;
import com.projectmanagement.multitenantprojectmanagement.core.project.dto.request.CreateProjectRequest;
import com.projectmanagement.multitenantprojectmanagement.core.project.dto.request.UpdateProjectRequest;
import com.projectmanagement.multitenantprojectmanagement.core.project.dto.response.ProjectDetailsResponse;
import com.projectmanagement.multitenantprojectmanagement.core.project.dto.response.ProjectsResponse;
import com.projectmanagement.multitenantprojectmanagement.core.project.enums.ProjectStatus;
import com.projectmanagement.multitenantprojectmanagement.core.project.mapper.ProjectMapper;
import com.projectmanagement.multitenantprojectmanagement.exception.NotFoundException;
import com.projectmanagement.multitenantprojectmanagement.helper.MaskingString;
import com.projectmanagement.multitenantprojectmanagement.organizationmembers.OrganizationMembers;
import com.projectmanagement.multitenantprojectmanagement.organizationmembers.OrganizationMembersService;
import com.projectmanagement.multitenantprojectmanagement.organizations.Organizations;
import com.projectmanagement.multitenantprojectmanagement.organizations.dto.response.PaginatedResponseDto;
import com.projectmanagement.multitenantprojectmanagement.users.Users;

import org.aspectj.weaver.ast.Or;
import org.hibernate.Hibernate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.Instant;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class ProjectTest {

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private OrganizationMembersService organizationMembersService;

    @Mock
    private JWTUtils jwtUtils;

    @Mock
    private MaskingString maskingString;

    @InjectMocks
    private ProjectService projectService;

    private UUID projectId;
    private Projects mockProject;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        projectId = UUID.fromString("c6e447db-1920-41a8-b728-bf831bf5f096");
        mockProject = new Projects();
        mockProject.setId(projectId);
        mockProject.setName("Test Project");
        
        Organizations organization = new Organizations();
        organization.setId(UUID.randomUUID());
        organization.setName("Test Organization");
        mockProject.setOrganization(organization);

        Users user = new Users();
        user.setId(UUID.randomUUID());
        user.setName("Test User");
        mockProject.setCreatedBy(user);

        mockProject.setStatus(ProjectStatus.ACTIVE);
        mockProject.setCreatedAt(Instant.now());
        mockProject.setUpdatedAt(Instant.now());

        mockProject.setKey("TUP");
        
        when(jwtUtils.getAuth0OrgId()).thenReturn("orgId");
        when(maskingString.maskSensitive(anyString())).thenReturn("masked-string");
    }

    @Test
    void testGetProjectById_Success() {
        String auth0OrgId = "auth0|12345";

        when(jwtUtils.getAuth0OrgId()).thenReturn(auth0OrgId);
        when(projectRepository.findByIdAndOrganization_Auth0Id(projectId, auth0OrgId)).thenReturn(Optional.of(mockProject));
        when(maskingString.maskSensitive(anyString())).thenReturn("masked");

        Projects result = projectService.getProjectById(projectId);

        assertNotNull(result);
        assertEquals(projectId, result.getId());
        assertEquals("Test Project", result.getName());
        verify(projectRepository, times(1)).findByIdAndOrganization_Auth0Id(projectId, auth0OrgId);
    }

    @Test
    void testGetProjectById_NotFound() {
        String auth0OrgId = "auth0|12345";

        when(jwtUtils.getAuth0OrgId()).thenReturn(auth0OrgId);
        when(projectRepository.findByIdAndOrganization_Auth0Id(projectId, auth0OrgId)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            projectService.getProjectById(projectId);
        });

        assertEquals("Project not found for the given ID: " + projectId, exception.getMessage());
        verify(projectRepository, times(1)).findByIdAndOrganization_Auth0Id(projectId, auth0OrgId);
    }

    @Test
    void testGetProjectByIdForController_Success() {

        when(projectRepository.findByIdAndOrganization_Auth0Id(eq(projectId), anyString())).thenReturn(Optional.of(mockProject));

        ProjectDetailsResponse result = projectService.getProjectByIdForController(projectId);

        assertNotNull(result);
        assertEquals("Test Project", result.getName());
    }

    @Test
    void testUpdateProjectIssueCount_Success() {
        Long issueCount = 10L;

        when(projectRepository.findByIdAndOrganization_Auth0Id(eq(projectId), anyString())).thenReturn(Optional.of(mockProject));

        projectService.updateProjectIssueCount(projectId, issueCount);

        assertEquals(issueCount, mockProject.getIssueCount());
        verify(projectRepository, times(1)).save(mockProject);
    }

    @Test
    void testGetAllProjectsByOrganizationId_Success() {
        UUID orgId = UUID.randomUUID();
        Pageable pageable = Pageable.unpaged();
        Page<Projects> projects = new PageImpl<>(Collections.singletonList(mockProject));

        when(projectRepository.findAllByOrganizationId(orgId, pageable)).thenReturn(projects);

        PaginatedResponseDto<ProjectsResponse> result = projectService.getAllProjectsByOrganizationId(orgId, pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(projectRepository, times(1)).findAllByOrganizationId(orgId, pageable);
    }

    @Test
    void testCreateProject_Success() {
        CreateProjectRequest request = new CreateProjectRequest();
        request.setName("New Project");

        OrganizationMembers member = new OrganizationMembers();
        Projects savedProject = new Projects();
        savedProject.setId(projectId);

        when(jwtUtils.getCurrentUserId()).thenReturn("auth0|user123");
        when(jwtUtils.getAuth0OrgId()).thenReturn("auth0|org123");
        when(organizationMembersService.getOrganizationMemberbyAuth0UserIdAndAuth0OrgId(anyString(), anyString())).thenReturn(member);
        when(projectRepository.save(any(Projects.class))).thenReturn(mockProject);

        ProjectDetailsResponse result = projectService.createProject(request);

        assertNotNull(result);
        assertEquals(projectId, result.getId());
        verify(projectRepository, times(1)).save(any(Projects.class));
    }

    @Test
    void testUpdateProject_Success() {
        UpdateProjectRequest request = new UpdateProjectRequest();
        request.setId(projectId);
        request.setName("Updated Project");

        when(projectRepository.findByIdAndOrganization_Auth0Id(eq(projectId), anyString())).thenReturn(Optional.of(mockProject));
        
    
        when(projectRepository.save(any(Projects.class))).thenReturn(mockProject);

        ProjectDetailsResponse result = projectService.updateProject(request);

        assertNotNull(result);
        assertEquals("Updated Project", result.getName());
        verify(projectRepository, times(1)).save(mockProject);
    }

    @Test
    void testDeleteProjectById_Success() {

        when(projectRepository.findByIdAndOrganization_Auth0Id(eq(projectId), anyString())).thenReturn(Optional.of(mockProject));

        ProjectDetailsResponse result = projectService.deleteProjectById(projectId);

        assertNotNull(result);
        verify(projectRepository, times(1)).delete(mockProject);
    }
}
