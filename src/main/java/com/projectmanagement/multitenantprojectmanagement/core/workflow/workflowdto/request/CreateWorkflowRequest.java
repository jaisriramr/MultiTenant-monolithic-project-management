package com.projectmanagement.multitenantprojectmanagement.core.workflow.workflowdto.request;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CreateWorkflowRequest {
    private String name;
    private String description;
    private Boolean isDefault;
}
