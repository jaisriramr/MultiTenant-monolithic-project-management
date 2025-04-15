package com.projectmanagement.multitenantprojectmanagement.organizationmembers.dto.response;

import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OrganizationMembersResponseDto {
    private UUID id;
    private UUID userId;
    private String userAuth0Id;
    private UUID organizationId;
    private String organizationName;
    private String orgAuth0Id;
    private Set<OrganizationMembersRoleDto> roles;
    private LocalDate joinedAt;
}
