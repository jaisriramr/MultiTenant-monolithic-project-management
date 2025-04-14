package com.projectmanagement.multitenantprojectmanagement.permissions;

import java.util.UUID;

import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.stereotype.Service;

import com.projectmanagement.multitenantprojectmanagement.auth0.Auth0Service;
import com.projectmanagement.multitenantprojectmanagement.permissions.dto.response.PermissionResponse;
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

}
