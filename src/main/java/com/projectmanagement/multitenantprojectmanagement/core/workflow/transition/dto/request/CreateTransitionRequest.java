package com.projectmanagement.multitenantprojectmanagement.core.workflow.transition.dto.request;

import java.util.UUID;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CreateTransitionRequest {
    private String name;
    private UUID fromId;
    private UUID toId;
    private UUID workflowId;
}
