package com.projectmanagement.multitenantprojectmanagement.core.workflow.status.dto.response;

import java.util.UUID;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class StatusResponse {
    private UUID id;
    private String name;
    private Boolean defaultStatus;
    private String category;
    private UUID projectId;
}
