package com.projectmanagement.multitenantprojectmanagement.users.dto.response;

import java.util.UUID;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserOrganizations {
    private UUID id;
    private String auth0Id;
    private String name;
}
