package com.projectmanagement.multitenantprojectmanagement.core.projectMember.dto.response;

import java.util.UUID;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProjectMemberProjectDto {
    private UUID id;
    private String name;
    private String key;
}
