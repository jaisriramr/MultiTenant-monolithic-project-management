package com.projectmanagement.multitenantprojectmanagement.organizationmembers.dto.response;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ListUsersOfAnOrganizationDto {
    private UUID id;
    private UserResponseForOrganizationMembersDto user;
    private OrganizationResponseForOrganizationMembersDto organization;
    private Set<OrganizationMembersRoleDto> roles;
    private LocalDate joinedAt;
}
