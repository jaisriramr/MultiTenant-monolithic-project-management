package com.projectmanagement.multitenantprojectmanagement.core.issue.dto.response;

import java.util.UUID;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ListIssuesUserDto {
    private UUID id;
    private String name;
    private String profilePic;
}
