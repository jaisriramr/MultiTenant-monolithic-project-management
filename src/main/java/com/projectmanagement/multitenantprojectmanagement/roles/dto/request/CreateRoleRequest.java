package com.projectmanagement.multitenantprojectmanagement.roles.dto.request;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CreateRoleRequest {
    private String name;
    private String organizationId;
}
