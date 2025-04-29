package com.projectmanagement.multitenantprojectmanagement.core.project.dto.response;

import java.util.UUID;

import com.projectmanagement.multitenantprojectmanagement.roles.dto.response.RoleResponse;
import com.projectmanagement.multitenantprojectmanagement.users.dto.response.UserListResponseDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectMemberMiniResponse {
    private UUID id;
    private UserListResponseDto user;
    private RoleResponse role;
}
