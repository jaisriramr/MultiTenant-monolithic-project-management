package com.projectmanagement.multitenantprojectmanagement.core.activity.mapper;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;

import com.projectmanagement.multitenantprojectmanagement.core.activity.Activity;
import com.projectmanagement.multitenantprojectmanagement.core.activity.dto.request.CreateActivityRequest;
import com.projectmanagement.multitenantprojectmanagement.core.activity.dto.response.ActivityResponse;
import com.projectmanagement.multitenantprojectmanagement.core.activity.enums.EntityType;
import com.projectmanagement.multitenantprojectmanagement.core.project.Projects;
import com.projectmanagement.multitenantprojectmanagement.organizations.Organizations;
import com.projectmanagement.multitenantprojectmanagement.organizations.dto.response.PaginatedResponseDto;
import com.projectmanagement.multitenantprojectmanagement.users.Users;
import com.projectmanagement.multitenantprojectmanagement.users.dto.mapper.UserMapper;

public class ActivityMapper {


    public static CreateActivityRequest toCreateActivityRequest(UUID entityId,UUID projectId,String orgId,String action, String description, String fieldChanged, String oldValue, String newValue, String entityType, UUID userId) {

        return CreateActivityRequest.builder()
                .entityId(entityId)
                .projectId(projectId)
                .organizationId(orgId)
                .action(action)
                .description(description)
                .fieldChanged(fieldChanged)
                .oldValue(oldValue)
                .newValue(newValue)
                .entityType(entityType)
                .performedById(userId)
                .build();
    }

    public static Activity toActivityEntity(CreateActivityRequest createActivityRequest, Users performedBy, Projects project, Organizations organizations) {

        Activity activity = new Activity();

        activity.setAction(createActivityRequest.getAction());
        activity.setDescription(createActivityRequest.getDescription());
        activity.setEntityId(createActivityRequest.getEntityId());
        activity.setFieldChanged(createActivityRequest.getFieldChanged());
        activity.setProject(project);
        activity.setOrganization(organizations);

        if("ISSUE".equals(createActivityRequest.getEntityType())) {
            activity.setEntityType(EntityType.ISSUE);
        }else if("PROJECT".equals(createActivityRequest.getEntityType())) {
            activity.setEntityType(EntityType.PROJECT);
        }else if("SPRINT".equals(createActivityRequest.getEntityType())) {
            activity.setEntityType(EntityType.SPRINT);
        }else if("COMMENT".equals(createActivityRequest.getEntityType())) {
            activity.setEntityType(EntityType.COMMENT);
        }else if("ATTACHMENT".equals(createActivityRequest.getEntityType())) {
            activity.setEntityType(EntityType.ATTACHMENT);
        }else {
            throw new IllegalArgumentException("Given entity type is not allowed: " + createActivityRequest.getEntityType());
        }

        activity.setOldValue(createActivityRequest.getOldValue());
        activity.setNewValue(createActivityRequest.getNewValue());
        activity.setPerformedBy(performedBy);

        return activity;
    }

    public static ActivityResponse toActivityResponse(Activity activity) {

        return ActivityResponse.builder()
                .id(activity.getId())
                .action(activity.getAction())
                .description(activity.getDescription())
                .fieldChanged(activity.getFieldChanged())
                .oldValue(activity.getOldValue())
                .newValue(activity.getNewValue())
                .entityType(activity.getEntityType().toString())
                .entityId(activity.getEntityId())
                .performedBy(UserMapper.toUserSingleListResponse(activity.getPerformedBy()))
                .createdAt(activity.getCreatedAt())
                .updatedAt(activity.getUpdatedAt())
                .build();

    }

    public static List<ActivityResponse> toListActivityResponses(List<Activity> activities) {

        List<ActivityResponse> response = new ArrayList<>();

        for(Activity activity: activities) {
            response.add(toActivityResponse(activity));
        }

        return response;
    }

    public static PaginatedResponseDto<ActivityResponse> toPaginatedResponseDto(Page<Activity> activities) {

        return PaginatedResponseDto.<ActivityResponse>builder()
                .data(toListActivityResponses(activities.getContent()))
                .page(activities.getNumber())
                .totalPages(activities.getTotalPages())
                .totalElements(activities.getTotalElements())
                .size(activities.getSize())
                .build();

    }

}
