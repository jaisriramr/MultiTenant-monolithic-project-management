package com.projectmanagement.multitenantprojectmanagement.roles.dto.request;

import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UpdateRoleRequest {
    @NotNull
    private UUID id;
    @NotBlank(message = "Role Name Must be Present")
    private String name;
}
