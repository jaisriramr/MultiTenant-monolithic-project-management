package com.projectmanagement.multitenantprojectmanagement.core.label.dto.request;

import java.util.UUID;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CreateLabelRequest {
    private String name;
    private UUID projectId;
    private UUID issueId;
}
