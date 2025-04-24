package com.projectmanagement.multitenantprojectmanagement.core.issue.dto.request;

import java.util.UUID;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CreateEpicIssueRequest {
    private UUID projectId;
    private UUID sprintId;
    private String title;
    private UUID reporterId;
    private String color;
}
