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

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.projectmanagement.multitenantprojectmanagement.exception.NotFoundException;
import com.projectmanagement.multitenantprojectmanagement.roles.Roles;
import com.projectmanagement.multitenantprojectmanagement.roles.dto.response.RoleResponse;
import com.projectmanagement.multitenantprojectmanagement.roles.mapper.RoleMapper;

@ExtendWith(MockitoExtension.class)
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


}
