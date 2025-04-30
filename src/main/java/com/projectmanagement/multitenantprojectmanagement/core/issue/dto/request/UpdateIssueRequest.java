package com.projectmanagement.multitenantprojectmanagement.core.issue.dto.request;

import java.util.Set;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateIssueRequest {
    private UUID id;
    private UUID projectId;
    private String title;
    private String description;
    private String status;
    private String type;
    private String priority;
    private UUID sprintId;
    private UUID assigneeId;
    private UUID reporterId;
    private Set<UUID> labels;
    private Integer storyPoints;
}
