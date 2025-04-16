package com.projectmanagement.multitenantprojectmanagement.users.dto.response;

import java.util.Set;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserPermissionsDto {
    private String auth0OrgId;
    private Set<String> roles;
    private Set<String> permissions;
}
