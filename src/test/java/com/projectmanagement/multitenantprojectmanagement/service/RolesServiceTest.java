package com.projectmanagement.multitenantprojectmanagement.service;

import org.mockito.InjectMocks;
import org.mockito.Mock;

import com.projectmanagement.multitenantprojectmanagement.auth0.Auth0Service;
import com.projectmanagement.multitenantprojectmanagement.auth0.utils.JWTUtils;
import com.projectmanagement.multitenantprojectmanagement.helper.MaskingString;
import com.projectmanagement.multitenantprojectmanagement.organizations.OrganizationsService;
import com.projectmanagement.multitenantprojectmanagement.permissions.PermissionsService;
import com.projectmanagement.multitenantprojectmanagement.roles.RolesRepository;
import com.projectmanagement.multitenantprojectmanagement.roles.RolesService;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import com.projectmanagement.multitenantprojectmanagement.exception.NotFoundException;
import com.projectmanagement.multitenantprojectmanagement.roles.Roles;
import com.projectmanagement.multitenantprojectmanagement.roles.dto.response.RoleResponse;
import com.projectmanagement.multitenantprojectmanagement.roles.dto.response.RolesResponse;
import com.projectmanagement.multitenantprojectmanagement.roles.mapper.RoleMapper;

public class RolesServiceTest {

    @Mock
    private RolesRepository rolesRepository;

    @Mock
    private OrganizationsService organizationsService;

    @Mock
    private Auth0Service auth0Service;

    @Mock
    private PermissionsService permissionsService;

    @Mock
    private MaskingString maskingString;

    @Mock
    private JWTUtils jwtUtils;

    @InjectMocks
    private RolesService rolesService;

    private UUID roleId;
    private Roles mockRole;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        roleId = UUID.fromString("c6e447db-1920-41a8-b728-bf831bf5f096");
        mockRole = new Roles();
        mockRole.setId(roleId);
        mockRole.setName("Test Role");

        when(jwtUtils.getAuth0OrgId()).thenReturn("orgId");
        when(maskingString.maskSensitive(anyString())).thenReturn("masked-string");
    }

    @Test
    void testFindRoleEntityById_Success() {
        UUID roleId = UUID.randomUUID();
        String auth0OrgId = "auth0OrgId";
        Roles role = new Roles();
        role.setId(roleId);

        when(jwtUtils.getAuth0OrgId()).thenReturn(auth0OrgId);
        when(rolesRepository.findByIdAndOrganization_Auth0Id(roleId, auth0OrgId)).thenReturn(Optional.of(role));

        Roles result = rolesService.findRoleEntityById(roleId);

        assertNotNull(result);
        assertEquals(roleId, result.getId());
    }

    @Test
    void testFindRoleEntityById_NotFound() {
        String auth0OrgId = "auth0OrgId";

        when(rolesRepository.findByIdAndOrganization_Auth0Id(roleId, auth0OrgId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> rolesService.findRoleEntityById(roleId));
    }

    @Test
    void testGetRoleById_Success() {
        when(rolesRepository.findByIdAndOrganization_Auth0Id(eq(roleId), eq("orgId"))).thenReturn(Optional.of(mockRole));

        RoleResponse response = rolesService.getRoleById(roleId);

        assertNotNull(response);
        assertEquals(mockRole.getId(), response.getId());
    }

    @Test
    void testGetRoleByName() {
        when(rolesRepository.findByNameAndOrganization_Auth0Id(eq("Test Role"), eq("orgId"))).thenReturn(Optional.of(mockRole));

        Roles response = rolesService.getRoleByName("Test Role");

        assertNotNull(response);
        assertEquals(mockRole.getId(), response.getId());
    }

    @Test
    void testGetRoleByName_IllegalArgumentException() {
        when(rolesRepository.findByNameAndOrganization_Auth0Id(eq(null), eq("orgId"))).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> rolesService.getRoleByName(null));
    }

    @Test
    void testGetRoleByName_NotFoundException() {
        when(rolesRepository.findByNameAndOrganization_Auth0Id(eq("Nonexistent Role"), eq("orgId"))).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> rolesService.getRoleByName("Nonexistent Role"));
    }

    @Test
    void testGetRoleByAuth0Id_Success() {
        when(rolesRepository.findByAuth0IdAndOrganization_Auth0Id(eq("role_123"), eq("orgId"))).thenReturn(Optional.of(mockRole));  

        RoleResponse role = rolesService.getRoleByAuth0Id("role_123");
        assertNotNull(role);
        assertEquals(mockRole.getId(), role.getId());
    }

    @Test
    void testGetRolesByOrgId() {

        when(rolesRepository.findAllByOrganization_Auth0Id(eq("orgId"))).thenReturn(List.of(mockRole));
        List<RolesResponse> roles = rolesService.getRolesByOrgId("orgId");

        assertNotNull(roles);
        assertFalse(roles.isEmpty());
        assertEquals(1, roles.size());
        assertEquals(mockRole.getId(), roles.get(0).getId());

    }

    @Test
    void testAssignPermissionsToRole()  {
        
    }

}
