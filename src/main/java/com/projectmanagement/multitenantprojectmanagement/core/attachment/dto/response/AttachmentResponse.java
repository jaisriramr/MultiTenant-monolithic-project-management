package com.projectmanagement.multitenantprojectmanagement.core.attachment.dto.response;

import java.time.Instant;
import java.util.UUID;

import com.projectmanagement.multitenantprojectmanagement.users.dto.response.UserListResponseDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AttachmentResponse {
    private UUID id;
    private String name;
    private String type;
    private String url;
    private UUID issueId;
    private UUID commentId;
    private UserListResponseDto uploadedBy;
    private Instant createdAt;
    private Instant updatedAt;
}
