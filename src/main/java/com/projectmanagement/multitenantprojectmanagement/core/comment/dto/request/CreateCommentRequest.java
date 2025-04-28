package com.projectmanagement.multitenantprojectmanagement.core.comment.dto.request;

import java.util.UUID;

import lombok.Data;

@Data
public class CreateCommentRequest {
    private String comment;
    private UUID issueId;
    private UUID authorId;
    private UUID projectId;
}
