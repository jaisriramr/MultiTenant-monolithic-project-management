package com.projectmanagement.multitenantprojectmanagement.core.issue.dto.response;

import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import com.projectmanagement.multitenantprojectmanagement.core.comment.Comment;
import com.projectmanagement.multitenantprojectmanagement.core.comment.dto.response.CommentResponse;
import com.projectmanagement.multitenantprojectmanagement.core.epic.Epic;
import com.projectmanagement.multitenantprojectmanagement.core.label.Label;
import com.projectmanagement.multitenantprojectmanagement.core.label.dto.response.LabelResponse;
import com.projectmanagement.multitenantprojectmanagement.core.subtask.SubTask;
import com.projectmanagement.multitenantprojectmanagement.core.watcher.Watcher;
import com.projectmanagement.multitenantprojectmanagement.core.worklog.WorkLog;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
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
    private List<Watcher> watchers;
    private List<WorkLog> worklogs;
    private List<SubTask> subTasks;
    private UUID epicId;

    private ListIssuesUserDto assignee;
    private ListIssuesUserDto reporter;
    private Instant createdAt;
    private Instant updatedAt;
}
