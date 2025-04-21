package com.projectmanagement.multitenantprojectmanagement.roles;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.projectmanagement.multitenantprojectmanagement.roles.dto.request.AssignPermissions;
import com.projectmanagement.multitenantprojectmanagement.roles.dto.request.CreateRoleRequest;
import com.projectmanagement.multitenantprojectmanagement.roles.dto.request.UpdateRoleRequest;
import com.projectmanagement.multitenantprojectmanagement.roles.dto.response.PaginatedRoleResponse;
import com.projectmanagement.multitenantprojectmanagement.roles.dto.response.RoleResponse;
import com.projectmanagement.multitenantprojectmanagement.roles.dto.response.RolesResponse;
import com.projectmanagement.multitenantprojectmanagement.roles.mapper.RoleMapper;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class RolesController {

    private final RolesService rolesService;

    @GetMapping("/v1/role/{id}")
    public ResponseEntity<RoleResponse> getRoleById(@PathVariable UUID id){
        RoleResponse role = rolesService.getRoleById(id);
        return ResponseEntity.ok(role);
    }

    @GetMapping("/v1/roles")
    public ResponseEntity<PaginatedRoleResponse<RolesResponse>> getAllRoles(Pageable pageable) {
        PaginatedRoleResponse<RolesResponse> roles = rolesService.getAllRoles(pageable);
        return ResponseEntity.ok(roles);
    }

    @GetMapping("/v1/role/by/name")
    public ResponseEntity<RoleResponse> getRoleByName(@Valid @RequestParam String name) {
        Roles role = rolesService.getRoleByName(name);
        RoleResponse response = RoleMapper.toRoleResponse(role);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/v1/role/auth0Id/{roleId}")
    public ResponseEntity<RoleResponse> getRoleByAuth0Id(@PathVariable String roleId) {
        RoleResponse role = rolesService.getRoleByAuth0Id(roleId);
        return ResponseEntity.ok(role);
    }

    @GetMapping("/v1/roles/by/organization/{id}")
    public ResponseEntity<List<RolesResponse>> getAllRolesByOrgId(@PathVariable String id) {
        List<RolesResponse> roles = rolesService.getRolesByOrgId(id);
        return ResponseEntity.ok(roles);
    }

    @PostMapping("/v1/role")
    public ResponseEntity<RoleResponse> createRole(@Valid @RequestBody CreateRoleRequest createRoleRequest) {
        RoleResponse role = rolesService.createRole(createRoleRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(role);
    }

    @PostMapping("/v1/role/{roleId}/assign/permissions")
    public ResponseEntity<RoleResponse> assignPermissionsToRole(@PathVariable UUID roleId ,@Valid @RequestBody AssignPermissions assignPermissions) {
        RoleResponse role = rolesService.assignPermissionsToRole(roleId, assignPermissions.getPermissions());
        return ResponseEntity.ok(role);
    }
    
    @PostMapping("/v1/role/{roleId}/remove/permissions")
    public ResponseEntity<RoleResponse> removePermissionsFromRole(@PathVariable UUID roleId ,@Valid @RequestBody AssignPermissions assignPermissions) {
        RoleResponse role = rolesService.removePermissionsFromRole(roleId, assignPermissions.getPermissions());
        return ResponseEntity.ok(role);
    }

    @PutMapping("/v1/role")
    public ResponseEntity<RoleResponse> UpdateRole(@Valid @RequestBody UpdateRoleRequest updateRoleRequest) {
        RoleResponse role = rolesService.updateRole(updateRoleRequest);
        return ResponseEntity.ok(role);
    }

    @DeleteMapping("/v1/role/{id}")
    public ResponseEntity<String> deleteRoleById(@PathVariable UUID id) {
        String roleResponse = rolesService.deleteRoleById(id);
        return ResponseEntity.ok(roleResponse);
    }

}
