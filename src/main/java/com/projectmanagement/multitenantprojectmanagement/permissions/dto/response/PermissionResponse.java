package com.projectmanagement.multitenantprojectmanagement.permissions.dto.response;

import java.util.UUID;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PermissionResponse {
    private UUID id;
    private String name;
    private String description;
    private String module;
}
