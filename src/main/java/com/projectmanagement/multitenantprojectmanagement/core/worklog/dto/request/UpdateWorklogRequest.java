package com.projectmanagement.multitenantprojectmanagement.core.worklog.dto.request;

import java.time.LocalDateTime;
import java.util.UUID;

import lombok.Data;


@Data
public class UpdateWorklogRequest {
    private UUID id;
    private Integer timeSpentInMinutes;
    private String comment;
    private LocalDateTime startedDateTime;
}
