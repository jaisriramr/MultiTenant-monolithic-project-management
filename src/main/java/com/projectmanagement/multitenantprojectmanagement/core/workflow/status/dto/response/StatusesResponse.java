package com.projectmanagement.multitenantprojectmanagement.core.workflow.status.dto.response;

import java.util.UUID;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class StatusesResponse {
    private UUID id;
    private String name;
    private String category;
    private Boolean defaultStatus;
}
