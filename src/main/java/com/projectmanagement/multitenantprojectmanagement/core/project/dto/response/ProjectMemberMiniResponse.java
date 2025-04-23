package com.projectmanagement.multitenantprojectmanagement.core.project.dto.response;

import java.util.UUID;

import com.projectmanagement.multitenantprojectmanagement.roles.dto.response.RoleResponse;
import com.projectmanagement.multitenantprojectmanagement.users.dto.response.UserListResponseDto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProjectMemberMiniResponse {
    private UUID id;
    private UserListResponseDto user;
    private RoleResponse role;
}
