package com.projectmanagement.multitenantprojectmanagement.permissions;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.projectmanagement.multitenantprojectmanagement.permissions.dto.request.CreatePermissionRequest;
import com.projectmanagement.multitenantprojectmanagement.permissions.dto.response.PermissionResponse;
import com.projectmanagement.multitenantprojectmanagement.permissions.dto.response.PermissionsResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/permissions")
@RequiredArgsConstructor
public class PermissionsController {

    private final PermissionsService permissionsService;

    @PostMapping("")
    public ResponseEntity<List<PermissionsResponse>> createPermissions(@RequestBody List<CreatePermissionRequest> CreatePermissionRequest) {
        List<PermissionsResponse> response = permissionsService.createOrUpdateOrDeletePermissions(CreatePermissionRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("")
    public ResponseEntity<List<PermissionsResponse>> getAllPermissions() {
        List<PermissionsResponse> response = permissionsService.getAllPermissions();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PermissionResponse> getPermissionById(@PathVariable UUID id) {
        PermissionResponse permission = permissionsService.getPermissionById(id);
        return ResponseEntity.ok(permission);
    }

    @GetMapping("/by/module")
    public ResponseEntity<List<PermissionsResponse>> getAllPermissionsByModule(@RequestParam String module) {
        List<PermissionsResponse> permissions = permissionsService.getAllPermissionsByModule(module);
        return ResponseEntity.ok(permissions);
    }

    @PostMapping("/by/names")
    public ResponseEntity<List<Permissions>> getAllPermissionsByNames(@RequestBody List<String> permissionList) {
        List<Permissions> response = permissionsService.getAllPermissionsByNameList(permissionList);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/all")
    public ResponseEntity<String> removeAllPermissions() {
        String permission = permissionsService.removeAllPermissions();
        return ResponseEntity.ok(permission);
    }
}
