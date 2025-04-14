package com.projectmanagement.multitenantprojectmanagement.roles;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.projectmanagement.multitenantprojectmanagement.auth0.Auth0Service;
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

    public Roles getRoleByName(String name) {
        try {
            return rolesRepository.findByName(name).orElseThrow(() -> new NotFoundException());
        }catch(Exception e) {
            throw new RuntimeException("Error while trying to get role by name - " + name, e);
        }
    }

    @Transactional
    public RoleResponse createRole(CreateRoleRequest createRoleRequest) {
        try {
            ResponseEntity<Map<String, Object>> auth0Response = auth0Service.createARole(createRoleRequest.getName(), createRoleRequest.getName());

            Roles role = RoleMapper.toEntityRole(createRoleRequest, auth0Response.getBody().get("id").toString());

            Roles savedRole = rolesRepository.save(role);

            return RoleMapper.toRoleResponse(savedRole);
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
