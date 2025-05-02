package com.projectmanagement.multitenantprojectmanagement.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.projectmanagement.multitenantprojectmanagement.auth0.utils.JWTUtils;
import com.projectmanagement.multitenantprojectmanagement.core.issue.Issue;
import com.projectmanagement.multitenantprojectmanagement.core.worklog.WorkLog;
import com.projectmanagement.multitenantprojectmanagement.core.worklog.WorkLogController;
import com.projectmanagement.multitenantprojectmanagement.core.worklog.WorkLogService;
import com.projectmanagement.multitenantprojectmanagement.core.worklog.dto.request.CreateWorklogRequest;
import com.projectmanagement.multitenantprojectmanagement.core.worklog.dto.request.UpdateWorklogRequest;
import com.projectmanagement.multitenantprojectmanagement.core.worklog.dto.response.WorklogResponse;
import com.projectmanagement.multitenantprojectmanagement.core.worklog.mapper.WorklogMapper;
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

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(WorkLogController.class)
@AutoConfigureMockMvc(addFilters = false)
public class WorklogControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private WorkLogService workLogService;

    @MockBean
    private JWTUtils jwtUtils;

    @Autowired
    private ObjectMapper objectMapper;

    @Mock
    private Jwt jwt;

    private UUID worklogId;
    private WorkLog workLog;
    private WorklogResponse worklogResponse;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        worklogId = UUID.randomUUID();
        workLog = new WorkLog();
        workLog.setId(worklogId);
        workLog.setComment("test comment");
        
        Issue issue = new Issue();
        issue.setId(UUID.randomUUID());

        workLog.setIssue(issue);

        Users user = new Users();
        user.setId(UUID.randomUUID());
        user.setEmail("test@gmail.com");

        workLog.setUser(user);

        worklogResponse = WorklogMapper.toWorklogResponse(workLog);

    }

    @Test
    void testGetWorklogById_Success() throws Exception {

        when(workLogService.getWorklogById(worklogId)).thenReturn(workLog);

        mockMvc.perform(get("/api/v1/worklog/{id}", worklogId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(worklogId.toString()));

        verify(workLogService, times(1)).getWorklogById(worklogId);
    }

    @Test
    void testCreateWorklog_Success() throws Exception {
        CreateWorklogRequest request = new CreateWorklogRequest();
        WorklogResponse response = new WorklogResponse();
        response.setId(worklogId);

        when(workLogService.createWorklog(any(CreateWorklogRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/v1/worklog")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(worklogId.toString()));

        verify(workLogService, times(1)).createWorklog(any(CreateWorklogRequest.class));
    }

    @Test
    void testUpdateWorklog_Success() throws Exception {
        UpdateWorklogRequest request = new UpdateWorklogRequest();
        WorklogResponse response = new WorklogResponse();
        response.setId(worklogId);

        when(workLogService.updateWorklog(any(UpdateWorklogRequest.class))).thenReturn(response);

        mockMvc.perform(put("/api/v1/worklog")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(worklogId.toString()));

        verify(workLogService, times(1)).updateWorklog(any(UpdateWorklogRequest.class));
    }

    @Test
    void testDeleteWorklogById_Success() throws Exception {
        WorklogResponse response = new WorklogResponse();
        response.setId(worklogId);

        when(workLogService.deleteWorklogById(worklogId)).thenReturn(response);

        mockMvc.perform(delete("/api/v1/worklog/{id}", worklogId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(worklogId.toString()));

        verify(workLogService, times(1)).deleteWorklogById(worklogId);
    }
}
