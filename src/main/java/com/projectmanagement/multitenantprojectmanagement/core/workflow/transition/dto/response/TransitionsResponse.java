package com.projectmanagement.multitenantprojectmanagement.core.workflow.transition.dto.response;

import java.util.List;
import java.util.UUID;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TransitionsResponse {
    private UUID workflowId;
    private List<TransitionResponse> transitions;
}
