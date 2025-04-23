package com.projectmanagement.multitenantprojectmanagement.core.project.dto.response;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import com.projectmanagement.multitenantprojectmanagement.core.issue.Issue;
import com.projectmanagement.multitenantprojectmanagement.core.project.enums.ProjectStatus;
import com.projectmanagement.multitenantprojectmanagement.core.projectMember.ProjectMember;
import com.projectmanagement.multitenantprojectmanagement.core.sprint.Sprint;
import com.projectmanagement.multitenantprojectmanagement.core.sprint.dto.response.ListSprintResponse;
import com.projectmanagement.multitenantprojectmanagement.core.workflow.workflowscheme.WorkflowScheme;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProjectDetailsResponse {
    private UUID id;
    private String name;
    private String key;
    private ProjectOrgResponse organization;
    private ProjectUserResponse createdBy;
    private ProjectStatus status;
    private List<ListSprintResponse> sprints;
    private List<Issue> issues;
    private List<ProjectMember> projectMembers;
    private WorkflowScheme workflowScheme;
    private Instant createdAt;
    private Instant updatedAt;
}
