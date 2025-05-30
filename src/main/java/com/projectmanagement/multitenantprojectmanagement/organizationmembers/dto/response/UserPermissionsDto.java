package com.projectmanagement.multitenantprojectmanagement.organizationmembers.dto.response;

import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserPermissionsDto {
    private Set<String> permissions;
}
