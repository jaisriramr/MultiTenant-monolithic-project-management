package com.projectmanagement.multitenantprojectmanagement.core.project.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateProjectRequest {
    @NotBlank(message = "Name cannot be blank!")
    private String name;
    @NotBlank(message = "Key cannot be blank!")
    private String key;
}
