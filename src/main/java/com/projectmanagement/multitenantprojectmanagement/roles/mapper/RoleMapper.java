package com.projectmanagement.multitenantprojectmanagement.roles.mapper;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;

import com.projectmanagement.multitenantprojectmanagement.permissions.Permissions;
import com.projectmanagement.multitenantprojectmanagement.roles.Roles;
import com.projectmanagement.multitenantprojectmanagement.roles.dto.request.CreateRoleRequest;
import com.projectmanagement.multitenantprojectmanagement.roles.dto.response.PaginatedRoleResponse;
import com.projectmanagement.multitenantprojectmanagement.roles.dto.response.RoleResponse;
import com.projectmanagement.multitenantprojectmanagement.roles.dto.response.RolesResponse;

public class RoleMapper {

    public static RoleResponse toRoleResponse(Roles role) {
        return RoleResponse.builder()
                .id(role.getId())
                .name(role.getName())
                .auth0Id(role.getAuth0Id())
                .organizationId(role.getOrganizationId())
                .permissions(role.getPermissions().stream().map(Permissions::getName).collect(Collectors.toSet()))
                .build();
    }

    public static List<RolesResponse> toRolesResponse(List<Roles> roles) {
        List<RolesResponse> response = new ArrayList<>();

        for(Roles role: roles) {
            RolesResponse roleResponse = RolesResponse.builder()
                                        .id(role.getId())
                                        .name(role.getName())
                                        .auth0Id(role.getAuth0Id())
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

    public static PaginatedRoleResponse<RolesResponse> toPaginatedRoleResponse(Page<Roles> roles, List<RolesResponse> rolesResponse) {

        return PaginatedRoleResponse.<RolesResponse>builder()
                .data(rolesResponse)
                .page(roles.getNumber())
                .size(roles.getSize())
                .totalElements(roles.getTotalElements())
                .totalPages(roles.getTotalPages())
                .build();
    }

}
