package com.projectmanagement.multitenantprojectmanagement.organizationmembers.dto.response;

import java.util.Set;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserPermissionsDto {
    private Set<String> permissions;
}
