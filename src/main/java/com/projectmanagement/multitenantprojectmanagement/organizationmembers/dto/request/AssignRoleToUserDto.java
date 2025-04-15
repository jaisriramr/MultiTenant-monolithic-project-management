package com.projectmanagement.multitenantprojectmanagement.organizationmembers.dto.request;

import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AssignRoleToUserDto {
    private List<String> roleIds;
}
