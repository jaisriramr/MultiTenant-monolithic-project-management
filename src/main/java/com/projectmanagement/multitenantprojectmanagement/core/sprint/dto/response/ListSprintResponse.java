package com.projectmanagement.multitenantprojectmanagement.core.sprint.dto.response;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.projectmanagement.multitenantprojectmanagement.core.issue.Issue;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ListSprintResponse {
    private UUID id;
    private String name;
    private String goal;
    private String status;
    // private List<Issue> issues;
    private String createdAt;
    private String updatedAt;
}
