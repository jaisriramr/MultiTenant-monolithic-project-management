package com.projectmanagement.multitenantprojectmanagement.organizationmembers.dto.response;

import java.util.UUID;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OrganizationResponseForOrganizationMembersDto {
    private UUID id;
    private String name;
    private String auth0Id;
}
