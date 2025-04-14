package com.projectmanagement.multitenantprojectmanagement.organizations.dto.request;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CreateOrganizationRequest {
    private String name;
    private String displayName;
    private String domain;
}
