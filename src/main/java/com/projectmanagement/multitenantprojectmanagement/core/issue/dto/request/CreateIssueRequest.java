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
public class CreateIssueRequest {
    private UUID projectId;
    private UUID sprintId;
    private String title;
    private String type;
    private UUID reporterId;
}
