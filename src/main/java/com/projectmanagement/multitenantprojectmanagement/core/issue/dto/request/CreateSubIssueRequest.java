package com.projectmanagement.multitenantprojectmanagement.core.issue.dto.request;

import java.util.UUID;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CreateSubIssueRequest {
    private UUID projectId;
    private UUID sprintId;
    private UUID issueId;
    private String title;
    private String type;
    private UUID reporterId;
}
