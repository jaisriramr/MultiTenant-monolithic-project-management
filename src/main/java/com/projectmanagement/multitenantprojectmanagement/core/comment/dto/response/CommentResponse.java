package com.projectmanagement.multitenantprojectmanagement.core.comment.dto.response;

import java.time.Instant;
import java.util.UUID;

import com.projectmanagement.multitenantprojectmanagement.users.dto.response.UserListResponseDto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CommentResponse {
    private UUID id;
    private String content;
    private UserListResponseDto author;
    private int depth;
    private String path;
    private Instant createdAt;
    private Instant updatedAt;
}
