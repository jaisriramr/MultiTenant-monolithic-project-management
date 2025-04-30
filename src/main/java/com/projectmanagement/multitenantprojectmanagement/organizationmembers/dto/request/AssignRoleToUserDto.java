package com.projectmanagement.multitenantprojectmanagement.organizationmembers.dto.request;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AssignRoleToUserDto {
    private List<String> roleIds;
}
