package com.projectmanagement.multitenantprojectmanagement.permissions;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.projectmanagement.multitenantprojectmanagement.auth0.Auth0Service;
import com.projectmanagement.multitenantprojectmanagement.exception.NotFoundException;
import com.projectmanagement.multitenantprojectmanagement.permissions.dto.request.CreatePermissionRequest;
import com.projectmanagement.multitenantprojectmanagement.permissions.dto.response.ModulesResponse;
import com.projectmanagement.multitenantprojectmanagement.permissions.dto.response.PermissionResponse;
import com.projectmanagement.multitenantprojectmanagement.permissions.dto.response.PermissionsResponse;
import com.projectmanagement.multitenantprojectmanagement.permissions.mapper.PermissionMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PermissionsService {

    private final PermissionsRepository permissionsRepository;
    private final Auth0Service auth0Service;
    

    public PermissionResponse getPermissionById(UUID id) {
        try {
            Permissions permission = permissionsRepository.findById(id).orElseThrow(() -> new NotFoundException());

            return PermissionMapper.toPermissionResponse(permission);
        }catch(Exception e) {
            throw new RuntimeException("Error while trying to fetch permission with id {}" + id);
        }
    }

    public List<PermissionsResponse> getAllPermissions() {
        try {
            List<Permissions> permissions = permissionsRepository.findAll();
            return PermissionMapper.toPermissionsResponses(permissions);
        }catch(Exception e) {
            throw new RuntimeException("Error while trying to fetch all permissions");
        }
    }

    public ModulesResponse getAllModules() {
        try {
            List<Permissions> permissions = permissionsRepository.findAll();

            return PermissionMapper.toPermissionModules(permissions);
        }catch(Exception e) {
            throw new RuntimeException("Error while getting all modules", e);
        }
    }

    public List<Permissions> getAllPermissionsById(List<UUID> permissionIds) {
        try {
            List<Permissions> permissions = permissionsRepository.findAllById(permissionIds);
            return permissions;
        }catch(Exception e) {
            throw new RuntimeException("Error while trying to fetch all permissions by list of ids");
        }
    }

    public List<PermissionsResponse> getAllPermissionsByModule(String module) {
        try {
            List<Permissions> permissions = permissionsRepository.findByModule(module);
            return PermissionMapper.toPermissionsResponses(permissions);
        }catch(Exception e) {
            throw new RuntimeException("Error while trying to fetch all permissions by module"); 
        }
    }

    public List<Permissions> getAllPermissionsByNameList(List<String> permissionNames) {
        try {
            List<Permissions> permissions = permissionsRepository.findByNameIn(permissionNames);
            return permissions;
        }catch(Exception e) {
            throw new RuntimeException("Error while trying to fetch all permissions by list of names");
        }
    }

    public List<PermissionsResponse> createOrUpdateOrDeletePermissions(List<CreatePermissionRequest> permissions) {
        try {

            Map<String, Object> permissionRequest = Map.of("scopes", permissions);

            auth0Service.createOrUpdateOrDeletePermission(permissionRequest);
            
            List<Permissions> permissionList = new ArrayList<>();

            for(CreatePermissionRequest permission: permissions) {
                Permissions perm = new Permissions();
                perm.setName(permission.getValue());
                perm.setDescription(permission.getDescription());
                String module = permission.getValue().split(":")[1];
                perm.setModule(module);

                permissionList.add(perm);
            }

            permissionsRepository.saveAll(permissionList);

            return PermissionMapper.toPermissionsResponses(permissionList);

        }catch(Exception e) {
            throw new RuntimeException("Error while trying to create permissions");
        }
    }

    public String removeAllPermissions() {
        try {
            permissionsRepository.deleteAll();
            return "All Permissions are removed";
        }catch(Exception e){
            throw new RuntimeException("Error while trying to delete all permissions");
        }
    }

}
