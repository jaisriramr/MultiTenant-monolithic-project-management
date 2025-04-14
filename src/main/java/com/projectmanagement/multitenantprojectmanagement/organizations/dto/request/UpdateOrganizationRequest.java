package com.projectmanagement.multitenantprojectmanagement.organizations.dto.request;

import java.util.UUID;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UpdateOrganizationRequest {
    private UUID id;
    private String name;
    private String displayName;
}
