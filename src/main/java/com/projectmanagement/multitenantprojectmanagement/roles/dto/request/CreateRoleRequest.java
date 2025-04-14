package com.projectmanagement.multitenantprojectmanagement.roles.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CreateRoleRequest {
    @NotBlank(message = "Role Name Must be Present")
    private String name;
    @NotBlank(message = "Organization Id is needed for mapping role to an organization")
    private String organizationId;
}
