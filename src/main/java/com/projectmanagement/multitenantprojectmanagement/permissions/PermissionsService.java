package com.projectmanagement.multitenantprojectmanagement.permissions;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;

import com.projectmanagement.multitenantprojectmanagement.auth0.Auth0Service;
import com.projectmanagement.multitenantprojectmanagement.exception.NotFoundException;
import com.projectmanagement.multitenantprojectmanagement.helper.MaskingString;
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
    private static final Logger logger = LoggerFactory.getLogger(PermissionsService.class);
    private final MaskingString maskingString;

    public PermissionResponse getPermissionById(UUID id) {
        logger.info("Getting permission for the given ID: {} ", maskingString.maskSensitive(id.toString()));

        Permissions permission = permissionsRepository.findById(id).orElseThrow(() -> new NotFoundException("Permission not found for the given Permission Id " + id));

        logger.debug("Fetched permission ID: {} ", maskingString.maskSensitive(permission.getId().toString()));

        return PermissionMapper.toPermissionResponse(permission);
    }

    public List<PermissionsResponse> getAllPermissions() {
        logger.info("Getting all permissions");

        List<Permissions> permissions = permissionsRepository.findAll();

        logger.debug("Fetched {} permissions", permissions.size());

        return PermissionMapper.toPermissionsResponses(permissions);
    }

    public ModulesResponse getAllModules() {
        logger.info("Getting all modules");

        List<Permissions> permissions = permissionsRepository.findAll();

        logger.debug("Fetched {} permissions", permissions.size());

        return PermissionMapper.toPermissionModules(permissions);
    }

    public List<Permissions> getAllPermissionsById(List<UUID> permissionIds) {
        logger.info("Getting all permissions by ID: {} ", maskingString.maskSensitive(permissionIds.toString()));

        List<Permissions> permissions = permissionsRepository.findAllById(permissionIds);

        logger.debug("Fetched {} permissions", permissions.size());

        if (permissions.isEmpty()) {
            logger.error("No permissions found for the given IDs: {}", maskingString.maskSensitive(permissionIds.toString()));
            throw new NotFoundException("No permissions found for the given IDs: " + permissionIds);
        }

        return permissions;
    }

    public List<PermissionsResponse> getAllPermissionsByModule(String module) {
        logger.info("Getting all permissions by module: {} ", maskingString.maskSensitive(module));

        List<Permissions> permissions = permissionsRepository.findByModule(module);

        logger.debug("Fetched {} permissions", permissions.size());

        return PermissionMapper.toPermissionsResponses(permissions);
    }

    public List<Permissions> getAllPermissionsByNameList(List<String> permissionNames) {
        logger.info("Getting all permissions by names: {} ", maskingString.maskSensitive(permissionNames.toString()));
        List<Permissions> permissions = permissionsRepository.findByNameIn(permissionNames);

        logger.debug("Fetched {} permissions", permissions.size());

        return permissions;
    }

    public List<PermissionsResponse> createOrUpdateOrDeletePermissions(List<CreatePermissionRequest> permissions) {
        logger.info("Creating or updating permissions size: {} ", permissions.size());
        try {
            Map<String, Object> permissionRequest = Map.of("scopes", permissions);

            auth0Service.createOrUpdateOrDeletePermission(permissionRequest);

            List<Permissions> permissionList = new ArrayList<>();

            for (CreatePermissionRequest permission : permissions) {
                Permissions perm = new Permissions();
                perm.setName(permission.getValue());
                perm.setDescription(permission.getDescription());
                String module = permission.getValue().split(":")[1];
                perm.setModule(module);

                permissionList.add(perm);
            }

            permissionsRepository.saveAll(permissionList);

            return PermissionMapper.toPermissionsResponses(permissionList);

        } catch (HttpClientErrorException | HttpServerErrorException e) {
            logger.error("Error calling Auth0 API for permissions creation or updation: {}", e.getMessage(), e);
            throw new RuntimeException(
                    "Error communicating with Auth0 while creating or updating permissions", e);
        } catch (Exception e) {
            throw new RuntimeException("Error while trying to create permissions");
        }
    }

    public String removeAllPermissions() {
        logger.info("Removing all permissions");
        try {

            Map<String, Object> permissionRequest = Map.of("scopes", new ArrayList<>());

            auth0Service.createOrUpdateOrDeletePermission(permissionRequest);

            logger.debug("Removed all permissions from Auth0");

            permissionsRepository.deleteAll();

            logger.debug("Removed all permissions from database");

            return "All Permissions are removed";
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            logger.error("Error calling Auth0 API for permissions deletion: {}", e.getMessage(), e);
            throw new RuntimeException(
                    "Error communicating with Auth0 while deleting all permissions", e);
        } catch (Exception e) {
            throw new RuntimeException("Error while trying to delete all permissions");
        }
    }

}
