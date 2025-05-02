package com.projectmanagement.multitenantprojectmanagement.core.issue.dto.request;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateEpicIssueRequest {
    private UUID projectId;
    private UUID sprintId;
    private String title;
    private UUID reporterId;
    private String color;
}
