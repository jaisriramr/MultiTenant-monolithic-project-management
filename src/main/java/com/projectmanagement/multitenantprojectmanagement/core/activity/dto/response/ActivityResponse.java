package com.projectmanagement.multitenantprojectmanagement.core.activity.dto.response;

import java.time.Instant;
import java.util.UUID;

import com.projectmanagement.multitenantprojectmanagement.users.dto.response.UserListResponseDto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ActivityResponse {
    private UUID id;
    private String action;
    private String description;
    private String fieldChanged;
    private String oldValue;
    private String newValue;
    private String entityType;
    private UUID entityId;
    private UserListResponseDto performedBy;
    private Instant createdAt;
    private Instant updatedAt;
}
