package com.projectmanagement.multitenantprojectmanagement.core.workflow.transition.dto.response;

import java.util.UUID;

import com.projectmanagement.multitenantprojectmanagement.core.workflow.status.dto.response.StatusResponse;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TransitionResponse {
    private UUID id;
    private String name;
    private StatusResponse from;
    private StatusResponse to;
}
