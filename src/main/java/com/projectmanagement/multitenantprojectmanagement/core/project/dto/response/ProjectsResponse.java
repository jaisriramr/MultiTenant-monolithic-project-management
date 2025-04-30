package com.projectmanagement.multitenantprojectmanagement.core.project.dto.response;

import java.util.UUID;

import com.projectmanagement.multitenantprojectmanagement.core.project.enums.ProjectStatus;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProjectsResponse {
    private UUID id;
    private String name;
    private ProjectOrgResponse organization;
    private ProjectUserResponse createdBy;
    private ProjectStatus status;
    private String createdAt;
    private String updatedAt;
}
