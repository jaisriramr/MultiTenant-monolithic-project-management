package com.projectmanagement.multitenantprojectmanagement.permissions;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.projectmanagement.multitenantprojectmanagement.auth0.Auth0Service;
import com.projectmanagement.multitenantprojectmanagement.permissions.dto.request.CreatePermissionRequest;
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

    public List<PermissionsResponse> createOrUpdateOrDeletePermissions(List<CreatePermissionRequest> permissions) {
        try {

            Map<String, Object> permissionRequest = Map.of("scopes", permissions);

            ResponseEntity<Map<String, Object>> auth0Response = auth0Service.createOrUpdateOrDeletePermission(permissionRequest);
            
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

}
