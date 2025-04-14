package com.projectmanagement.multitenantprojectmanagement.organizations.dto.request;

import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UpdateOrganizationRequest {
    @NotBlank(message = "Org Id must be passed")
    private UUID id;
    private String name;
    private String displayName;
}
