package com.projectmanagement.multitenantprojectmanagement.core.comment.dto.request;

import java.util.UUID;

import lombok.Data;

@Data
public class CreateCommentReplyRequest {
    private UUID parentId;
    private UUID authorId;
    private String comment;
}
