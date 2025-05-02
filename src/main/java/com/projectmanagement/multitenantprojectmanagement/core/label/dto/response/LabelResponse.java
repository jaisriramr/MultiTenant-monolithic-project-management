package com.projectmanagement.multitenantprojectmanagement.core.label.dto.response;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LabelResponse {
    private UUID id;
    private String name;
    private String color;
}
