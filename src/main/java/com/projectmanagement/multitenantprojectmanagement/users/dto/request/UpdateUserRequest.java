package com.projectmanagement.multitenantprojectmanagement.users.dto.request;

import java.util.UUID;

import com.projectmanagement.multitenantprojectmanagement.users.embeddable.About;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class UpdateUserRequest {
    @NotEmpty
    private UUID id;
    private String name;
    private About about;
    private String coverPic;
    private String profilePic;
}
