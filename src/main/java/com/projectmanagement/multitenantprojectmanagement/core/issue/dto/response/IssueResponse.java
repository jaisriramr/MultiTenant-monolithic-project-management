package com.projectmanagement.multitenantprojectmanagement.core.issue.dto.response;

import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import com.projectmanagement.multitenantprojectmanagement.core.comment.Comment;
import com.projectmanagement.multitenantprojectmanagement.core.label.dto.response.LabelResponse;
import com.projectmanagement.multitenantprojectmanagement.core.watcher.dto.response.WatcherResponse;
import com.projectmanagement.multitenantprojectmanagement.core.worklog.dto.response.WorklogResponse;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class IssueResponse {
    private UUID id;
    private String key;
    private String title;
    private String description;
    private String status;
    private String type;
    private String priority;
    private UUID projectId;
    private UUID sprintId;
    private Integer storyPoints;
    
    private Set<LabelResponse> labels;
    private List<Comment> comments;
    private List<WatcherResponse> watchers;
    private WorklogResponse worklog;
    // private List<ListIssuesResponse> subTasks;
    private UUID epicId;

    private ListIssuesUserDto assignee;
    private ListIssuesUserDto reporter;
    private Instant createdAt;
    private Instant updatedAt;
}
