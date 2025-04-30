package com.projectmanagement.multitenantprojectmanagement.service;

import com.projectmanagement.multitenantprojectmanagement.auth0.utils.JWTUtils;
import com.projectmanagement.multitenantprojectmanagement.core.project.Projects;
import com.projectmanagement.multitenantprojectmanagement.core.project.ProjectService;
import com.projectmanagement.multitenantprojectmanagement.core.projectMember.*;
import com.projectmanagement.multitenantprojectmanagement.core.projectMember.dto.request.CreateProjectMember;
import com.projectmanagement.multitenantprojectmanagement.core.projectMember.dto.response.ProjectMemberDetailedResponse;
import com.projectmanagement.multitenantprojectmanagement.core.projectMember.dto.response.ProjectMembersResponse;
import com.projectmanagement.multitenantprojectmanagement.exception.NotFoundException;
import com.projectmanagement.multitenantprojectmanagement.helper.MaskingString;
import com.projectmanagement.multitenantprojectmanagement.organizations.Organizations;
import com.projectmanagement.multitenantprojectmanagement.organizations.OrganizationsService;
import com.projectmanagement.multitenantprojectmanagement.organizations.dto.response.PaginatedResponseDto;
import com.projectmanagement.multitenantprojectmanagement.roles.Roles;
import com.projectmanagement.multitenantprojectmanagement.roles.RolesService;
import com.projectmanagement.multitenantprojectmanagement.users.UserService;
import com.projectmanagement.multitenantprojectmanagement.users.Users;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class ProjectMemberTest {

    @Mock
    private ProjectMemberRepository projectMemberRepository;

    @Mock
    private ProjectService projectService;

    @Mock
    private UserService userService;

    @Mock
    private RolesService rolesService;

    @Mock
    private OrganizationsService organizationsService;

    @Mock
    private JWTUtils jwtUtils;

    @Mock
    private MaskingString maskingString;

    @InjectMocks
    private ProjectMemberService projectMemberService;

    private UUID memberId;
    private ProjectMember mockMember;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        memberId = UUID.randomUUID();
        mockMember = new ProjectMember();
        mockMember.setId(memberId);

        Projects project = new Projects();
        project.setId(UUID.randomUUID());

        Users user = new Users();
        user.setId(UUID.randomUUID());

        Roles role = new Roles();
        role.setId(UUID.randomUUID());
        role.setName("Admin");

        Organizations organization = new Organizations();
        organization.setId(UUID.randomUUID());
        organization.setName("Test Organization");
        organization.setAuth0Id("auth0|12345");

        mockMember.setProject(project);
        mockMember.setUser(user);
        mockMember.setRole(role);
        mockMember.setOrganization(organization);

        
        when(jwtUtils.getAuth0OrgId()).thenReturn("orgId");
        when(maskingString.maskSensitive(anyString())).thenReturn("masked-string");
    }

    @Test
    void testGetProjectMemberEntity_Success() {
        String auth0OrgId = "auth0|12345";

        when(jwtUtils.getAuth0OrgId()).thenReturn(auth0OrgId);
        when(projectMemberRepository.findByIdAndOrganization_Auth0Id(memberId, auth0OrgId)).thenReturn(Optional.of(mockMember));
        when(maskingString.maskSensitive(anyString())).thenReturn("masked");

        ProjectMember result = projectMemberService.getProjectMemberEntity(memberId);

        assertNotNull(result);
        assertEquals(memberId, result.getId());
        verify(projectMemberRepository, times(1)).findByIdAndOrganization_Auth0Id(memberId, auth0OrgId);
    }

    @Test
    void testGetProjectMemberEntity_NotFound() {
        String auth0OrgId = "auth0|12345";

        when(jwtUtils.getAuth0OrgId()).thenReturn(auth0OrgId);
        when(projectMemberRepository.findByIdAndOrganization_Auth0Id(memberId, auth0OrgId)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            projectMemberService.getProjectMemberEntity(memberId);
        });

        assertEquals("Project member not found for the given ID: " + memberId, exception.getMessage());
        verify(projectMemberRepository, times(1)).findByIdAndOrganization_Auth0Id(memberId, auth0OrgId);
    }

    @Test
    void testGetMemberByUserId_Success() {
        String auth0OrgId = "auth0|12345";

        when(jwtUtils.getAuth0OrgId()).thenReturn(auth0OrgId);
        when(projectMemberRepository.findByUserIdAndOrganization_Auth0Id(memberId, auth0OrgId)).thenReturn(Optional.of(mockMember));

        ProjectMemberDetailedResponse result = projectMemberService.getMemberByUserId(memberId);

        assertNotNull(result);
        verify(projectMemberRepository, times(1)).findByUserIdAndOrganization_Auth0Id(memberId, auth0OrgId);
    }

    @Test
    void testGetAllMembersByProjectId_Success() {
        UUID projectId = UUID.randomUUID();
        Pageable pageable = Pageable.unpaged();
        Page<ProjectMember> members = new PageImpl<>(Collections.singletonList(mockMember));

        when(jwtUtils.getAuth0OrgId()).thenReturn("auth0|12345");
        when(projectMemberRepository.findAllByProjectIdAndOrganization_Auth0Id(projectId, "auth0|12345", pageable)).thenReturn(members);

        PaginatedResponseDto<ProjectMembersResponse> result = projectMemberService.getAllMembersByProjectId(projectId, pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(projectMemberRepository, times(1)).findAllByProjectIdAndOrganization_Auth0Id(projectId, "auth0|12345", pageable);
    }

    @Test
    void testCreateProjectMember_Success() {
        CreateProjectMember request = new CreateProjectMember();
        request.setProjectId(UUID.randomUUID());
        request.setUserId(UUID.randomUUID());
        request.setRoleId(UUID.randomUUID());

        Projects project = new Projects();
        Users user = new Users();
        Roles role = new Roles();
        Organizations organization = new Organizations();

        when(projectService.getProjectById(request.getProjectId())).thenReturn(project);
        when(userService.getUserEntity(request.getUserId())).thenReturn(user);
        when(rolesService.findRoleEntityById(request.getRoleId())).thenReturn(role);
        when(jwtUtils.getAuth0OrgId()).thenReturn("auth0|12345");
        when(organizationsService.getOrganizationByAuth0Id("auth0|12345")).thenReturn(organization);
        when(projectMemberRepository.save(any(ProjectMember.class))).thenReturn(mockMember);

        ProjectMemberDetailedResponse result = projectMemberService.createProjectMember(request);

        assertNotNull(result);
        verify(projectMemberRepository, times(1)).save(any(ProjectMember.class));
    }

    @Test
    void testUpdateProjectMemberRole_Success() {

        when(projectMemberRepository.findByIdAndOrganization_Auth0Id(memberId, "orgId")).thenReturn(Optional.of(mockMember));
        
        when(rolesService.findRoleEntityById(memberId)).thenReturn(mockMember.getRole());

        when(projectMemberRepository.save(any(ProjectMember.class))).thenReturn(mockMember);

        ProjectMemberDetailedResponse result = projectMemberService.updateProjectMemberRole(memberId, memberId);

        assertNotNull(result);
        verify(projectMemberRepository, times(1)).save(mockMember);
    }

    @Test
    void testRemoveMemberFromProject_Success() {
        Projects project = new Projects();
        mockMember.setProject(project);
        
        when(projectMemberRepository.findByIdAndOrganization_Auth0Id(memberId, "orgId")).thenReturn(Optional.of(mockMember));
        

        ProjectMemberDetailedResponse result = projectMemberService.removeMemberFromProject(memberId);

        assertNotNull(result);
        verify(projectMemberRepository, never()).delete(mockMember); // Member is removed from the project, not deleted
    }
}
