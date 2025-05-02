package com.projectmanagement.multitenantprojectmanagement.core.issue.dto.response;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
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
