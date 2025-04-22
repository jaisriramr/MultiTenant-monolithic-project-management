package com.projectmanagement.multitenantprojectmanagement.core.project.dto.response;

import java.util.UUID;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProjectUserResponse {
    private UUID id;
    private String auth0Id;
    private String name;
    private String profilePic;
}
