package com.projectmanagement.multitenantprojectmanagement.roles.dto.request;

import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdateRoleRequest {
    @NotNull
    private UUID id;
    @NotBlank(message = "Role Name Must be Present")
    private String name;
}
