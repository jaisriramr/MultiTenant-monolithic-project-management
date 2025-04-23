package com.projectmanagement.multitenantprojectmanagement.core.workflow.workflowdto.request;

import java.util.UUID;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UpdateWorkflowRequest {
    private UUID id;
    private String name;
    private String description;
    private Boolean isDefault;
}
