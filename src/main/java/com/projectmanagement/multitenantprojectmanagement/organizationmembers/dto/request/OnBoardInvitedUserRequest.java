package com.projectmanagement.multitenantprojectmanagement.organizationmembers.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OnBoardInvitedUserRequest {
    private String name;
    private String email;
    private String auth0UserId;
    private String auth0OrgId;
    private String roleName;
}
