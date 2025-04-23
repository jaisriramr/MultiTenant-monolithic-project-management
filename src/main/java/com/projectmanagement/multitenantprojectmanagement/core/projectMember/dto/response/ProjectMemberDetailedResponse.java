package com.projectmanagement.multitenantprojectmanagement.core.projectMember.dto.response;

import java.time.Instant;
import java.util.UUID;

import com.projectmanagement.multitenantprojectmanagement.roles.dto.response.RoleResponse;
import com.projectmanagement.multitenantprojectmanagement.users.dto.response.UserResponseDto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProjectMemberDetailedResponse {
    private UUID id;
    private ProjectMemberProjectDto project;
    private UserResponseDto user;
    private RoleResponse role;
    private Instant createdAt;
    private Instant updatedAt;
}
