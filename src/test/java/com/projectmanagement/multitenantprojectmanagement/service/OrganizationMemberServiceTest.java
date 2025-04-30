package com.projectmanagement.multitenantprojectmanagement.service;

import com.projectmanagement.multitenantprojectmanagement.auth0.Auth0Service;
import com.projectmanagement.multitenantprojectmanagement.auth0.utils.JWTUtils;
import com.projectmanagement.multitenantprojectmanagement.exception.AccessDenied;
import com.projectmanagement.multitenantprojectmanagement.exception.NotFoundException;
import com.projectmanagement.multitenantprojectmanagement.helper.MaskingString;
import com.projectmanagement.multitenantprojectmanagement.organizationmembers.*;
import com.projectmanagement.multitenantprojectmanagement.organizationmembers.dto.response.UserPermissionsDto;
import com.projectmanagement.multitenantprojectmanagement.organizationmembers.mapper.OrganizationMembersMapper;
import com.projectmanagement.multitenantprojectmanagement.organizations.Organizations;
import com.projectmanagement.multitenantprojectmanagement.organizations.OrganizationsService;
import com.projectmanagement.multitenantprojectmanagement.permissions.Permissions;
import com.projectmanagement.multitenantprojectmanagement.roles.Roles;
import com.projectmanagement.multitenantprojectmanagement.roles.RolesService;
import com.projectmanagement.multitenantprojectmanagement.users.UserService;
import com.projectmanagement.multitenantprojectmanagement.users.Users;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class OrganizationMemberServiceTest {

    @Mock
    private OrganizationMembersRepository organizationMembersRepository;

    @Mock
    private OrganizationsService organizationsService;

    @Mock
    private UserService userService;

    @Mock
    private RolesService rolesService;

    @Mock
    private Auth0Service auth0Service;

    @Mock
    private MaskingString maskingString;

    @Mock
    private JWTUtils jwtUtils;

    @Mock
    private OrganizationMembersMapper organizationMembersMapper;

    @InjectMocks
    private OrganizationMembersService organizationMembersService;

    private UUID memberId;
    private OrganizationMembers mockMember;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        Roles roles = new Roles();
        roles.setId(UUID.randomUUID());
        roles.setName("Admin");

        Permissions permission = new Permissions();
        permission.setId(UUID.randomUUID());
        permission.setName("READ");
        permission.setDescription("Read permission");

        roles.setPermissions(new HashSet<>());
        roles.getPermissions().add(permission);

        Permissions permission1 = new Permissions();
        permission1.setId(UUID.randomUUID());
        permission1.setName("WRITE");
        permission1.setDescription("WRITE permission");

        roles.getPermissions().add(permission1);

        Users user = new Users();
        user.setId(UUID.randomUUID());
        user.setAuth0Id("auth0|12345");

        Organizations organization = new Organizations();
        organization.setId(UUID.randomUUID());
        organization.setAuth0Id("auth0|org123");
        organization.setName("Test Organization");

        memberId = UUID.randomUUID();
        mockMember = new OrganizationMembers();
        mockMember.setId(memberId);
        mockMember.setIsDeleted(false);
        mockMember.setRole(new HashSet<>());
        mockMember.getRole().add(roles);
        mockMember.setUser(user);
        mockMember.setOrganization(organization);
    }

    @Test
    void testGetOrganizationMemberById_Success() {
        String authOrgId = "auth0|12345";

        when(jwtUtils.getAuth0OrgId()).thenReturn(authOrgId);
        when(organizationMembersRepository.findByIdAndOrganization_Auth0Id(memberId, authOrgId))
                .thenReturn(Optional.of(mockMember));

        OrganizationMembers result = organizationMembersService.getOrganizationMemberById(memberId);

        assertNotNull(result);
        assertEquals(memberId, result.getId());
        verify(organizationMembersRepository, times(1)).findByIdAndOrganization_Auth0Id(memberId, authOrgId);
    }

    @Test
    void testGetOrganizationMemberById_NotFound() {
        String auth0OrgId = "auth0|12345";

        when(jwtUtils.getAuth0OrgId()).thenReturn(auth0OrgId);
        when(organizationMembersRepository.findByIdAndOrganization_Auth0Id(memberId, auth0OrgId))
                .thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            organizationMembersService.getOrganizationMemberById(memberId);
        });

        assertEquals("Organization member not found for the given id " + memberId, exception.getMessage());
        verify(organizationMembersRepository, times(1)).findByIdAndOrganization_Auth0Id(memberId, auth0OrgId);
    }

    @Test
    void testHasPermission_Success() {
        String userId = "auth0|user123";
        String orgId = "auth0|org123";
        List<String> permissions = List.of("READ", "WRITE");

        when(organizationMembersRepository.findByUser_Auth0IdAndOrganization_Auth0Id(userId, orgId))
                .thenReturn(Optional.of(mockMember));
        when(maskingString.maskSensitive(anyString())).thenReturn("masked");

        UserPermissionsDto userPermissionsDto = new UserPermissionsDto();
        userPermissionsDto.setPermissions(new HashSet<>(permissions));
        
        // when(OrganizationMembersMapper.toUserPermissions(mockMember)).thenReturn(userPermissionsDto);

        boolean result = organizationMembersService.hasPermission(userId, permissions, orgId);

        assertTrue(result);
        verify(organizationMembersRepository, times(1))
                .findByUser_Auth0IdAndOrganization_Auth0Id(userId, orgId);
    }

    @Test
    void testHasPermission_AccessDenied() {
        String userId = "auth0|user123";
        String orgId = "auth0|org123";
        List<String> permissions = List.of("READ1", "WRITE1");

        when(organizationMembersRepository.findByUser_Auth0IdAndOrganization_Auth0Id(userId, orgId))
                .thenReturn(Optional.of(mockMember));
        when(maskingString.maskSensitive(anyString())).thenReturn("masked");
        

        AccessDenied exception = assertThrows(AccessDenied.class, () -> {
            organizationMembersService.hasPermission(userId, permissions, orgId);
        });

        assertEquals("Access denied: missing required scope", exception.getMessage());
        verify(organizationMembersRepository, times(1))
                .findByUser_Auth0IdAndOrganization_Auth0Id(userId, orgId);
    }
}
