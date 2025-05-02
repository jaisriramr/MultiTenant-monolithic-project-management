package com.projectmanagement.multitenantprojectmanagement.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.projectmanagement.multitenantprojectmanagement.auth0.utils.JWTUtils;
import com.projectmanagement.multitenantprojectmanagement.core.issue.Issue;
import com.projectmanagement.multitenantprojectmanagement.core.watcher.Watcher;
import com.projectmanagement.multitenantprojectmanagement.core.watcher.WatcherController;
import com.projectmanagement.multitenantprojectmanagement.core.watcher.WatcherService;
import com.projectmanagement.multitenantprojectmanagement.core.watcher.dto.request.WatcherRequest;
import com.projectmanagement.multitenantprojectmanagement.core.watcher.dto.response.WatcherResponse;
import com.projectmanagement.multitenantprojectmanagement.organizations.Organizations;
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

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(WatcherController.class)
@AutoConfigureMockMvc(addFilters = false)
public class WatcherControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private WatcherService watcherService;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private JWTUtils jwtUtils;

    @Mock
    private Jwt jwt;

    private UUID watcherId;
    private UUID issueId;
    private UUID userId;

    private WatcherResponse watcherResponse;
    private Watcher watcher;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        watcherId = UUID.fromString("c6e447db-1920-41a8-b728-bf831bf5f096");
        issueId = UUID.randomUUID();
        userId = UUID.randomUUID();

        watcher = new Watcher();
        watcher.setId(watcherId);

        Users user = new Users();
        user.setId(issueId);

        watcher.setUser(user);
        
        Issue issue = new Issue();
        issue.setId(issueId);

        watcher.setIssue(issue);

        Organizations organization = new Organizations();
        organization.setId(issueId);

        watcher.setOrganization(organization);

        watcherResponse = new WatcherResponse();
        watcherResponse.setId(watcherId);
        watcherResponse.setUserId(userId);
        watcherResponse.setUserName("Test USer");

    }

    @Test
    void testGetWatcherById_Success() throws Exception {

        when(watcherService.getWatcherById(watcherId)).thenReturn(watcher);

        mockMvc.perform(get("/api/v1/watcher/{id}", watcherId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(watcherId.toString()));

        verify(watcherService, times(1)).getWatcherById(watcherId);
    }

    @Test
    void testGetWatchersByIssueId_Success() throws Exception {
        List<WatcherResponse> response = Collections.singletonList(watcherResponse);

        when(watcherService.getWatchersByIssueId(issueId)).thenReturn(response);

        mockMvc.perform(get("/api/v1/watcher/{id}/issue", issueId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());

        verify(watcherService, times(1)).getWatchersByIssueId(issueId);
    }

    @Test
    void testCreateWatcher_Success() throws Exception {
        WatcherRequest request = new WatcherRequest();
        request.setIssueId(issueId);
        request.setUserId(userId);

        when(watcherService.createWatcher(issueId, userId)).thenReturn(watcherResponse);

        mockMvc.perform(post("/api/v1/watcher")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(watcherId.toString()));

        verify(watcherService, times(1)).createWatcher(issueId, userId);
    }

    @Test
    void testDeleteWatcherById_Success() throws Exception {

        when(watcherService.removeWatcher(watcherId)).thenReturn(watcherResponse);

        mockMvc.perform(delete("/api/v1/watcher/{id}", watcherId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(watcherId.toString()));

        verify(watcherService, times(1)).removeWatcher(watcherId);
    }
}
