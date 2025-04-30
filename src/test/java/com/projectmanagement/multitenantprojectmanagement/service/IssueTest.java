package com.projectmanagement.multitenantprojectmanagement.service;

import com.projectmanagement.multitenantprojectmanagement.core.issue.*;
import com.projectmanagement.multitenantprojectmanagement.core.issue.dto.request.CreateIssueRequest;
import com.projectmanagement.multitenantprojectmanagement.core.issue.dto.request.UpdateIssueRequest;
import com.projectmanagement.multitenantprojectmanagement.core.issue.dto.response.IssueResponse;
import com.projectmanagement.multitenantprojectmanagement.core.issue.dto.response.ListIssuesResponse;
import com.projectmanagement.multitenantprojectmanagement.core.issue.enums.IssuePriority;
import com.projectmanagement.multitenantprojectmanagement.core.issue.enums.IssueStatus;
import com.projectmanagement.multitenantprojectmanagement.core.issue.enums.IssueType;
import com.projectmanagement.multitenantprojectmanagement.core.issue.mapper.IssueMapper;
import com.projectmanagement.multitenantprojectmanagement.core.issuerelation.IssueRelationService;
import com.projectmanagement.multitenantprojectmanagement.core.issuerelation.enums.IssueRelationType;
import com.projectmanagement.multitenantprojectmanagement.core.project.ProjectService;
import com.projectmanagement.multitenantprojectmanagement.core.project.Projects;
import com.projectmanagement.multitenantprojectmanagement.core.sprint.Sprint;
import com.projectmanagement.multitenantprojectmanagement.core.sprint.SprintService;
import com.projectmanagement.multitenantprojectmanagement.exception.NotFoundException;
import com.projectmanagement.multitenantprojectmanagement.helper.MaskingString;
import com.projectmanagement.multitenantprojectmanagement.organizations.Organizations;
import com.projectmanagement.multitenantprojectmanagement.organizations.OrganizationsService;
import com.projectmanagement.multitenantprojectmanagement.users.UserService;
import com.projectmanagement.multitenantprojectmanagement.users.Users;
import com.projectmanagement.multitenantprojectmanagement.auth0.utils.JWTUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class IssueTest {

    @Mock
    private IssueRepository issueRepository;

    @Mock
    private MaskingString maskingString;

    @Mock
    private ProjectService projectService;

    @Mock
    private SprintService sprintService;

    @Mock
    private UserService userService;

    @Mock
    private IssueRelationService issueRelationService;

    @Mock
    private JWTUtils jwtUtils;

    @Mock
    private OrganizationsService organizationsService;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private IssueService issueService;

    private UUID issueId;
    private Issue mockIssue;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        issueId = UUID.fromString("c6e447db-1920-41a8-b728-bf831bf5f096");
        mockIssue = new Issue();
        mockIssue.setId(issueId);
        mockIssue.setType(IssueType.BUG);
        mockIssue.setStatus(IssueStatus.TO_DO);
        mockIssue.setPriority(IssuePriority.LOW);
        mockIssue.setStoryPoints(5);
        mockIssue.setKey("TS-123");
        mockIssue.setTitle("Test Issue");
        mockIssue.setDescription("This is a test issue.");

        Projects project = new Projects();
        project.setId(UUID.randomUUID());
        project.setKey("PROJ");
        project.setIssueCount(10L);
        project.setOrganization(new Organizations());
        mockIssue.setProject(project);

        when(jwtUtils.getAuth0OrgId()).thenReturn("orgId");
        when(maskingString.maskSensitive(anyString())).thenReturn("masked-string");

    }

    @Test
    void testGetIssueById_Success() {
        String auth0OrgId = "auth0|12345";

        when(jwtUtils.getAuth0OrgId()).thenReturn(auth0OrgId);
        when(issueRepository.findByIdAndOrganization_Auth0Id(issueId, auth0OrgId)).thenReturn(Optional.of(mockIssue));
        when(maskingString.maskSensitive(anyString())).thenReturn("masked");

        Issue result = issueService.getIssueById(issueId);

        assertNotNull(result);
        assertEquals(issueId, result.getId());
        assertEquals("Test Issue", result.getTitle());
        verify(issueRepository, times(1)).findByIdAndOrganization_Auth0Id(issueId, auth0OrgId);
    }

    @Test
    void testGetIssueById_NotFound() {
        String auth0OrgId = "auth0|12345";

        when(jwtUtils.getAuth0OrgId()).thenReturn(auth0OrgId);
        when(issueRepository.findByIdAndOrganization_Auth0Id(issueId, auth0OrgId)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            issueService.getIssueById(issueId);
        });

        assertEquals("Issue not found for the given ID: " + issueId, exception.getMessage());
        verify(issueRepository, times(1)).findByIdAndOrganization_Auth0Id(issueId, auth0OrgId);
    }

    @Test
    void testGetIssueByKey_Success() {

        when(issueRepository.findByKeyAndOrganization_Auth0Id(anyString(), anyString())).thenReturn(Optional.of(mockIssue));

        IssueResponse result = issueService.getIssueByKey("TS-123");

        assertNotNull(result);
        assertEquals("Test Issue", result.getTitle());
    }

    @Test
    void testGetIssueByKey_NotFound() {
        String key = "ISSUE-123";
        String auth0OrgId = "auth0|12345";

        when(jwtUtils.getAuth0OrgId()).thenReturn(auth0OrgId);
        when(issueRepository.findByKeyAndOrganization_Auth0Id(key, auth0OrgId)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            issueService.getIssueByKey(key);
        });

        assertEquals("Issue not found for the given Key: " + key, exception.getMessage());
        verify(issueRepository, times(1)).findByKeyAndOrganization_Auth0Id(key, auth0OrgId);
    }

    @Test
    void testAssigneeUserToAnIssue_Success() {
        UUID userId = UUID.randomUUID();
        Users user = new Users();
        user.setId(userId);

        when(issueRepository.findByIdAndOrganization_Auth0Id(eq(issueId), anyString())).thenReturn(Optional.of(mockIssue));
        when(userService.getUserEntity(userId)).thenReturn(user);

        issueService.assigneeUserToAnIssue(issueId, userId);

        assertEquals(user, mockIssue.getAssignee());
        verify(issueRepository, times(1)).save(mockIssue);
    }

    @Test
    void testUnAssigneeUserToAnIssue_Success() {
        mockIssue.setAssignee(new Users());

        when(issueRepository.findByIdAndOrganization_Auth0Id(eq(issueId), anyString())).thenReturn(Optional.of(mockIssue));

        issueService.unAssigneeUserToAnIssue(issueId);

        assertNull(mockIssue.getAssignee());
        verify(issueRepository, times(1)).save(mockIssue);
    }

    @Test
    void testCreateIssue_Success() {
        CreateIssueRequest request = new CreateIssueRequest();
        request.setProjectId(UUID.randomUUID());
        request.setReporterId(UUID.randomUUID());
        request.setTitle("New Issue");
        request.setType(IssueType.BUG.toString());
        


        Projects project = new Projects();
        project.setKey("PROJ");
        project.setIssueCount(10L);

        Users reporter = new Users();
        Organizations organization = new Organizations();

        when(projectService.getProjectById(request.getProjectId())).thenReturn(project);
        when(userService.getUserEntity(request.getReporterId())).thenReturn(reporter);
        when(jwtUtils.getAuth0OrgId()).thenReturn("auth0|12345");
        when(organizationsService.getOrganizationByAuth0Id("auth0|12345")).thenReturn(organization);
        when(issueRepository.save(any(Issue.class))).thenReturn(mockIssue);

        ListIssuesResponse result = issueService.createIssue(request);

        assertNotNull(result);
        assertEquals("Test Issue", result.getTitle());
        verify(issueRepository, times(1)).save(any(Issue.class));
    }

    @Test
    void testUpdateIssue_Success() {
        UpdateIssueRequest request = new UpdateIssueRequest();
        request.setId(issueId);
        request.setTitle("Updated Title");
        request.setDescription("Updated Description");
        request.setPriority("HIGH");

        when(issueRepository.findById(issueId)).thenReturn(Optional.of(mockIssue));
        when(issueRepository.saveAndFlush(mockIssue)).thenReturn(mockIssue);

        IssueResponse result = issueService.updateIssue(request);

        assertNotNull(result);
        assertEquals("Updated Title", result.getTitle());
        assertEquals("Updated Description", result.getDescription());
        assertEquals(IssuePriority.HIGH, mockIssue.getPriority());
        verify(issueRepository, times(1)).saveAndFlush(mockIssue);
    }
}
