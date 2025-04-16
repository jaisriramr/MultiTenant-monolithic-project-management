package com.projectmanagement.multitenantprojectmanagement.organizationinvitation.dto.request;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class InviteUserToAnOrganization {
    private String auth0OrgId;
    private String inviterName;
    private String emai;
    private String roleId;
}
