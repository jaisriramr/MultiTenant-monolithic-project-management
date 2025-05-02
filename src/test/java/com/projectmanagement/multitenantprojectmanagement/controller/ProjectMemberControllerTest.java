package com.projectmanagement.multitenantprojectmanagement.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.projectmanagement.multitenantprojectmanagement.core.projectMember.ProjectMember;
import com.projectmanagement.multitenantprojectmanagement.core.projectMember.ProjectMemberController;
import com.projectmanagement.multitenantprojectmanagement.core.projectMember.ProjectMemberService;
import com.projectmanagement.multitenantprojectmanagement.core.projectMember.dto.request.CreateProjectMember;
import com.projectmanagement.multitenantprojectmanagement.core.projectMember.dto.response.ProjectMemberDetailedResponse;
import com.projectmanagement.multitenantprojectmanagement.core.projectMember.dto.response.ProjectMembersResponse;
import com.projectmanagement.multitenantprojectmanagement.organizations.Organizations;
import com.projectmanagement.multitenantprojectmanagement.organizations.dto.response.PaginatedResponseDto;
import com.projectmanagement.multitenantprojectmanagement.roles.Roles;
import com.projectmanagement.multitenantprojectmanagement.users.Users;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProjectMemberController.class)
@AutoConfigureMockMvc(addFilters = false)
public class ProjectMemberControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProjectMemberService projectMemberService;

    @Autowired
    private ObjectMapper objectMapper;

    @Mock
    private Jwt jwt;

    private UUID memberId;
    private UUID projectId;
    private UUID roleId;

    private ProjectMember projectMember;
    private ProjectMemberDetailedResponse projectMemberDetailedResponse;
    private ProjectMembersResponse projectMembersResponse;
    private PaginatedResponseDto<ProjectMembersResponse> paginatedResponseDto;
    private List<ProjectMembersResponse> projectMembersResponseList;
    private List<ProjectMemberDetailedResponse> projectMemberDetailedResponseList;


    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        memberId = UUID.randomUUID();
        projectId = UUID.randomUUID();
        roleId = UUID.randomUUID();

        Roles role = new Roles();
        role.setId(roleId);
        role.setName("name");

        Users user = new Users();
        user.setId(memberId);
        user.setEmail("test@gmail.com");

        Organizations organizations = new Organizations();
        organizations.setId(UUID.randomUUID());

        projectMember = new ProjectMember();
        projectMember.setId(memberId);
        projectMember.setRole(role);
        projectMember.setUser(user);
        projectMember.setOrganization(organizations);
        
        projectMemberDetailedResponse = new ProjectMemberDetailedResponse();
        projectMemberDetailedResponse.setId(memberId);

        projectMembersResponse = new ProjectMembersResponse();
        projectMembersResponse.setId(memberId);

        projectMembersResponseList = Collections.singletonList(projectMembersResponse);
        projectMemberDetailedResponseList = Collections.singletonList(projectMemberDetailedResponse);
        paginatedResponseDto = PaginatedResponseDto.<ProjectMembersResponse>builder()
                .data(projectMembersResponseList)
                .page(0)
                .size(1)
                .totalElements(1)
                .totalPages(1)
                                .build();

    }

    @Test
    void testGetMemberById_Success() throws Exception {
        ProjectMemberDetailedResponse response = new ProjectMemberDetailedResponse();
        response.setId(memberId);

        when(projectMemberService.getMemberByUserId(memberId)).thenReturn(response);

        mockMvc.perform(get("/api/v1/project-member/{id}/user", memberId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(memberId.toString()));

        verify(projectMemberService, times(1)).getMemberByUserId(memberId);
    }

    @Test
    void testGetMembersByProjectId_Success() throws Exception {

        when(projectMemberService.getAllMembersByProjectId(eq(projectId), any())).thenReturn(paginatedResponseDto);

        mockMvc.perform(get("/api/v1/project-member/{id}/project", projectId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray());

        verify(projectMemberService, times(1)).getAllMembersByProjectId(eq(projectId), any());
    }

    @Test
    void testCreateProjectMember_Success() throws Exception {
        CreateProjectMember request = new CreateProjectMember();
        ProjectMemberDetailedResponse response = new ProjectMemberDetailedResponse();
        response.setId(memberId);

        when(projectMemberService.createProjectMember(any(CreateProjectMember.class))).thenReturn(response);

        mockMvc.perform(post("/api/v1/project-member")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(memberId.toString()));

        verify(projectMemberService, times(1)).createProjectMember(any(CreateProjectMember.class));
    }

    @Test
    void testUpdateProjectMember_Success() throws Exception {
        ProjectMemberDetailedResponse response = new ProjectMemberDetailedResponse();
        response.setId(memberId);

        when(projectMemberService.updateProjectMemberRole(eq(memberId), eq(roleId))).thenReturn(response);

        mockMvc.perform(put("/api/v1/project-member/{id}/role/{roleId}", memberId, roleId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(memberId.toString()));

        verify(projectMemberService, times(1)).updateProjectMemberRole(eq(memberId), eq(roleId));
    }

    @Test
    void testRemoveProjectMember_Success() throws Exception {
        ProjectMemberDetailedResponse response = new ProjectMemberDetailedResponse();
        response.setId(memberId);

        when(projectMemberService.removeMemberFromProject(memberId)).thenReturn(response);

        mockMvc.perform(delete("/api/v1/project-member/{id}", memberId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(memberId.toString()));

        verify(projectMemberService, times(1)).removeMemberFromProject(memberId);
    }
}
