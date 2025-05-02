package com.projectmanagement.multitenantprojectmanagement.organizationinvitation.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateInvitationStatus {
    private String invitationId;
    private String status;
}
