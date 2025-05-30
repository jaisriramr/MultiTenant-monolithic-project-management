package com.projectmanagement.multitenantprojectmanagement.core.worklog.dto.response;

import java.time.Instant;
import java.time.LocalDateTime;
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
public class WorklogResponse {
    private UUID id;
    private UserListResponseDto user;
    private LocalDateTime startedDateTime;
    private String comment;
    private Integer timeSpent;
    private Instant createdAt;
    private Instant updatedAt;
}
