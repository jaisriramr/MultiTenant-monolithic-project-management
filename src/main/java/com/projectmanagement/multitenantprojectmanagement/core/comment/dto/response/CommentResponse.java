package com.projectmanagement.multitenantprojectmanagement.core.comment.dto.response;

import java.time.Instant;
import java.util.UUID;

import com.projectmanagement.multitenantprojectmanagement.core.issue.dto.response.ListIssuesUserDto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CommentResponse {
    private UUID id;
    private String content;
    private ListIssuesUserDto author;
    private Instant createdAt;
    private Instant updatedAt;
}
