package com.projectmanagement.multitenantprojectmanagement.roles.dto.response;

import java.util.Set;
import java.util.UUID;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RoleResponse {
    private UUID id;
    private String name;
    private String auth0Id;
    private String organizationId;
    private Set<String> permissions;
}
