package com.projectmanagement.multitenantprojectmanagement.core.issue.dto.request;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateSubIssueRequest {
    private UUID projectId;
    private UUID sprintId;
    private UUID issueId;
    private String title;
    private String type;
    private UUID reporterId;
}
