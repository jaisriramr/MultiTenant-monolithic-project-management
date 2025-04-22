package com.projectmanagement.multitenantprojectmanagement.core.workflow.status.dto.request;

import java.util.UUID;

import com.projectmanagement.multitenantprojectmanagement.core.workflow.status.enums.Category;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CreateStatusRequest {

    @NotBlank(message = "Name must be passed")
    private String name;

    private String category;

    private Boolean defaultStatus;

    @NotNull
    private UUID projectId;
}
