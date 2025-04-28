package com.projectmanagement.multitenantprojectmanagement.core.activity;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.projectmanagement.multitenantprojectmanagement.auth0.utils.JWTUtils;
import com.projectmanagement.multitenantprojectmanagement.core.activity.dto.request.CreateActivityRequest;
import com.projectmanagement.multitenantprojectmanagement.core.activity.dto.response.ActivityResponse;
import com.projectmanagement.multitenantprojectmanagement.core.activity.mapper.ActivityMapper;
import com.projectmanagement.multitenantprojectmanagement.core.attachment.AttachmentService;
import com.projectmanagement.multitenantprojectmanagement.core.project.ProjectService;
import com.projectmanagement.multitenantprojectmanagement.core.project.Projects;
import com.projectmanagement.multitenantprojectmanagement.exception.NotFoundException;
import com.projectmanagement.multitenantprojectmanagement.helper.MaskingString;
import com.projectmanagement.multitenantprojectmanagement.organizations.Organizations;
import com.projectmanagement.multitenantprojectmanagement.organizations.OrganizationsService;
import com.projectmanagement.multitenantprojectmanagement.organizations.dto.response.PaginatedResponseDto;
import com.projectmanagement.multitenantprojectmanagement.users.UserService;
import com.projectmanagement.multitenantprojectmanagement.users.Users;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ActivityService {

    private final ActivityRepository activityRepository;
    private final UserService userService;
    private final ProjectService projectService;
    private final OrganizationsService organizationsService;
    private final MaskingString maskingString;
    private static final Logger logger = LoggerFactory.getLogger(ActivityService.class);
    private final JWTUtils jwtUtils;

    public Activity getActivityById(UUID id) {
        logger.info("Getting activity for the given ID: {}", maskingString.maskSensitive(id.toString()));

        String auth0OrgId = jwtUtils.getAuth0OrgId();

        Activity activity = activityRepository.findByIdAndOrganization_Auth0Id(id, auth0OrgId).orElseThrow(() -> new NotFoundException("Activity not found for the given ID: " + id));

        logger.debug("Fetched activity ID: {}", maskingString.maskSensitive(activity.getId().toString()));

        return activity;
    }

    public PaginatedResponseDto<ActivityResponse> getActivitiesByEntityId(UUID entityId, Pageable pageable) {
        logger.info("Getting activites for the given ID: {}", maskingString.maskSensitive(entityId.toString()));

        String auth0OrgId = jwtUtils.getAuth0OrgId();

        Page<Activity> activites = activityRepository.findAllByEntityIdAndOrganization_Auth0Id(entityId, auth0OrgId,pageable);

        logger.debug("Fetched {} activites", activites.getTotalElements());

        return ActivityMapper.toPaginatedResponseDto(activites);
    }

    public PaginatedResponseDto<ActivityResponse> getActivitiesByProjectIdAndOrgId(UUID projectId, String auth0orgId, Pageable pageable){
        logger.info("Getting activities for the given project ID: {} and org ID: {}", maskingString.maskSensitive(projectId.toString()), maskingString.maskSensitive(auth0orgId));

        Organizations org = organizationsService.getOrganizationByAuth0Id(auth0orgId);

        Page<Activity> activites = activityRepository.findAllByProjectIdAndOrganizationId(projectId, org.getId(), pageable);

        logger.debug("Fetched {} activities", activites.getTotalElements());

        return ActivityMapper.toPaginatedResponseDto(activites);
    }

    public ActivityResponse createActivity(CreateActivityRequest createActivityRequest) {
        logger.info("Creating activity");

        Users performedBy = userService.getUserEntity(createActivityRequest.getPerformedById());

        Projects project = projectService.getProjectById(createActivityRequest.getProjectId());

        String auth0OrgId = jwtUtils.getAuth0OrgId();

        Organizations organization = organizationsService.getOrganizationByAuth0Id(auth0OrgId);

        Activity activity = ActivityMapper.toActivityEntity(createActivityRequest, performedBy, project, organization);

        Activity savedActivity = activityRepository.save(activity);

        logger.debug("Saved activity ID: {}", maskingString.maskSensitive(savedActivity.getId().toString()));

        return ActivityMapper.toActivityResponse(savedActivity);
    }

    public ActivityResponse deleteActivityById(UUID id) {
        logger.info("Deleting activity by ID: {}", maskingString.maskSensitive(id.toString()));

        Activity activity = getActivityById(id);

        activityRepository.delete(activity);

        logger.debug("Deleted the given activity");

        return ActivityMapper.toActivityResponse(activity);
    }

}
