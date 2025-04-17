package com.projectmanagement.multitenantprojectmanagement.roles;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;

import com.projectmanagement.multitenantprojectmanagement.auth0.Auth0Service;
import com.projectmanagement.multitenantprojectmanagement.exception.ConflictException;
import com.projectmanagement.multitenantprojectmanagement.exception.NotFoundException;
import com.projectmanagement.multitenantprojectmanagement.helper.MaskingString;
import com.projectmanagement.multitenantprojectmanagement.permissions.Permissions;
import com.projectmanagement.multitenantprojectmanagement.permissions.PermissionsService;
import com.projectmanagement.multitenantprojectmanagement.roles.dto.request.CreateRoleRequest;
import com.projectmanagement.multitenantprojectmanagement.roles.dto.request.UpdateRoleRequest;
import com.projectmanagement.multitenantprojectmanagement.roles.dto.response.PaginatedRoleResponse;
import com.projectmanagement.multitenantprojectmanagement.roles.dto.response.RoleResponse;
import com.projectmanagement.multitenantprojectmanagement.roles.dto.response.RolesResponse;
import com.projectmanagement.multitenantprojectmanagement.roles.mapper.RoleMapper;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RolesService {

    private final RolesRepository rolesRepository;
    private final Auth0Service auth0Service;
    private final PermissionsService permissionsService;
    private static final Logger logger = LoggerFactory.getLogger(RolesService.class);
    private final MaskingString maskingString;

    private Roles findRoleEntityById(UUID id) {
        logger.info("Getting Role for input ID : {} ", maskingString.maskSensitive(id.toString()));

        Roles role = rolesRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Role not found for ID: " + id));

        logger.debug("Fetched role ID: {}", maskingString.maskSensitive(role.getId().toString()));

        return role;
    }

    public RoleResponse getRoleById(UUID id) {
        Roles role = findRoleEntityById(id);

        return RoleMapper.toRoleResponse(role);
    }

    public PaginatedRoleResponse<RolesResponse> getAllRoles(Pageable pageable) {
        logger.info("Getting all roles: {}", pageable);
        Page<Roles> roles = rolesRepository.findAll(pageable);

        logger.debug("fetched {} roles", roles.getTotalElements());

        List<RolesResponse> rolesReponse = RoleMapper.toRolesResponse(roles.getContent());

        return RoleMapper.toPaginatedRoleResponse(roles, rolesReponse);
    }

    public List<Roles> getRolesByAuth0Ids(List<String> roleIds) {
        logger.info("Getting all roles by auth0Ids for input: {}", maskingString.maskSensitive(roleIds.toString()));

        if (roleIds == null || roleIds.isEmpty()) {
            throw new IllegalArgumentException("roleIds list cannot be null or empty");
        }

        List<Roles> roles = rolesRepository.findAllByAuth0IdIn(roleIds);

        logger.debug("Fetched {} Roles", roles.size());

        return roles;
    }

    public Roles getRoleByName(String name) {
        logger.info("Getting Role By Name: {} ", maskingString.maskSensitive(name));

        if(name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Role name cannot be null or empty");
        }

        Roles role = rolesRepository.findByName(name)
                .orElseThrow(() -> new NotFoundException("Role not found with name: " + name));

        logger.debug("Fetched role ID: {}", maskingString.maskSensitive(role.getId().toString()));

        return role;
    }

    public RoleResponse getRoleByAuth0Id(String auth0Id) {
        logger.info("Getting Role By Auth0Id: {} ", maskingString.maskSensitive(auth0Id));

        Roles role = rolesRepository.findByAuth0Id(auth0Id)
                .orElseThrow(() -> new NotFoundException("Role Not found with auth0Id: " + auth0Id));

        logger.debug("Fetched role ID: {}", maskingString.maskSensitive(role.getId().toString()));

        return RoleMapper.toRoleResponse(role);
    }

    public List<RolesResponse> getRolesByOrgId(String orgId) {
        logger.info("Getting Roles By OrgId: {} ", maskingString.maskSensitive(orgId));
        List<Roles> roles = rolesRepository.findAllByOrganizationId(orgId);

        logger.debug("Fetched {} roles", roles.size());
        if (roles.isEmpty()) {
            throw new NotFoundException("No roles found for the given orgId");
        }

        return RoleMapper.toRolesResponse(roles);
    }

    @Transactional
    public RoleResponse assignPermissionsToRole(UUID id, List<String> permissions) {
        logger.info("Assigning permissions to a role with ID: {} ", maskingString.maskSensitive(id.toString()));
        try {
            Roles role = findRoleEntityById(id);
            logger.debug("Fetched role ID: {}", role.getId());

            List<Permissions> permissionsList = permissionsService.getAllPermissionsByNameList(permissions);

            logger.debug("Fetched {} permissions", permissionsList.size());

            if (permissionsList.isEmpty()) {
                throw new NotFoundException("No permissions found for the given permission names");
            }

            if (role.getPermissions() == null) {
                role.setPermissions(new HashSet<>());
            }

            role.getPermissions().addAll(permissionsList);

            auth0Service.assignOrRemovePermissionToARole(role.getAuth0Id(), permissions);

            Roles savedRole = rolesRepository.save(role);
            logger.debug("Saved role ID: {}", maskingString.maskSensitive(savedRole.getId().toString()));

            return RoleMapper.toRoleResponse(savedRole);
        } catch (NotFoundException e) {
            throw e;
        } 
        catch (HttpClientErrorException | HttpServerErrorException e) {
            logger.error("Error calling Auth0 API for assigning permission to a role: {}", e.getMessage(), e);
            throw new RuntimeException("Error communicating with Auth0 while assigning permission to a role.", e);
        }
        catch (Exception e) {
            logger.error("Internal server error for while assigning permissions to role with ID: {}", maskingString.maskSensitive(id.toString()), e);
            throw new RuntimeException(
                    "Internal server error while trying to assign permissions to a role with ID: " + id, e);
        }
    }

    @Transactional
    public RoleResponse removePermissionsFromRole(UUID id, List<String> permissions) {
        logger.info("Removing permissions from a role with ID: {} ", maskingString.maskSensitive(id.toString()));
        try {
            Roles role = findRoleEntityById(id);

            logger.debug("Fetched role ID: {}", maskingString.maskSensitive(role.getId().toString()));

            List<Permissions> permissionsList = permissionsService.getAllPermissionsByNameList(permissions);
            if (permissionsList.isEmpty()) {
                throw new NotFoundException("No permissions found for the given permission names");
            }
            logger.debug("Fetched {} permissions", permissionsList.size());

            role.getPermissions().removeAll(permissionsList);

            auth0Service.removePermissionFromARole(role.getAuth0Id(), permissions);

            Roles savedRole = rolesRepository.save(role);

            logger.debug("Saved role ID: {}", maskingString.maskSensitive(savedRole.getId().toString()));

            return RoleMapper.toRoleResponse(savedRole);
        } catch (NotFoundException e) {
            throw e;
        }
        catch (HttpClientErrorException | HttpServerErrorException e) {
            logger.error("Error calling Auth0 API for removing permission from role: {}", e.getMessage(), e);
            throw new RuntimeException("Error communicating with Auth0 while removing permission from role.", e);
        }
        catch (Exception e) {
            logger.error("Internal server error for while removing permissions from role with ID: {}", maskingString.maskSensitive(id.toString()), e);
            throw new RuntimeException("Error while trying to remove permissions from a role with ID: " + id, e);
        }
    }

    @Transactional
    public RoleResponse createRole(CreateRoleRequest createRoleRequest) {
        logger.info("Creating Role for the given input: {} ", maskingString.maskSensitive(createRoleRequest.getName()));
        try {
            Roles existingRole = rolesRepository.findByName(createRoleRequest.getName()).orElse(null);

            if(existingRole != null) {
                logger.error("Role with name {} already exists", maskingString.maskSensitive(createRoleRequest.getName()));
                throw new ConflictException("Role with name " + createRoleRequest.getName() + " already exists");
            }

            ResponseEntity<Map<String, Object>> auth0Response = auth0Service.createARole(createRoleRequest.getName(),
                    createRoleRequest.getName());
            Map<String, Object> body = auth0Response.getBody();

            if (body != null) {
                String id = (String) body.get("id");
                logger.debug("Auth0 ID: {}", maskingString.maskSensitive(id));
                Roles role = RoleMapper.toEntityRole(createRoleRequest, id);
                Roles savedRole = rolesRepository.save(role);

                logger.debug("Saved role ID: {}", maskingString.maskSensitive(savedRole.getId().toString()));

                return RoleMapper.toRoleResponse(savedRole);
            } else {
                logger.error("Auth0 response is null or unsuccessful for role creation with name: {}",
                        maskingString.maskSensitive(createRoleRequest.getName()));
                throw new RuntimeException("Internal server Error while trying to create a role");
            }
        }catch(ConflictException e) {
            throw e;
        }
         catch (HttpClientErrorException | HttpServerErrorException e) {
            logger.error("Error calling Auth0 API for role creation: {}", e.getMessage(), e);
            throw new RuntimeException("Error communicating with Auth0 while creating the role.", e);
        } catch (Exception e) {
            logger.error("Internal server error while trying to create a role for the given inputs: {}",
            maskingString.maskSensitive(createRoleRequest.toString()), e);
            throw new RuntimeException("Internal server error while trying to create a role", e);
        }
    }

    @Transactional
    public RoleResponse updateRole(UpdateRoleRequest updateRoleRequest) {
        logger.info("Updating Role for the given name: {} ", maskingString.maskSensitive(updateRoleRequest.getName()));
        try {
            Roles role = findRoleEntityById(updateRoleRequest.getId());
            logger.debug("Fetched role ID: {}", maskingString.maskSensitive(role.getId().toString()));

            role.setName(updateRoleRequest.getName());

            auth0Service.updateARole(role.getAuth0Id(), updateRoleRequest.getName(), updateRoleRequest.getName());

            Roles updateRole = rolesRepository.save(role);
            logger.debug("Updated role ID: {}", maskingString.maskSensitive(updateRole.getId().toString()));

            return RoleMapper.toRoleResponse(updateRole);
        }catch(NotFoundException e) {
            throw e;
        }
         catch (HttpClientErrorException | HttpServerErrorException e) {
            logger.error("Error calling Auth0 API for role updation: {}", e.getMessage(), e);
            throw new RuntimeException(
                    "Error communicating with Auth0 while updating the role with ID: " + updateRoleRequest.getId(), e);
        } catch (Exception e) {
            logger.error("Internal server error for updateRole input: {}", maskingString.maskSensitive(updateRoleRequest.getId().toString()), e);
            throw new RuntimeException("Internal server Error while trying to update a role", e);
        }
    }

    @Transactional
    public String deleteRoleById(UUID id) {
        logger.info("Deleting Role for the given ID: {} ", maskingString.maskSensitive(id.toString()));
        try {
            Roles role = findRoleEntityById(id);
            logger.debug("Fetched role ID: {}", maskingString.maskSensitive(role.getId().toString()));
            auth0Service.removeRoleFromAuth0(role.getAuth0Id());

            rolesRepository.deleteById(id);

            return "Role with Id " + id + " have been removed successfully!";
        }catch(NotFoundException e) {
            throw e;
        }
         catch (HttpClientErrorException | HttpServerErrorException e) {
            logger.error("Error calling Auth0 API for role deletion: {}", e.getMessage(), e);
            throw new RuntimeException("Error communicating with Auth0 while deleting the role with ID: " + id, e);
        } catch (Exception e) {
            logger.error("Internal server error while trying to delete a role with ID: {}", maskingString.maskSensitive(id.toString()), e);
            throw new RuntimeException("Error while trying to delete a role with ID: " + id, e);
        }
    }

}
