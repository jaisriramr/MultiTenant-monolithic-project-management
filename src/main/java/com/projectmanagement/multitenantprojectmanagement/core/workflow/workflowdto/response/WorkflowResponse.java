package com.projectmanagement.multitenantprojectmanagement.core.workflow.workflowdto.response;

import java.util.List;
import java.util.UUID;

import com.projectmanagement.multitenantprojectmanagement.core.workflow.transition.dto.response.TransitionResponse;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class WorkflowResponse {
    private UUID id;
    private String name;
    private String description;
    private Boolean isDefault;
    private List<TransitionResponse> transactions;
}
