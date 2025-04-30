package com.projectmanagement.multitenantprojectmanagement.core.epic.dto.request;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateEpicRequest {
    private String name;
    private String description;
    private String color;
    private UUID projectId;
}
