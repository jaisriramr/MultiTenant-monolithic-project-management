package com.projectmanagement.multitenantprojectmanagement.core.projectMember.dto.request;

import java.util.UUID;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateProjectMember {
    @NotNull
    private UUID projectId;
    @NotNull
    private UUID userId;
    @NotNull
    private UUID roleId;
}
