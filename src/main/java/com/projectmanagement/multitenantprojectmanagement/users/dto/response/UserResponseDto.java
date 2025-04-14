package com.projectmanagement.multitenantprojectmanagement.users.dto.response;

import java.util.UUID;

import com.projectmanagement.multitenantprojectmanagement.users.embeddable.About;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserResponseDto {
    private UUID id;
    private String name;
    private String email;
    private String auth0Id;
    private About about;
    private String profilePic;
    private String coverPic;
    private boolean isActive;
    // roles
    // organizations
}
