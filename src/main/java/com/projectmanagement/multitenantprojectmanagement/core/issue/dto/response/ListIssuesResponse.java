package com.projectmanagement.multitenantprojectmanagement.core.issue.dto.response;

import java.util.UUID;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ListIssuesResponse {
    private UUID id;
    private String key;
    private String title;
    private String status;
    private String type;
    private String priority;
    private ListIssuesUserDto assignee;
    private Integer storyPoints;
}
