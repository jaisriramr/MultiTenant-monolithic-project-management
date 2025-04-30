package com.projectmanagement.multitenantprojectmanagement.roles.dto.response;

import java.util.Set;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RoleResponse {
    private UUID id;
    private String name;
    private String auth0Id;
    private Set<String> permissions;
}
