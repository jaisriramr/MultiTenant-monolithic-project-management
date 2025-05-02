package com.projectmanagement.multitenantprojectmanagement.core.projectMember.dto.response;

import java.util.UUID;

import com.projectmanagement.multitenantprojectmanagement.roles.dto.response.RoleResponse;
import com.projectmanagement.multitenantprojectmanagement.users.dto.response.UserListResponseDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProjectMembersResponse {
    private UUID id;
    private UserListResponseDto user;
    private RoleResponse role;
}
