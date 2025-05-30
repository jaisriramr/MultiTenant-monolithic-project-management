package com.projectmanagement.multitenantprojectmanagement.organizationmembers.dto.request;

import java.time.LocalDate;

import com.projectmanagement.multitenantprojectmanagement.organizations.Organizations;
import com.projectmanagement.multitenantprojectmanagement.roles.Roles;
import com.projectmanagement.multitenantprojectmanagement.users.Users;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateOrganizationMembersDto {
    private Users user;
    private Organizations org;
    private Roles role;
    private LocalDate joinedAt;
}
