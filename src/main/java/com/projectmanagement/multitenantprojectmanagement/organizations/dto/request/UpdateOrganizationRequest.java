package com.projectmanagement.multitenantprojectmanagement.organizations.dto.request;

import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdateOrganizationRequest {
    @NotBlank(message = "Org Id must be passed")
    private UUID id;
    private String name;
    private String displayName;
}
