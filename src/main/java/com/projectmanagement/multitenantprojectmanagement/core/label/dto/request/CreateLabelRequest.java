package com.projectmanagement.multitenantprojectmanagement.core.label.dto.request;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateLabelRequest {
    private String name;
    private UUID projectId;
    private UUID issueId;
}
