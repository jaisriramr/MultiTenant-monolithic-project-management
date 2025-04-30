package com.projectmanagement.multitenantprojectmanagement.core.activity.dto.request;

import java.util.UUID;

import lombok.Data;

@Data
public class CreateActivityRequest {
    private UUID entityId;
    private UUID projectId;
    private String organizationId;
    private String action;
    private String description;
    private String fieldChanged;
    private String oldValue;
    private String newValue;
    private String entityType;
    private UUID performedById;
}
