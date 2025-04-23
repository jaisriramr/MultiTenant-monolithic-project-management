package com.projectmanagement.multitenantprojectmanagement.core.sprint.dto.request;

import java.time.LocalDate;
import java.util.UUID;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UpdateSprintRequest {
    private UUID id;
    private String name;
    private String description;
    private String startDate;
    private String endDate;
}
