package com.projectmanagement.multitenantprojectmanagement.service;

import com.projectmanagement.multitenantprojectmanagement.auth0.utils.JWTUtils;
import com.projectmanagement.multitenantprojectmanagement.core.issue.Issue;
import com.projectmanagement.multitenantprojectmanagement.core.issue.IssueService;
import com.projectmanagement.multitenantprojectmanagement.core.issue.event.IssueEvent;
import com.projectmanagement.multitenantprojectmanagement.core.watcher.*;
import com.projectmanagement.multitenantprojectmanagement.core.watcher.dto.response.WatcherResponse;
import com.projectmanagement.multitenantprojectmanagement.core.watcher.mapper.WatcherMapper;
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

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class WatcherTest {

    @Mock
    private WatcherRepository watcherRepository;

    @Mock
    private MaskingString maskingString;

    @Mock
    private IssueService issueService;

    @Mock
    private UserService userService;

    @Mock
    private OrganizationsService organizationsService;

    @Mock
    private JWTUtils jwtUtils;

    @InjectMocks
    private WatcherService watcherService;

    private UUID watcherId;
    private Watcher mockWatcher;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        watcherId = UUID.randomUUID();
        mockWatcher = new Watcher();
        mockWatcher.setId(watcherId);

        Users user = new Users();
        user.setId(UUID.randomUUID());
        user.setName("Tester User");
        user.setEmail("test@gmail.com");

        mockWatcher.setUser(user);

        Issue issue = new Issue();
        issue.setId(UUID.randomUUID());
        
        mockWatcher.setIssue(issue);
        Organizations organization = new Organizations();
        organization.setId(UUID.randomUUID());
        organization.setAuth0Id("auth0|12345");

        mockWatcher.setOrganization(organization);
    



    }

    @Test
    void testGetWatcherById_Success() {
        String auth0OrgId = "auth0|12345";

        when(jwtUtils.getAuth0OrgId()).thenReturn(auth0OrgId);
        when(watcherRepository.findByIdAndOrganization_Auth0Id(watcherId, auth0OrgId)).thenReturn(Optional.of(mockWatcher));
        when(maskingString.maskSensitive(anyString())).thenReturn("masked");

        Watcher result = watcherService.getWatcherById(watcherId);

        assertNotNull(result);
        assertEquals(watcherId, result.getId());
        verify(watcherRepository, times(1)).findByIdAndOrganization_Auth0Id(watcherId, auth0OrgId);
    }

    @Test
    void testGetWatcherById_NotFound() {
        String auth0OrgId = "auth0|12345";

        when(jwtUtils.getAuth0OrgId()).thenReturn(auth0OrgId);
        when(watcherRepository.findByIdAndOrganization_Auth0Id(watcherId, auth0OrgId)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            watcherService.getWatcherById(watcherId);
        });

        assertEquals("Watcher not found for the given ID: " + watcherId, exception.getMessage());
        verify(watcherRepository, times(1)).findByIdAndOrganization_Auth0Id(watcherId, auth0OrgId);
    }

    @Test
    void testGetWatchersByIssueId_Success() {
        UUID issueId = UUID.randomUUID();
        String auth0OrgId = "auth0|12345";

        when(jwtUtils.getAuth0OrgId()).thenReturn(auth0OrgId);
        when(watcherRepository.findAllByIssueIdAndOrganization_Auth0Id(issueId, auth0OrgId))
                .thenReturn(Collections.singletonList(mockWatcher));

        List<WatcherResponse> result = watcherService.getWatchersByIssueId(issueId);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(watcherRepository, times(1)).findAllByIssueIdAndOrganization_Auth0Id(issueId, auth0OrgId);
    }

    @Test
    void testCreateWatcher_Success() {
        UUID issueId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        Issue issue = new Issue();
        Users user = new Users();
        Organizations organization = new Organizations();

        when(issueService.getIssueById(issueId)).thenReturn(issue);
        when(userService.getUserEntity(userId)).thenReturn(user);
        when(jwtUtils.getAuth0OrgId()).thenReturn("auth0|12345");
        when(organizationsService.getOrganizationByAuth0Id("auth0|12345")).thenReturn(organization);
        when(watcherRepository.save(any(Watcher.class))).thenReturn(mockWatcher);

        WatcherResponse result = watcherService.createWatcher(issueId, userId);

        assertNotNull(result);
        verify(watcherRepository, times(1)).save(any(Watcher.class));
    }

    @Test
    void testRemoveWatcher_Success() {
        String auth0OrgId = "auth0|12345";

        when(jwtUtils.getAuth0OrgId()).thenReturn(auth0OrgId);
        when(watcherRepository.findByIdAndOrganization_Auth0Id(watcherId, auth0OrgId)).thenReturn(Optional.of(mockWatcher));

        WatcherResponse result = watcherService.removeWatcher(watcherId);

        assertNotNull(result);
        verify(watcherRepository, times(1)).delete(mockWatcher);
    }

    @Test
    void testRemoveWatcher_NotFound() {
        String auth0OrgId = "auth0|12345";

        when(jwtUtils.getAuth0OrgId()).thenReturn(auth0OrgId);
        when(watcherRepository.findByIdAndOrganization_Auth0Id(watcherId, auth0OrgId)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            watcherService.removeWatcher(watcherId);
        });

        assertEquals("Watcher not found for the given ID: " + watcherId, exception.getMessage());
        verify(watcherRepository, times(1)).findByIdAndOrganization_Auth0Id(watcherId, auth0OrgId);
    }

    @Test
    void testCreateWatcherViaEvent_Success() {
        UUID issueId = UUID.randomUUID();
        UUID reporterId = UUID.randomUUID();
        IssueEvent event = new IssueEvent(this, issueId, reporterId);

        when(issueService.getIssueById(issueId)).thenReturn(new Issue());
        when(userService.getUserEntity(reporterId)).thenReturn(new Users());
        when(jwtUtils.getAuth0OrgId()).thenReturn("auth0|12345");
        when(organizationsService.getOrganizationByAuth0Id("auth0|12345")).thenReturn(new Organizations());
        when(watcherRepository.save(any(Watcher.class))).thenReturn(mockWatcher);

        watcherService.createWatcherViaEvent(event);

        verify(watcherRepository, times(1)).save(any(Watcher.class));
    }
}
