package com.projectmanagement.multitenantprojectmanagement.users.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateUserRequest {

    @NotBlank(message = "Name is required")
    private String name;
    @Email(message = "Not a valid email")
    @NotBlank(message = "Email is required")
    private String email;
    @NotBlank(message = "Auth0 id is required")
    private String auth0UserId;

}
