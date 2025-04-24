package com.projectmanagement.multitenantprojectmanagement.core.epic.dto.response;

import java.util.List;
import java.util.UUID;

import com.projectmanagement.multitenantprojectmanagement.core.issue.dto.response.ListIssuesResponse;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EpicResponse {
    private UUID id;
    private String name;
    private String description;
    private List<ListIssuesResponse> issues;
}
