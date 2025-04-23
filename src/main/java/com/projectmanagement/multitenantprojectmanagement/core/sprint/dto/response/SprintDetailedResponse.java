package com.projectmanagement.multitenantprojectmanagement.core.sprint.dto.response;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import com.projectmanagement.multitenantprojectmanagement.core.issue.Issue;
import com.projectmanagement.multitenantprojectmanagement.core.project.dto.response.ProjectDetailsResponse;
import com.projectmanagement.multitenantprojectmanagement.core.project.dto.response.ProjectsResponse;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SprintDetailedResponse {
    private UUID id;
    private String name;
    private LocalDate startDate;
    private LocalDate endDate;
    private String status;
    private ProjectsResponse project;
    private List<Issue> issues;
    private Instant createdAt;
    private Instant updatedAt;
}
