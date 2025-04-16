package com.projectmanagement.multitenantprojectmanagement.organizationmembers.dto.response;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

import com.projectmanagement.multitenantprojectmanagement.organizations.dto.response.OrganizationResponse;
import com.projectmanagement.multitenantprojectmanagement.users.dto.response.UserResponseDto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserDetailsFromOrganizationMember {
    private UUID id;
    private UserResponseDto user;
    private OrganizationResponse organization;
    private Set<OrganizationMembersRoleDto> roles;
    private Set<String> permissions;
    private Boolean isDeleted;
    private Instant deletedAt;
    private UUID deletedBy;
    private LocalDate joinedAt;
}
