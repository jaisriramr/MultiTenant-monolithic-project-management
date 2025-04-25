package com.projectmanagement.multitenantprojectmanagement.core.comment.dto.request;

import java.util.UUID;

import lombok.Data;

@Data
public class UpdateCommentRequest {
    private String comment;
    private UUID id;
}
