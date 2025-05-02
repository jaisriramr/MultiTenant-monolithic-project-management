package com.projectmanagement.multitenantprojectmanagement.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.projectmanagement.multitenantprojectmanagement.auth0.utils.JWTUtils;
import com.projectmanagement.multitenantprojectmanagement.core.activity.Activity;
import com.projectmanagement.multitenantprojectmanagement.core.activity.ActivityController;
import com.projectmanagement.multitenantprojectmanagement.core.activity.ActivityService;
import com.projectmanagement.multitenantprojectmanagement.core.activity.dto.request.CreateActivityRequest;
import com.projectmanagement.multitenantprojectmanagement.core.activity.dto.response.ActivityResponse;
import com.projectmanagement.multitenantprojectmanagement.core.activity.enums.EntityType;
import com.projectmanagement.multitenantprojectmanagement.organizations.dto.response.PaginatedResponseDto;
import com.projectmanagement.multitenantprojectmanagement.users.Users;
import com.projectmanagement.multitenantprojectmanagement.users.dto.mapper.UserMapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ActivityController.class)
@AutoConfigureMockMvc(addFilters = false)
public class ActivityControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ActivityService activityService;

    @MockBean
    private JWTUtils jwtUtils;

    @Autowired
    private ObjectMapper objectMapper;

    private UUID activityId;
    private UUID entityId;
    private UUID projectId;
    private Activity mockActivity;
    private ActivityResponse activityResponse;
    private PaginatedResponseDto<ActivityResponse> paginatedResponseDto;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        activityId = UUID.randomUUID();
        entityId = UUID.randomUUID();
        projectId = UUID.randomUUID();

        Users user = new Users();
        user.setId(activityId);

        mockActivity = new Activity();
        mockActivity.setId(activityId);
        mockActivity.setEntityId(entityId);
        mockActivity.setPerformedBy(user);
        mockActivity.setEntityType(EntityType.ATTACHMENT);

        activityResponse = new ActivityResponse();
        activityResponse.setId(activityId);
        activityResponse.setAction("DONE");
        activityResponse.setPerformedBy(UserMapper.toUserSingleListResponse(user));
        


        activityResponse.setPerformedBy(null);
    
        paginatedResponseDto = PaginatedResponseDto.<ActivityResponse>builder()
                                .data(Collections.singletonList(activityResponse))
                                .page(0)
                                .size(0)
                                .totalElements(1)
                                .totalPages(1)
                                .build();

    }

    @Test
    void testGetActivityById_Success() throws Exception {

        when(activityService.getActivityById(activityId)).thenReturn(mockActivity);
        when(jwtUtils.getAuth0OrgId()).thenReturn("auth0|org123");

        mockMvc.perform(get("/api/v1/activity/{id}", activityId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(activityId.toString()));

        verify(activityService, times(1)).getActivityById(activityId);
    }

    @Test
    void testGetActivityByEntityId_Success() throws Exception {

        when(activityService.getActivitiesByEntityId(eq(entityId), any())).thenReturn(paginatedResponseDto);

        mockMvc.perform(get("/api/v1/activity/{id}/entity", entityId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray());

        verify(activityService, times(1)).getActivitiesByEntityId(eq(entityId), any());
    }

    @Test
    void testGetActivityByProjectId_Success() throws Exception {

        when(jwtUtils.getAuth0OrgId()).thenReturn("auth0|org123");
        when(activityService.getActivitiesByProjectIdAndOrgId(eq(projectId), eq("auth0|org123"), any())).thenReturn(paginatedResponseDto);

        mockMvc.perform(get("/api/v1/activity/{id}/project", projectId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray());

        verify(activityService, times(1)).getActivitiesByProjectIdAndOrgId(eq(projectId), eq("auth0|org123"), any());
    }

    @Test
    void testCreateActivity_Success() throws Exception {
        CreateActivityRequest request = new CreateActivityRequest();

        when(activityService.createActivity(any(CreateActivityRequest.class))).thenReturn(activityResponse);

        mockMvc.perform(post("/api/v1/activity")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(activityId.toString()));

        verify(activityService, times(1)).createActivity(any(CreateActivityRequest.class));
    }

    @Test
    void testDeleteActivityById_Success() throws Exception {
        
        when(activityService.deleteActivityById(activityId)).thenReturn(activityResponse);

        mockMvc.perform(delete("/api/v1/activity/{id}", activityId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(activityId.toString()));

        verify(activityService, times(1)).deleteActivityById(activityId);
    }
}
