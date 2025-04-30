package com.projectmanagement.multitenantprojectmanagement.service;

import com.projectmanagement.multitenantprojectmanagement.auth0.utils.JWTUtils;
import com.projectmanagement.multitenantprojectmanagement.core.activity.*;
import com.projectmanagement.multitenantprojectmanagement.core.activity.dto.request.CreateActivityRequest;
import com.projectmanagement.multitenantprojectmanagement.core.activity.dto.response.ActivityResponse;
import com.projectmanagement.multitenantprojectmanagement.core.activity.enums.EntityType;
import com.projectmanagement.multitenantprojectmanagement.core.activity.mapper.ActivityMapper;
import com.projectmanagement.multitenantprojectmanagement.core.project.ProjectService;
import com.projectmanagement.multitenantprojectmanagement.core.project.Projects;
import com.projectmanagement.multitenantprojectmanagement.exception.NotFoundException;
import com.projectmanagement.multitenantprojectmanagement.helper.MaskingString;
import com.projectmanagement.multitenantprojectmanagement.organizations.Organizations;
import com.projectmanagement.multitenantprojectmanagement.organizations.OrganizationsService;
import com.projectmanagement.multitenantprojectmanagement.organizations.dto.response.PaginatedResponseDto;
import com.projectmanagement.multitenantprojectmanagement.users.UserService;
import com.projectmanagement.multitenantprojectmanagement.users.Users;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
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

class ActivityTest {

    @Mock
    private ActivityRepository activityRepository;

    @Mock
    private UserService userService;

    @Mock
    private ProjectService projectService;

    @Mock
    private OrganizationsService organizationsService;

    @Mock
    private MaskingString maskingString;

    @Mock
    private JWTUtils jwtUtils;

    @InjectMocks
    private ActivityService activityService;

    private UUID activityId;
    private Activity mockActivity;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        activityId = UUID.fromString("c6e447db-1920-41a8-b728-bf831bf5f096");

        mockActivity = new Activity();
        mockActivity.setId(activityId);
        mockActivity.setAction("Updated");
        mockActivity.setEntityType(EntityType.ISSUE);
        mockActivity.setEntityId(activityId);
        mockActivity.setFieldChanged("status changed");
        mockActivity.setOldValue("none");
        mockActivity.setNewValue("in progress");
        
        mockActivity.setDescription("decription");

        Projects project = new Projects();
        project.setId(activityId);

        Organizations organization = new Organizations();
        organization.setId(activityId);

        Users user = new Users();
        user.setId(activityId);

        mockActivity.setPerformedBy(user);
        mockActivity.setProject(project);
        mockActivity.setOrganization(organization);
        

        when(jwtUtils.getAuth0OrgId()).thenReturn("orgId");
        when(maskingString.maskSensitive(anyString())).thenReturn("masked-string");

    }

    @Test
    void testGetActivityById_Success() {
        UUID activityId = UUID.randomUUID();
        String auth0OrgId = "auth0|12345";
        Activity activity = new Activity();
        activity.setId(activityId);

        when(jwtUtils.getAuth0OrgId()).thenReturn(auth0OrgId);
        when(activityRepository.findByIdAndOrganization_Auth0Id(activityId, auth0OrgId)).thenReturn(Optional.of(activity));
        when(maskingString.maskSensitive(anyString())).thenReturn("masked");

        Activity result = activityService.getActivityById(activityId);

        assertNotNull(result);
        assertEquals(activityId, result.getId());
        verify(activityRepository, times(1)).findByIdAndOrganization_Auth0Id(activityId, auth0OrgId);
    }

    @Test
    void testGetActivityById_NotFound() {
        UUID activityId = UUID.randomUUID();
        String auth0OrgId = "auth0|12345";

        when(jwtUtils.getAuth0OrgId()).thenReturn(auth0OrgId);
        when(activityRepository.findByIdAndOrganization_Auth0Id(activityId, auth0OrgId)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            activityService.getActivityById(activityId);
        });

        assertEquals("Activity not found for the given ID: " + activityId, exception.getMessage());
        verify(activityRepository, times(1)).findByIdAndOrganization_Auth0Id(activityId, auth0OrgId);
    }

    @Test
    void testGetActivitiesByEntityId_Success() {
        UUID entityId = UUID.randomUUID();
        String auth0OrgId = "auth0|12345";
        Pageable pageable = Pageable.unpaged();
        Page<Activity> activities = new PageImpl<>(Collections.emptyList());

        when(jwtUtils.getAuth0OrgId()).thenReturn(auth0OrgId);
        when(activityRepository.findAllByEntityIdAndOrganization_Auth0Id(entityId, auth0OrgId, pageable)).thenReturn(activities);

        PaginatedResponseDto<ActivityResponse> result = activityService.getActivitiesByEntityId(entityId, pageable);

        assertNotNull(result);
        assertEquals(0, result.getTotalElements());
        verify(activityRepository, times(1)).findAllByEntityIdAndOrganization_Auth0Id(entityId, auth0OrgId, pageable);
    }

    @Test
    void testCreateActivity_Success() {
        CreateActivityRequest request = new CreateActivityRequest();
        request.setPerformedById(UUID.randomUUID());
        request.setProjectId(UUID.randomUUID());
        request.setEntityType("ISSUE");

        Users user = new Users();
        Projects project = new Projects();
        Organizations organization = new Organizations();
        

        when(userService.getUserEntity(request.getPerformedById())).thenReturn(user);
        when(projectService.getProjectById(request.getProjectId())).thenReturn(project);
        when(jwtUtils.getAuth0OrgId()).thenReturn("auth0|12345");
        when(organizationsService.getOrganizationByAuth0Id("auth0|12345")).thenReturn(organization);
        when(activityRepository.save(any(Activity.class))).thenReturn(mockActivity);

        ActivityResponse result = activityService.createActivity(request);

        assertNotNull(result);
        verify(activityRepository, times(1)).save(any(Activity.class));
    }

    @Test
    void testDeleteActivityById_Success() {

        when(activityRepository.findByIdAndOrganization_Auth0Id(eq(activityId), anyString())).thenReturn(Optional.of(mockActivity));

        ActivityResponse result = activityService.deleteActivityById(activityId);

        assertNotNull(result);
        assertEquals(activityId, result.getId());
    }
}
