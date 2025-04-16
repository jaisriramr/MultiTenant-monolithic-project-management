package com.projectmanagement.multitenantprojectmanagement.organizationinvitation.dto.request;

import lombok.Data;

@Data
public class UpdateInvitationStatus {
    private String invitationId;
    private String status;
}
