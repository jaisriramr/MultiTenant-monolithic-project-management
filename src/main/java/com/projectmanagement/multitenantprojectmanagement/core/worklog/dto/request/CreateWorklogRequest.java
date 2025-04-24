package com.projectmanagement.multitenantprojectmanagement.core.worklog.dto.request;

import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CreateWorklogRequest {
    @NotNull
    private Integer timeSpentInMinutes;
    private String comment;
    @NotNull
    private LocalDateTime startedDateTime;
    @NotNull
    private UUID issueId;
    @NotNull
    private UUID userId;
}
