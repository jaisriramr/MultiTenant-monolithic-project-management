package com.projectmanagement.multitenantprojectmanagement.core.activity.dto.response;

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
