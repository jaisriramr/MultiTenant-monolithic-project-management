package com.projectmanagement.multitenantprojectmanagement.users.dto.request;

import java.util.UUID;

import com.projectmanagement.multitenantprojectmanagement.users.embeddable.About;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserRequest {
    @NotNull
    private UUID id;
    private String name;
    private About about;
    private String coverPic;
    private String profilePic;
}
