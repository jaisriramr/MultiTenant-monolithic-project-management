package com.projectmanagement.multitenantprojectmanagement.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.projectmanagement.multitenantprojectmanagement.organizations.Organizations;
import com.projectmanagement.multitenantprojectmanagement.roles.Roles;
import com.projectmanagement.multitenantprojectmanagement.roles.RolesController;
import com.projectmanagement.multitenantprojectmanagement.roles.RolesService;
import com.projectmanagement.multitenantprojectmanagement.roles.dto.request.AssignPermissions;
import com.projectmanagement.multitenantprojectmanagement.roles.dto.request.CreateRoleRequest;
import com.projectmanagement.multitenantprojectmanagement.roles.dto.request.UpdateRoleRequest;
import com.projectmanagement.multitenantprojectmanagement.roles.dto.response.PaginatedRoleResponse;
import com.projectmanagement.multitenantprojectmanagement.roles.dto.response.RoleResponse;
import com.projectmanagement.multitenantprojectmanagement.roles.dto.response.RolesResponse;
import com.projectmanagement.multitenantprojectmanagement.roles.mapper.RoleMapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RolesController.class)
@AutoConfigureMockMvc(addFilters = false)
public class RoleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RolesService rolesService;

    @Autowired
    private ObjectMapper objectMapper;

    private UUID roleId;
    private Roles mockRole;
    private RoleResponse roleResponse;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        roleId = UUID.randomUUID();

        mockRole = new Roles();
        mockRole.setId(roleId);
        mockRole.setName("Admin");
        
        Organizations organization = new Organizations();
        organization.setId(UUID.randomUUID());
        organization.setName("Test Organization");
        organization.setAuth0Id("org_123");

        mockRole.setOrganization(organization);

        roleResponse = RoleMapper.toRoleResponse(mockRole);


    }

    @Test
    void testGetRoleById_Success() throws Exception {

        when(rolesService.getRoleById(roleId)).thenReturn(roleResponse);

        mockMvc.perform(get("/api/v1/role/{id}", roleId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(roleId.toString()))
                .andExpect(jsonPath("$.name").value("Admin"));

        verify(rolesService, times(1)).getRoleById(roleId);
    }

    @Test
    void testGetRoleByName_Success() throws Exception {
        RoleResponse response = new RoleResponse();
        response.setId(roleId);
        response.setName("Admin");

        when(rolesService.getRoleByName("Admin")).thenReturn(mockRole);

        mockMvc.perform(get("/api/v1/role/by/name")
                .param("name", "Admin")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(roleId.toString()))
                .andExpect(jsonPath("$.name").value("Admin"));

        verify(rolesService, times(1)).getRoleByName("Admin");
    }

    @Test
    void testCreateRole_Success() throws Exception {
        CreateRoleRequest request = new CreateRoleRequest();
        request.setName("New Role");

        roleResponse.setName("New Role");

        when(rolesService.createRole(any(CreateRoleRequest.class))).thenReturn(roleResponse);

        mockMvc.perform(post("/api/v1/role")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(roleId.toString()))
                .andExpect(jsonPath("$.name").value("New Role"));

        verify(rolesService, times(1)).createRole(any(CreateRoleRequest.class));
    }

    @Test
    void testUpdateRole_Success() throws Exception {
        UpdateRoleRequest request = new UpdateRoleRequest();
        request.setId(roleId);
        request.setName("Updated Role");

        roleResponse.setName("Updated Role");

        when(rolesService.updateRole(any(UpdateRoleRequest.class))).thenReturn(roleResponse);

        mockMvc.perform(put("/api/v1/role")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(roleId.toString()))
                .andExpect(jsonPath("$.name").value("Updated Role"));

        verify(rolesService, times(1)).updateRole(any(UpdateRoleRequest.class));
    }

    @Test
    void testDeleteRoleById_Success() throws Exception {
        String responseMessage = "Role deleted successfully";

        when(rolesService.deleteRoleById(roleId)).thenReturn(responseMessage);

        mockMvc.perform(delete("/api/v1/role/{id}", roleId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(responseMessage));

        verify(rolesService, times(1)).deleteRoleById(roleId);
    }

    @Test
    void testAssignPermissionsToRole_Success() throws Exception {
        AssignPermissions request = new AssignPermissions();
        request.setPermissions(List.of("READ", "WRITE"));

        when(rolesService.assignPermissionsToRole(eq(roleId), anyList())).thenReturn(roleResponse);

        mockMvc.perform(post("/api/v1/role/{roleId}/assign/permissions", roleId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(roleId.toString()))
                .andExpect(jsonPath("$.name").value("Admin"));

        verify(rolesService, times(1)).assignPermissionsToRole(eq(roleId), anyList());
    }

    @Test
    void testRemovePermissionsFromRole_Success() throws Exception {
        AssignPermissions request = new AssignPermissions();
        request.setPermissions(List.of("READ", "WRITE"));

        when(rolesService.removePermissionsFromRole(eq(roleId), anyList())).thenReturn(roleResponse);

        mockMvc.perform(post("/api/v1/role/{roleId}/remove/permissions", roleId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(roleId.toString()))
                .andExpect(jsonPath("$.name").value("Admin"));

        verify(rolesService, times(1)).removePermissionsFromRole(eq(roleId), anyList());
    }
}
