package com.projectmanagement.multitenantprojectmanagement.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.projectmanagement.multitenantprojectmanagement.auth0.utils.JWTUtils;
import com.projectmanagement.multitenantprojectmanagement.core.issue.Issue;
import com.projectmanagement.multitenantprojectmanagement.core.issue.IssueController;
import com.projectmanagement.multitenantprojectmanagement.core.issue.IssueService;
import com.projectmanagement.multitenantprojectmanagement.core.issue.dto.request.CreateEpicIssueRequest;
import com.projectmanagement.multitenantprojectmanagement.core.issue.dto.request.CreateIssueRequest;
import com.projectmanagement.multitenantprojectmanagement.core.issue.dto.request.CreateSubIssueRequest;
import com.projectmanagement.multitenantprojectmanagement.core.issue.dto.request.UpdateIssueRequest;
import com.projectmanagement.multitenantprojectmanagement.core.issue.dto.response.IssueResponse;
import com.projectmanagement.multitenantprojectmanagement.core.issue.dto.response.ListIssuesResponse;
import com.projectmanagement.multitenantprojectmanagement.core.issue.enums.IssuePriority;
import com.projectmanagement.multitenantprojectmanagement.core.issue.enums.IssueStatus;
import com.projectmanagement.multitenantprojectmanagement.core.issue.enums.IssueType;
import com.projectmanagement.multitenantprojectmanagement.core.issue.mapper.IssueMapper;
import com.projectmanagement.multitenantprojectmanagement.core.project.Projects;
import com.projectmanagement.multitenantprojectmanagement.organizations.Organizations;
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

import java.time.Instant;
import java.util.Collections;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(IssueController.class)
@AutoConfigureMockMvc(addFilters = false)
public class IssueControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IssueService issueService;

    @MockBean
    private JWTUtils jwtUtils;

    @Autowired
    private ObjectMapper objectMapper;

    private UUID issueId;
    private UUID epicId;
    private UUID sprintId;

    private IssueResponse issueResponse;
    private PaginatedResponseDto<IssueResponse> paginatedResponseDto;
    private PaginatedResponseDto<ListIssuesResponse> paginatedResponseDto2;
    private ListIssuesResponse listIssuesResponse;
    private Issue issue;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        issueId = UUID.randomUUID();
        epicId = UUID.randomUUID();
        sprintId = UUID.randomUUID();

        Users user = new Users();
        user.setId(UUID.randomUUID());

        Projects project = new Projects();
        project.setId(issueId);
        project.setCreatedAt(Instant.now());
        project.setUpdatedAt(Instant.now());

        issue = new Issue();
        issue.setId(issueId);
        issue.setStatus(IssueStatus.DONE);
        issue.setPriority(IssuePriority.CRITICAL);
        issue.setType(IssueType.BUG);
        issue.setProject(project);
        issue.setAssignee(user);
        
        Organizations organization = new Organizations();
        organization.setId(epicId);
        
        issue.setOrganization(organization);

        issueResponse = new IssueResponse();
        issueResponse.setId(issueId);
        issueResponse.setEpicId(epicId);
        issueResponse.setSprintId(sprintId);

        listIssuesResponse = new ListIssuesResponse();
        listIssuesResponse.setId(issueId);
        listIssuesResponse.setAssignee(IssueMapper.toListIssuesUserDto(user));
        listIssuesResponse.setKey("key");
        listIssuesResponse.setStatus("DONE");


        paginatedResponseDto = PaginatedResponseDto.<IssueResponse>builder()
                                .data(Collections.singletonList(issueResponse))
                                .page(0)
                                .size(1)
                                .totalElements(1)
                                .totalPages(1)
                                .build();

                                paginatedResponseDto2 = PaginatedResponseDto.<ListIssuesResponse>builder()
                                .data(Collections.singletonList(listIssuesResponse))
                                .page(0)
                                .size(1)
                                .totalElements(1)
                                .totalPages(1)
                                .build();

    }

    @Test
    void testGetIssueById_Success() throws Exception {

        when(issueService.getIssueById(issueId)).thenReturn(issue);

        mockMvc.perform(get("/api/v1/issue/{id}", issueId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(issueId.toString()));

        verify(issueService, times(1)).getIssueById(issueId);
    }

    @Test
    void testGetIssueByKey_Success() throws Exception {

        when(issueService.getIssueByKey("ISSUE-123")).thenReturn(issueResponse);

        mockMvc.perform(get("/api/v1/issue")
                .param("key", "ISSUE-123")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(issueId.toString()));

        verify(issueService, times(1)).getIssueByKey("ISSUE-123");
    }

    @Test
    void testGetProjectBacklogs_Success() throws Exception {

        when(issueService.getBacklogIssues(eq(issueId), any())).thenReturn(paginatedResponseDto2);

        mockMvc.perform(get("/api/v1/issue/{id}/backlog", issueId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray());

        verify(issueService, times(1)).getBacklogIssues(eq(issueId), any());
    }

    @Test
    void testCreateIssue_Success() throws Exception {
        CreateIssueRequest request = new CreateIssueRequest();

        when(issueService.createIssue(any(CreateIssueRequest.class))).thenReturn(listIssuesResponse);

        mockMvc.perform(post("/api/v1/issue")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        verify(issueService, times(1)).createIssue(any(CreateIssueRequest.class));
    }

    @Test
    void testCreateSubIssue_Success() throws Exception {
        CreateSubIssueRequest request = new CreateSubIssueRequest();

        when(issueService.createSubIssue(any(CreateSubIssueRequest.class))).thenReturn(listIssuesResponse);

        mockMvc.perform(post("/api/v1/issue/sub-task")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        verify(issueService, times(1)).createSubIssue(any(CreateSubIssueRequest.class));
    }

    @Test
    void testCreateEpicIssue_Success() throws Exception {
        CreateEpicIssueRequest request = new CreateEpicIssueRequest();

        when(issueService.createEpicIssue(any(CreateEpicIssueRequest.class))).thenReturn(listIssuesResponse);

        mockMvc.perform(post("/api/v1/issue/epic")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        verify(issueService, times(1)).createEpicIssue(any(CreateEpicIssueRequest.class));
    }

    @Test
    void testUpdateIssue_Success() throws Exception {
        UpdateIssueRequest request = new UpdateIssueRequest();
        IssueResponse response = new IssueResponse();

        when(issueService.updateIssue(any(UpdateIssueRequest.class))).thenReturn(response);

        mockMvc.perform(put("/api/v1/issue")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        verify(issueService, times(1)).updateIssue(any(UpdateIssueRequest.class));
    }

    @Test
    void testAssigneeUserToAnIssue_Success() throws Exception {
        UUID userId = UUID.randomUUID();

        mockMvc.perform(put("/api/v1/issue/{id}/assign/{userId}/user", issueId, userId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("User successfully assigned to the given User ID: " + issueId));

        verify(issueService, times(1)).assigneeUserToAnIssue(issueId, userId);
    }

    @Test
    void testUnassigneeUserToAnIssue_Success() throws Exception {
        mockMvc.perform(put("/api/v1/issue/{id}/unassign", issueId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("User successfully removed to the given User ID: " + issueId));

        verify(issueService, times(1)).unAssigneeUserToAnIssue(issueId);
    }
}
