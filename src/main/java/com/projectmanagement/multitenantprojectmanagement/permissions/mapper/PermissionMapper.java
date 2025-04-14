package com.projectmanagement.multitenantprojectmanagement.permissions.mapper;


import java.util.ArrayList;
import java.util.List;

import com.projectmanagement.multitenantprojectmanagement.permissions.Permissions;
import com.projectmanagement.multitenantprojectmanagement.permissions.dto.response.PermissionResponse;
import com.projectmanagement.multitenantprojectmanagement.permissions.dto.response.PermissionsResponse;

public class PermissionMapper {

    public static PermissionResponse toPermissionResponse(Permissions permission){
        return PermissionResponse.builder()
                .id(permission.getId())
                .name(permission.getName())
                .description(permission.getDescription())
                .module(permission.getModule())
                .build();
    }

    public static List<PermissionsResponse> toPermissionsResponses(List<Permissions> permissions) {

        List<PermissionsResponse> response = new ArrayList<>();

        for(Permissions permission: permissions) {

            PermissionsResponse perm = PermissionsResponse.builder()
                                        .id(permission.getId())
                                        .name(permission.getName())
                                        .build();
            response.add(perm);
        }

        return response;
    }

}
