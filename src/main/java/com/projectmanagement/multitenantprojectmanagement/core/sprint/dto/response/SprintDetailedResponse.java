package com.projectmanagement.multitenantprojectmanagement.core.sprint.dto.response;

import java.time.LocalDate;
import java.util.UUID;

import com.projectmanagement.multitenantprojectmanagement.core.project.dto.response.ProjectsResponse;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SprintDetailedResponse {
    private UUID id;
    private String name;
    private LocalDate startDate;
    private LocalDate endDate;
    private String status;
    private ProjectsResponse project;
    private String createdAt;
    private String updatedAt;
}
