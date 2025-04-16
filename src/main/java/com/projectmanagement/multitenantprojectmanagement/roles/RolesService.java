package com.projectmanagement.multitenantprojectmanagement.roles;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.projectmanagement.multitenantprojectmanagement.auth0.Auth0Service;
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

    public RoleResponse getRoleById(UUID id) {
        try {
            Roles role = rolesRepository.findById(id).orElseThrow(() -> new NotFoundException());

            return RoleMapper.toRoleResponse(role);
        }catch(Exception e) {
            throw new RuntimeException("Error while trying to fetch role with id {}" + id);
        }
    }
    
    public PaginatedRoleResponse<RolesResponse> getAllRoles(Pageable pageable) {
        try {
            Page<Roles> roles = rolesRepository.findAll(pageable);

            List<RolesResponse> rolesReponse = RoleMapper.toRolesResponse(roles.getContent());

            return RoleMapper.toPaginatedRoleResponse(roles, rolesReponse);
        }catch(Exception e) {
            throw new RuntimeException("Error while trying to fetch all roles", e);
        }
    }

    public List<Roles> getAllByIds(List<String> roleIds) {
        try {
            return rolesRepository.findAllByAuth0IdIn(roleIds);
        }catch(Exception e) {
            throw new RuntimeException("Error while trying to fetch all the roles by id given",e);
        }
    }

    public Roles getRoleByName(String name) {
        try {
            return rolesRepository.findByName(name).orElseThrow(() -> new NotFoundException());
        }catch(Exception e) {
            throw new RuntimeException("Error while trying to get role by name - " + name, e);
        }
    }

    public RoleResponse getRoleByAuth0Id(String auth0Id) {
        try {
            Roles role = rolesRepository.findByAuth0Id(auth0Id).orElseThrow(() -> new NotFoundException());

            return RoleMapper.toRoleResponse(role);
        }catch(Exception e) {
            throw new RuntimeException("Error while trying to get role by auth0 - " + auth0Id, e);
        }
    }

    @Transactional
    public RoleResponse assignPermissionsToARole(UUID id, List<String> permissions) {
        try {

            Roles role = rolesRepository.findById(id).orElseThrow(() -> new NotFoundException());

            List<Permissions> permissionsList = permissionsService.getAllPermissionsByNameList(permissions);

            if(role.getPermissions() == null) {
                role.setPermissions(new HashSet<>());
            }

            role.getPermissions().addAll(permissionsList);

            auth0Service.assignOrRemovePermissionToARole(role.getAuth0Id(), permissions);

            Roles savedRole = rolesRepository.save(role);

            return RoleMapper.toRoleResponse(savedRole);
        }catch(Exception e) {
            throw new RuntimeException("Error while trying to assign permissions to a role", e);
        }
    }

    @Transactional
    public RoleResponse removePermissionsToARole(UUID id, List<String> permissions) {
        try {
            Roles role = rolesRepository.findById(id).orElseThrow(() -> new NotFoundException());

            List<Permissions> permissionsList = permissionsService.getAllPermissionsByNameList(permissions);

            role.getPermissions().removeAll(permissionsList);

            auth0Service.removePermissionFromARole(role.getAuth0Id(), permissions);

            Roles savedRole = rolesRepository.save(role);

            return RoleMapper.toRoleResponse(savedRole);
        }catch(Exception e) {
            throw new RuntimeException("Error while trying to remove permissions to a role", e);
        }
    }

    @Transactional
    public RoleResponse createRole(CreateRoleRequest createRoleRequest) {
        try {
            ResponseEntity<Map<String, Object>> auth0Response = auth0Service.createARole(createRoleRequest.getName(), createRoleRequest.getName());

            Map<String, Object> body = auth0Response.getBody();

            if(body != null) {
                String id = (String) body.get("id");
                Roles role = RoleMapper.toEntityRole(createRoleRequest, id);
                Roles savedRole = rolesRepository.save(role);
                
                return RoleMapper.toRoleResponse(savedRole);
            }
            else {
                throw new RuntimeException("Error while trying to create a role");    
            }
        }catch(Exception e) {
            throw new RuntimeException("Error while trying to create a role", e);
        }
    }

    @Transactional
    public RoleResponse updateRole(UpdateRoleRequest updateRoleRequest) {
        try {
            Roles role = rolesRepository.findById(updateRoleRequest.getId()).orElseThrow(() -> new NotFoundException());

            role.setName(updateRoleRequest.getName());

            auth0Service.updateARole(role.getAuth0Id(), updateRoleRequest.getName(), updateRoleRequest.getName());

            Roles updateRole = rolesRepository.save(role);

            return RoleMapper.toRoleResponse(updateRole);
        }catch(Exception e) {
            throw new RuntimeException("Error while trying to update a role", e);
        }
    }

    @Transactional
    public String deleteRoleById(UUID id) {
        try {
            Roles role = rolesRepository.findById(id).orElseThrow(() -> new NotFoundException());
            
            auth0Service.removeRoleFromAuth0(role.getAuth0Id());
            
            rolesRepository.deleteById(id);

            return "Role with Id " + id + " have been removed successfully!";
        }catch(Exception e) {
            throw new RuntimeException("Error while trying to delete a role with id {}" + id, e);
        }
    }

}
