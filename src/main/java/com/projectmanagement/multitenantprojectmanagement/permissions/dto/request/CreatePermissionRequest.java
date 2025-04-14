package com.projectmanagement.multitenantprojectmanagement.permissions.dto.request;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CreatePermissionRequest {
    private String value;
    private String description;
}
