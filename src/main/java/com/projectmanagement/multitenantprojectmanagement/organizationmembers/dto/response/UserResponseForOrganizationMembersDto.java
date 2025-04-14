package com.projectmanagement.multitenantprojectmanagement.organizationmembers.dto.response;

import java.util.UUID;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserResponseForOrganizationMembersDto {
    private UUID id;
    private String auth0Id;
    private String name;
    private String profilePic;
    private Boolean isActive;
}
