package com.projectmanagement.multitenantprojectmanagement.users.dto.response;

import java.util.UUID;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserListResponseDto {
    private UUID id;
    private String name;
    private String profilePic;
}
