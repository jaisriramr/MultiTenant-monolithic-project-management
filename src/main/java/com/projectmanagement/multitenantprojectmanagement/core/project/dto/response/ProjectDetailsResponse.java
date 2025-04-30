package com.projectmanagement.multitenantprojectmanagement.core.project.dto.response;

import java.util.List;
import java.util.UUID;

import com.projectmanagement.multitenantprojectmanagement.core.project.enums.ProjectStatus;
import com.projectmanagement.multitenantprojectmanagement.core.sprint.dto.response.ListSprintResponse;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProjectDetailsResponse {
    private UUID id;
    private String name;
    private String key;
    private ProjectOrgResponse organization;
    private ProjectUserResponse createdBy;
    private ProjectStatus status;
    private List<ListSprintResponse> sprints;
    // private List<Issue> issues;
    private List<ProjectMemberMiniResponse> projectMembers;
    // private WorkflowScheme workflowScheme;
    private String createdAt;
    private String updatedAt;
}
