package com.projectmanagement.multitenantprojectmanagement.roles.mapper;

import java.util.ArrayList;
import java.util.List;

import com.projectmanagement.multitenantprojectmanagement.roles.Roles;
import com.projectmanagement.multitenantprojectmanagement.roles.dto.request.CreateRoleRequest;
import com.projectmanagement.multitenantprojectmanagement.roles.dto.response.RoleResponse;
import com.projectmanagement.multitenantprojectmanagement.roles.dto.response.RolesResponse;

public class RoleMapper {

    public static RoleResponse toRoleResponse(Roles role) {
        return RoleResponse.builder()
                .id(role.getId())
                .name(role.getName())
                .organizationId(role.getOrganizationId())
                .build();
    }

    public static List<RolesResponse> toRolesResponse(List<Roles> roles) {
        List<RolesResponse> response = new ArrayList<>();

        for(Roles role: roles) {
            RolesResponse roleResponse = RolesResponse.builder()
                                        .id(role.getId())
                                        .name(role.getName())
                                        .build();

            response.add(roleResponse);
        }

        return response;
    }

    public static Roles toEntityRole(CreateRoleRequest createRoleRequest, String auth0Id) {
        Roles role = new Roles();
        role.setName(createRoleRequest.getName());
        role.setOrganizationId(createRoleRequest.getOrganizationId());
        role.setAuth0Id(auth0Id);
        return role;
    }

}
