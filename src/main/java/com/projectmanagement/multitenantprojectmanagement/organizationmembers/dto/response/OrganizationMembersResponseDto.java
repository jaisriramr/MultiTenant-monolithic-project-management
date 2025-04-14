package com.projectmanagement.multitenantprojectmanagement.organizationmembers.dto.response;

import java.time.LocalDate;
import java.util.UUID;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OrganizationMembersResponseDto {
    private UUID id;
    private UUID userId;
    private UUID organizationId;
    private String organizationName;
    private UUID roleId;
    private String roleName;
    private LocalDate joinedAt;
}
