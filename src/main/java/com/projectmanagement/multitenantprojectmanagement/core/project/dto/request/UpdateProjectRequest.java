package com.projectmanagement.multitenantprojectmanagement.core.project.dto.request;

import java.util.UUID;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UpdateProjectRequest {
    private UUID id;
    private String name;
    private String key;
}
