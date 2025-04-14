package com.projectmanagement.multitenantprojectmanagement.organizationmembers.dto.response;

import java.util.UUID;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OrganizationMembersRoleDto {
    private UUID id;
    private String name;
    private String auth0Id;

    public OrganizationMembersRoleDto(UUID id, String name, String authId) {
        this.id = id;
        this.name = name;
        this.auth0Id = authId;
    }

}
