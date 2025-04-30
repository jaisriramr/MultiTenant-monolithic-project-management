package com.projectmanagement.multitenantprojectmanagement.roles.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateRoleRequest {
    @NotBlank(message = "Role Name Must be Present")
    private String name;
}
