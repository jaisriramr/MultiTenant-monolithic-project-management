package com.projectmanagement.multitenantprojectmanagement.core.epic.dto.request;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UpdateEpicRequest {
    private String name;
    private String description;
    private String color;
}
