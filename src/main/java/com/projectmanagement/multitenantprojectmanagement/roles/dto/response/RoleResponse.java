package com.projectmanagement.multitenantprojectmanagement.roles.dto.response;

import java.util.UUID;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RoleResponse {
    private UUID id;
    private String name;
    private String organizationId;
}
