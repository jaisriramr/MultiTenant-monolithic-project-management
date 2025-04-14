package com.projectmanagement.multitenantprojectmanagement.organizations.dto.response;

import java.util.UUID;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OrganizationsResponse {
    private UUID id;
    private String name;
    private String auth0Id;
}
