package com.projectmanagement.multitenantprojectmanagement.roles.dto.request;

import java.util.UUID;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UpdateRoleRequest {
    private UUID id;
    private String name;
}
