package com.projectmanagement.multitenantprojectmanagement.permissions.dto.response;

import java.util.UUID;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PermissionsResponse {
    private UUID id;
    private String name;
}
