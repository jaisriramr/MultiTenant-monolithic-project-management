package com.projectmanagement.multitenantprojectmanagement.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.projectmanagement.multitenantprojectmanagement.core.project.ProjectService;
import com.projectmanagement.multitenantprojectmanagement.core.project.Projectcontroller;
import com.projectmanagement.multitenantprojectmanagement.core.project.dto.request.CreateProjectRequest;
import com.projectmanagement.multitenantprojectmanagement.core.project.dto.request.UpdateProjectRequest;
import com.projectmanagement.multitenantprojectmanagement.core.project.dto.response.ProjectDetailsResponse;
import com.projectmanagement.multitenantprojectmanagement.core.project.dto.response.ProjectsResponse;
import com.projectmanagement.multitenantprojectmanagement.organizations.dto.response.PaginatedResponseDto;
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

@WebMvcTest(Projectcontroller.class)
@AutoConfigureMockMvc(addFilters = false)
public class ProjectControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProjectService projectService;

    @Autowired
    private ObjectMapper objectMapper;

    @Mock
    private Jwt jwt;

    private UUID projectId;
    private UUID orgId;

    private PaginatedResponseDto<ProjectsResponse> paginatedResponseDto;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        projectId = UUID.randomUUID();
        orgId = UUID.randomUUID();
        

        paginatedResponseDto = PaginatedResponseDto.<ProjectsResponse>builder()
                                .data(List.of())
                                .totalElements(10)
                                .totalPages(1)
                                .page(1)
                                .build();

    }

    @Test
    void testGetProjectById_Success() throws Exception {
        ProjectDetailsResponse response = new ProjectDetailsResponse();
        response.setId(projectId);

        when(projectService.getProjectByIdForController(projectId)).thenReturn(response);

        mockMvc.perform(get("/api/v1/project/{id}", projectId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(projectId.toString()));

        verify(projectService, times(1)).getProjectByIdForController(projectId);
    }

    @Test
    void testGetAllProjectsByOrgId_Success() throws Exception {

        when(projectService.getAllProjectsByOrganizationId(eq(orgId), any())).thenReturn(paginatedResponseDto);

        mockMvc.perform(get("/api/v1/project/{id}/organization", orgId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray());

        verify(projectService, times(1)).getAllProjectsByOrganizationId(eq(orgId), any());
    }

    @Test
    void testCreateProject_Success() throws Exception {
        CreateProjectRequest request = new CreateProjectRequest();
        request.setKey("ED");
        request.setName("EDD");
        ProjectDetailsResponse response = new ProjectDetailsResponse();
        response.setId(projectId);

        when(projectService.createProject(any(CreateProjectRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/v1/project")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(projectId.toString()));

        verify(projectService, times(1)).createProject(any(CreateProjectRequest.class));
    }

    @Test
    void testUpdateProject_Success() throws Exception {
        UpdateProjectRequest request = new UpdateProjectRequest();
        ProjectDetailsResponse response = new ProjectDetailsResponse();
        response.setId(projectId);

        when(projectService.updateProject(any(UpdateProjectRequest.class))).thenReturn(response);

        mockMvc.perform(put("/api/v1/project")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(projectId.toString()));

        verify(projectService, times(1)).updateProject(any(UpdateProjectRequest.class));
    }

    @Test
    void testDeleteProjectById_Success() throws Exception {
        ProjectDetailsResponse response = new ProjectDetailsResponse();
        response.setId(projectId);

        when(projectService.deleteProjectById(projectId)).thenReturn(response);

        mockMvc.perform(delete("/api/v1/project/{id}", projectId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(projectId.toString()));

        verify(projectService, times(1)).deleteProjectById(projectId);
    }
}
