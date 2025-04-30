package com.projectmanagement.multitenantprojectmanagement.service;

import com.projectmanagement.multitenantprojectmanagement.auth0.utils.JWTUtils;
import com.projectmanagement.multitenantprojectmanagement.core.issue.Issue;
import com.projectmanagement.multitenantprojectmanagement.core.issue.IssueService;
import com.projectmanagement.multitenantprojectmanagement.core.worklog.*;
import com.projectmanagement.multitenantprojectmanagement.core.worklog.dto.request.CreateWorklogRequest;
import com.projectmanagement.multitenantprojectmanagement.core.worklog.dto.request.UpdateWorklogRequest;
import com.projectmanagement.multitenantprojectmanagement.core.worklog.dto.response.WorklogResponse;
import com.projectmanagement.multitenantprojectmanagement.core.worklog.mapper.WorklogMapper;
import com.projectmanagement.multitenantprojectmanagement.exception.NotFoundException;
import com.projectmanagement.multitenantprojectmanagement.helper.MaskingString;
import com.projectmanagement.multitenantprojectmanagement.organizations.Organizations;
import com.projectmanagement.multitenantprojectmanagement.organizations.OrganizationsService;
import com.projectmanagement.multitenantprojectmanagement.users.UserService;
import com.projectmanagement.multitenantprojectmanagement.users.Users;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class WorklogTest {

    @Mock
    private WorkLogRepository workLogRepository;

    @Mock
    private IssueService issueService;

    @Mock
    private UserService userService;

    @Mock
    private MaskingString maskingString;

    @Mock
    private OrganizationsService organizationsService;

    @Mock
    private JWTUtils jwtUtils;

    @InjectMocks
    private WorkLogService workLogService;

    private UUID worklogId;
    private WorkLog mockWorklog;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        worklogId = UUID.fromString("c6e447db-1920-41a8-b728-bf831bf5f096");
        mockWorklog = new WorkLog();
        mockWorklog.setId(worklogId);
        mockWorklog.setTimeSpentInMinutes(100);
        mockWorklog.setComment("something");
        
        Issue issue = new Issue();
        issue.setId(UUID.randomUUID());
        issue.setWorkLog(mockWorklog);
        mockWorklog.setIssue(issue);
        Users user = new Users();
        user.setId(UUID.randomUUID());
        mockWorklog.setUser(user);
        Organizations organization = new Organizations();
        organization.setId(UUID.randomUUID());
        mockWorklog.setOrganization(organization);
        
        // Mocking the dependencies
        when(maskingString.maskSensitive(anyString())).thenReturn("masked");
        when(jwtUtils.getAuth0OrgId()).thenReturn("orgId");
    }

    @Test
    void testGetWorklogById_Success() {
        String auth0OrgId = "auth0|12345";

        when(jwtUtils.getAuth0OrgId()).thenReturn(auth0OrgId);
        when(workLogRepository.findByIdAndOrganization_Auth0Id(worklogId, auth0OrgId)).thenReturn(Optional.of(mockWorklog));
        when(maskingString.maskSensitive(anyString())).thenReturn("masked");

        WorkLog result = workLogService.getWorklogById(worklogId);

        assertNotNull(result);
        assertEquals(worklogId, result.getId());
        verify(workLogRepository, times(1)).findByIdAndOrganization_Auth0Id(worklogId, auth0OrgId);
    }

    @Test
    void testGetWorklogById_NotFound() {
        String auth0OrgId = "auth0|12345";

        when(jwtUtils.getAuth0OrgId()).thenReturn(auth0OrgId);
        when(workLogRepository.findByIdAndOrganization_Auth0Id(worklogId, auth0OrgId)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            workLogService.getWorklogById(worklogId);
        });

        assertEquals("Worklog not found for the given ID: " + worklogId, exception.getMessage());
        verify(workLogRepository, times(1)).findByIdAndOrganization_Auth0Id(worklogId, auth0OrgId);
    }

    @Test
    void testCreateWorklog_Success() {
        CreateWorklogRequest request = new CreateWorklogRequest();
        request.setIssueId(UUID.randomUUID());
        request.setUserId(UUID.randomUUID());

        Issue issue = new Issue();
        Users user = new Users();
        Organizations organization = new Organizations();

        when(issueService.getIssueById(request.getIssueId())).thenReturn(issue);
        when(userService.getUserEntity(request.getUserId())).thenReturn(user);
        when(jwtUtils.getAuth0OrgId()).thenReturn("auth0|12345");
        when(organizationsService.getOrganizationByAuth0Id("auth0|12345")).thenReturn(organization);
        when(workLogRepository.save(any(WorkLog.class))).thenReturn(mockWorklog);

        WorklogResponse result = workLogService.createWorklog(request);

        assertNotNull(result);
        verify(workLogRepository, times(1)).save(any(WorkLog.class));
    }

    @Test
    void testUpdateWorklog_Success() {
        UpdateWorklogRequest request = new UpdateWorklogRequest();
        request.setId(worklogId);
        request.setTimeSpentInMinutes(10);

        when(workLogRepository.findByIdAndOrganization_Auth0Id(eq(worklogId), anyString())).thenReturn(Optional.of(mockWorklog));
        when(workLogRepository.save(any(WorkLog.class))).thenReturn(mockWorklog);

        WorklogResponse result = workLogService.updateWorklog(request);

        assertNotNull(result);
        verify(workLogRepository, times(1)).save(any(WorkLog.class));
    }

    @Test
    void testDeleteWorklogById_Success() {

        when(workLogRepository.findByIdAndOrganization_Auth0Id(eq(worklogId), anyString())).thenReturn(Optional.of(mockWorklog));

        WorklogResponse result = workLogService.deleteWorklogById(worklogId);

        assertNotNull(result);
        verify(workLogRepository, times(1)).delete(mockWorklog);
    }

    @Test
    void testDeleteWorklogById_NotFound() {
        when(workLogRepository.findByIdAndOrganization_Auth0Id(eq(worklogId), anyString())).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            workLogService.deleteWorklogById(worklogId);
        });

        assertEquals("Worklog not found for the given ID: " + worklogId, exception.getMessage());
    }
}
