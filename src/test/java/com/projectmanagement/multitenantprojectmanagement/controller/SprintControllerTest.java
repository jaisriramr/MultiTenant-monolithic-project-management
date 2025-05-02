package com.projectmanagement.multitenantprojectmanagement.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.projectmanagement.multitenantprojectmanagement.auth0.utils.JWTUtils;
import com.projectmanagement.multitenantprojectmanagement.core.project.Projects;
import com.projectmanagement.multitenantprojectmanagement.core.project.dto.response.ProjectsResponse;
import com.projectmanagement.multitenantprojectmanagement.core.project.mapper.ProjectMapper;
import com.projectmanagement.multitenantprojectmanagement.core.sprint.Sprint;
import com.projectmanagement.multitenantprojectmanagement.core.sprint.SprintController;
import com.projectmanagement.multitenantprojectmanagement.core.sprint.SprintService;
import com.projectmanagement.multitenantprojectmanagement.core.sprint.dto.request.CreateSprintRequest;
import com.projectmanagement.multitenantprojectmanagement.core.sprint.dto.request.UpdateSprintRequest;
import com.projectmanagement.multitenantprojectmanagement.core.sprint.dto.response.ListSprintResponse;
import com.projectmanagement.multitenantprojectmanagement.core.sprint.dto.response.MinimalSprintResponse;
import com.projectmanagement.multitenantprojectmanagement.core.sprint.dto.response.SprintDetailedResponse;
import com.projectmanagement.multitenantprojectmanagement.core.sprint.enums.SprintStatus;
import com.projectmanagement.multitenantprojectmanagement.organizations.Organizations;
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

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(SprintController.class)
@AutoConfigureMockMvc(addFilters = false)
public class SprintControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SprintService sprintService;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private JWTUtils jwtUtils;

    @Mock
    private Jwt jwt;

    private UUID sprintId;
    private UUID projectId;

    private Sprint sprint;
    private SprintDetailedResponse sprintDetailedResponse;
    private ListSprintResponse listSprintResponse;
    private PaginatedResponseDto<ListSprintResponse> paginatedResponseDto;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        sprintId = UUID.randomUUID();
        projectId = UUID.randomUUID();

        sprint = new Sprint();
        sprint.setId(sprintId);
        sprint.setName("Sprint 1");
        sprint.setCreatedAt(Instant.now());
        sprint.setUpdatedAt(Instant.now());

        Projects project = new Projects();
        project.setId(projectId);
        project.setName("Project 1");
        project.setCreatedAt(Instant.now());
        project.setUpdatedAt(Instant.now());

        sprint.setProject(project);
        sprint.setStatus(SprintStatus.ACTIVE);

        Organizations organization = new Organizations();
        organization.setId(UUID.randomUUID());

        sprint.setOrganization(organization);
        
        sprintDetailedResponse = new SprintDetailedResponse();
        sprintDetailedResponse.setId(sprintId);
        sprintDetailedResponse.setName("Sprint 1");
        sprintDetailedResponse.setProject(ProjectsResponse.builder().id(projectId).build());

        listSprintResponse = new ListSprintResponse();
        listSprintResponse.setId(sprintId);
        listSprintResponse.setName("Sprint 1");

        paginatedResponseDto = PaginatedResponseDto.<ListSprintResponse>builder()
                                .data(List.of(listSprintResponse))
                                .totalElements(10)
                                .page(1)
                                .build();

    }

    @Test
    void testGetSprintById_Success() throws Exception {

        when(sprintService.getSprintEntity(sprintId)).thenReturn(sprint);

        mockMvc.perform(get("/api/v1/sprint/{id}", sprintId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(sprintId.toString()));

        verify(sprintService, times(1)).getSprintEntity(sprintId);
    }

    @Test
    void testGetAllSprintByProjectId_Success() throws Exception {

        when(sprintService.getAllSprintByProjectId(eq(projectId), any())).thenReturn(paginatedResponseDto);

        mockMvc.perform(get("/api/v1/sprint/{id}/project", projectId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray());

        verify(sprintService, times(1)).getAllSprintByProjectId(eq(projectId), any());
    }

    @Test
    void testCreateSprint_Success() throws Exception {
        CreateSprintRequest request = new CreateSprintRequest();
        SprintDetailedResponse response = new SprintDetailedResponse();
        response.setId(sprintId);

        when(sprintService.createSprint(any(CreateSprintRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/v1/sprint")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(sprintId.toString()));

        verify(sprintService, times(1)).createSprint(any(CreateSprintRequest.class));
    }

    @Test
    void testUpdateSprint_Success() throws Exception {
        UpdateSprintRequest request = new UpdateSprintRequest();
        SprintDetailedResponse response = new SprintDetailedResponse();
        response.setId(sprintId);

        when(sprintService.updateSprint(any(UpdateSprintRequest.class))).thenReturn(response);

        mockMvc.perform(put("/api/v1/sprint")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(sprintId.toString()));

        verify(sprintService, times(1)).updateSprint(any(UpdateSprintRequest.class));
    }

    @Test
    void testDeleteSprintById_Success() throws Exception {
        MinimalSprintResponse response = new MinimalSprintResponse();
        response.setId(sprintId);

        when(sprintService.deleteSprint(sprintId)).thenReturn(response);

        mockMvc.perform(delete("/api/v1/sprint/{id}", sprintId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(sprintId.toString()));

        verify(sprintService, times(1)).deleteSprint(sprintId);
    }
}
