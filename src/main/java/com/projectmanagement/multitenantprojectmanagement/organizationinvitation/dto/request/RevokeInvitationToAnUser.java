package com.projectmanagement.multitenantprojectmanagement.organizationinvitation.dto.request;

import lombok.Data;

@Data
public class RevokeInvitationToAnUser {
    private String orgId;
    private String invitationId;
}
