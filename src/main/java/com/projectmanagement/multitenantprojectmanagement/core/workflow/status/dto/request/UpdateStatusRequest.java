package com.projectmanagement.multitenantprojectmanagement.core.workflow.status.dto.request;

import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UpdateStatusRequest {

    @NotNull
    private UUID id;

    @NotBlank(message = "Name must be passed")
    private String name;

    private String category;

    private Boolean defaultStatus;
}
