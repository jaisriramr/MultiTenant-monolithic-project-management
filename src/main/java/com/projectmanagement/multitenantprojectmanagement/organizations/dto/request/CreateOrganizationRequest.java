package com.projectmanagement.multitenantprojectmanagement.organizations.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateOrganizationRequest {
    @NotBlank(message = "Organization name cannot be empty")
    private String name;
    @NotBlank(message = "Organization display Name cannot be empty")
    private String displayName;
    @NotBlank(message = "Organization domain cannot be empty")
    private String domain;
}
