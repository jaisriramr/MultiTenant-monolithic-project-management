package com.projectmanagement.multitenantprojectmanagement.core.sprint.dto.request;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateSprintRequest {
    private String name;
    private String description;
    private String startDate;
    private String endDate;
    private UUID projectId;
}
