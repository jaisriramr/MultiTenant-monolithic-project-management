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

    public InviteUserToAnOrganization() {
    }

    public InviteUserToAnOrganization(String auth0OrgId, String inviterName, String emai, String roleId) {
        this.auth0OrgId = auth0OrgId;
        this.inviterName = inviterName;
        this.emai = emai;
        this.roleId = roleId;
    }

    public String getAuth0OrgId() {
        return auth0OrgId;
    }

    public String getInviterName() {
        return inviterName;
    }

    public String getEmai() {
        return emai;
    }

    public String getRoleId() {
        return roleId;
    }

    public void setAuth0OrgId(String auth0OrgId) {
        this.auth0OrgId = auth0OrgId;
    }

    public void setInviterName(String inviterName) {
        this.inviterName = inviterName;
    }

    public void setEmai(String emai) {
        this.emai = emai;
    }

    public void setRoleId(String roleId) {
        this.roleId = roleId;
    }

}
