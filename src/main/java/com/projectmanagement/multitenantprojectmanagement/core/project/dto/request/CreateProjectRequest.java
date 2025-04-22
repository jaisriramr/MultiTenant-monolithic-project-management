package com.projectmanagement.multitenantprojectmanagement.core.project.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CreateProjectRequest {
    @NotBlank(message = "Name cannot be blank!")
    private String name;
    @NotBlank(message = "Key cannot be blank!")
    private String key;
}
