package com.projectmanagement.multitenantprojectmanagement.roles.dto.request;

import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AssignPermissions {
    private List<String> permissions;
}
