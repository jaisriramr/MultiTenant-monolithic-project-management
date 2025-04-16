package com.projectmanagement.multitenantprojectmanagement.organizationmembers.dto.request;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OnBoardInvitedUserRequest {
    private String name;
    private String email;
    private String auth0UserId;
    private String auth0OrgId;
    private String roleName;
}
