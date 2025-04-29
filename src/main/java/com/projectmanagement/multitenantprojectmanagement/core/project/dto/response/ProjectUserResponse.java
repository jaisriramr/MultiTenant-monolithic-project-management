package com.projectmanagement.multitenantprojectmanagement.core.project.dto.response;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectUserResponse {
    private UUID id;
    private String auth0Id;
    private String name;
    private String profilePic;
}
