package com.projectmanagement.multitenantprojectmanagement.permissions.mapper;


import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.projectmanagement.multitenantprojectmanagement.permissions.Permissions;
import com.projectmanagement.multitenantprojectmanagement.permissions.dto.response.ModulesResponse;
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

    public static ModulesResponse toPermissionModules(List<Permissions> permissions){
        Set<String> modules = new HashSet<>();
        
        for(Permissions permission: permissions) {
            modules.add(permission.getModule());
        }

        List<String> response = new ArrayList<>(modules);

        return ModulesResponse.builder()
                .modules(response)
                .size(response.size())
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
