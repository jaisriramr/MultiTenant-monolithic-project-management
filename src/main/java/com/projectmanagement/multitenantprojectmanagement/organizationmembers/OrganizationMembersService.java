package com.projectmanagement.multitenantprojectmanagement.organizationmembers;

import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException.Forbidden;
import org.springframework.web.client.HttpClientErrorException.Unauthorized;
import org.springframework.web.server.ResponseStatusException;

import com.projectmanagement.multitenantprojectmanagement.auth0.Auth0Service;
import com.projectmanagement.multitenantprojectmanagement.exception.NotFoundException;
import com.projectmanagement.multitenantprojectmanagement.exception.AccessDenied;
import com.projectmanagement.multitenantprojectmanagement.exception.ForbiddenException;
import com.projectmanagement.multitenantprojectmanagement.exception.GlobalExceptionHandler;
import com.projectmanagement.multitenantprojectmanagement.organizationmembers.dto.request.AssignRoleToUserDto;
import com.projectmanagement.multitenantprojectmanagement.organizationmembers.dto.request.OnBoardInvitedUserRequest;
import com.projectmanagement.multitenantprojectmanagement.organizationmembers.dto.request.OnBoardRequest;
import com.projectmanagement.multitenantprojectmanagement.organizationmembers.dto.response.ListUsersOfAnOrganizationDto;
import com.projectmanagement.multitenantprojectmanagement.organizationmembers.dto.response.OrganizationMembersResponseDto;
import com.projectmanagement.multitenantprojectmanagement.organizationmembers.dto.response.UserDetailsFromOrganizationMember;
import com.projectmanagement.multitenantprojectmanagement.organizationmembers.dto.response.UserPermissionsDto;
import com.projectmanagement.multitenantprojectmanagement.organizationmembers.mapper.OrganizationMembersMapper;
import com.projectmanagement.multitenantprojectmanagement.organizations.Organizations;
import com.projectmanagement.multitenantprojectmanagement.organizations.OrganizationsService;
import com.projectmanagement.multitenantprojectmanagement.organizations.dto.request.CreateOrganizationRequest;
import com.projectmanagement.multitenantprojectmanagement.roles.Roles;
import com.projectmanagement.multitenantprojectmanagement.roles.RolesService;
import com.projectmanagement.multitenantprojectmanagement.users.UserService;
import com.projectmanagement.multitenantprojectmanagement.users.Users;
import com.projectmanagement.multitenantprojectmanagement.users.dto.request.CreateUserRequest;
import com.projectmanagement.multitenantprojectmanagement.users.dto.response.PaginatedResponseDto;
import com.projectmanagement.multitenantprojectmanagement.users.dto.response.UserResponseDto;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrganizationMembersService {

    private final OrganizationMembersRepository organizationMembersRepository;
    private final OrganizationsService organizationsService;
    private final UserService userService;
    private final RolesService rolesService;
    private final Auth0Service auth0Service;
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    public boolean hasPermission(String userId, List<String> permission, String orgId) {
        logger.info("Checking permission for the user {} ", userId);
        
        OrganizationMembers organizationMembers = organizationMembersRepository.findByUser_Auth0IdAndOrganization_Auth0Id(userId, orgId).orElseThrow(() -> new ForbiddenException("Forbidden Access"));
        
        logger.debug("Fetched User Details ", organizationMembers.getUser().getId());
        
        UserPermissionsDto userPermissionsDto = OrganizationMembersMapper.toUserPermissions(organizationMembers);
        
        logger.debug("Fetched Permissions to check", userPermissionsDto.getPermissions().toString());
        
        if(userPermissionsDto.getPermissions().containsAll(permission)) {
            return userPermissionsDto.getPermissions().containsAll(permission);
        }else {
            List<String> providedPermissions = new ArrayList<>(userPermissionsDto.getPermissions());
            throw new AccessDenied("Access denied: missing required scope", permission, providedPermissions);
        }
        
    }

    // super admin level or support role
    public PaginatedResponseDto<OrganizationMembersResponseDto> getAllMembers(Pageable pageable) {
        try {
            Page<OrganizationMembers> organizationMembers = organizationMembersRepository.findAll(pageable);

            List<OrganizationMembersResponseDto> orgs = OrganizationMembersMapper
                    .toOrganizationMembersResponseDto(organizationMembers);

            return OrganizationMembersMapper.toPaginatedResponseDto(orgs, organizationMembers);

        } catch (Exception e) {
            throw new RuntimeException("Error while trying to fetch all members", e);
        }
    }

    public OrganizationMembersResponseDto getOrganizationMemberById(UUID id) {
        try {
            OrganizationMembers organizationMember = organizationMembersRepository.findById(id)
                    .orElseThrow(() -> new NotFoundException());

            return OrganizationMembersMapper.toOrganizationMemberResponseDto(organizationMember);
        } catch (Exception e) {
            throw new RuntimeException("Error while trying to fetch organization member with id " + id, e);
        }
    }

    // super admin level or support role
    public PaginatedResponseDto<OrganizationMembersResponseDto> getOrgsWhereUserIsAMember(String userId,
            Pageable pageable) {
        try {
            Page<OrganizationMembers> organizationMembers = organizationMembersRepository
                    .findAllByUser_Auth0Id("auth0|" + userId, pageable);

            List<OrganizationMembersResponseDto> orgs = OrganizationMembersMapper
                    .toOrganizationMembersResponseDto(organizationMembers);

            return OrganizationMembersMapper.toPaginatedResponseDto(orgs, organizationMembers);
        } catch (Exception e) {
            throw new RuntimeException("Error while trying to fetch all organizations with userId {}" + userId, e);
        }
    }

    public PaginatedResponseDto<ListUsersOfAnOrganizationDto> getAllMembersInAnOrganization(UUID orgId,
            Pageable pageable) {
        try {
            Page<OrganizationMembers> organizationMembers = organizationMembersRepository.findAllByOrganizationId(orgId,
                    pageable);

            return OrganizationMembersMapper.toListUsersOfAnOrganizationDto(organizationMembers);
        } catch (Exception e) {
            throw new RuntimeException("Error while trying to fetch all members in an organization with id {}" + orgId,
                    e);
        }
    }

    public PaginatedResponseDto<ListUsersOfAnOrganizationDto> getMembersByRoleInAnOrg(String orgId, String roleId,
            Pageable pageable) {
        try {
            Page<OrganizationMembers> organizationMembers = organizationMembersRepository
                    .findAllByOrganization_Auth0IdAndRole_Auth0Id(orgId, roleId, pageable);

            return OrganizationMembersMapper.toListUsersOfAnOrganizationDto(organizationMembers);
        } catch (Exception e) {
            throw new RuntimeException("Error while trying to fetch all members in an organization with id {}" + orgId,
                    e);
        }
    }

    public UserDetailsFromOrganizationMember getSpecificMember(String auth0UserId, String auth0OrganizationId) {

        OrganizationMembers organizationMembers = organizationMembersRepository
                .findByUser_Auth0IdAndOrganization_Auth0Id(auth0UserId, auth0OrganizationId)
                .orElseThrow(() -> new RuntimeException("Not Found Please Check your credentials"));

        return OrganizationMembersMapper.toSpecificUserOrganizationMember(organizationMembers);
    }

    public UserPermissionsDto getUserPermissions(String auth0UserId, String auth0OrgId) {
        OrganizationMembers organizationMembers = organizationMembersRepository.findByUser_Auth0IdAndOrganization_Auth0Id(auth0UserId, auth0OrgId).orElseThrow(() -> new NotFoundException("Not Found"));

        return OrganizationMembersMapper.toUserPermissions(organizationMembers);
    }

    @Transactional
    public UserDetailsFromOrganizationMember onBoardInvitedUser(OnBoardInvitedUserRequest onBoardInvitedUserRequest) {
        try {

            Users user = userService.getUserByEmail(onBoardInvitedUserRequest.getEmail());

            Organizations organization = organizationsService
                    .getOrganizationByAuth0Id(onBoardInvitedUserRequest.getAuth0OrgId());

            Roles role = rolesService.getRoleByName(onBoardInvitedUserRequest.getRoleName());

            OrganizationMembers organizationMembers = new OrganizationMembers();
            organizationMembers.setOrganization(organization);
            if (organizationMembers.getRole() == null) {
                organizationMembers.setRole(new HashSet<>());
            }
            organizationMembers.getRole().add(role);
            organizationMembers.setJoinedAt(LocalDate.now());

            if (user != null) {
                organizationMembers.setUser(user);
            } else {
                CreateUserRequest newUser = CreateUserRequest.builder()
                        .name(onBoardInvitedUserRequest.getName())
                        .email(onBoardInvitedUserRequest.getEmail())
                        .auth0UserId(onBoardInvitedUserRequest.getAuth0UserId())
                        .build();

                Users savedUser = userService.createUser(newUser);

                organizationMembers.setUser(savedUser);
            }
            auth0Service.addMembersToAnOrg(onBoardInvitedUserRequest.getAuth0OrgId(),
                    List.of(onBoardInvitedUserRequest.getAuth0UserId()));

            auth0Service.assignRolesToUser(onBoardInvitedUserRequest.getAuth0OrgId(),
                    onBoardInvitedUserRequest.getAuth0UserId(), List.of(role.getAuth0Id()));

            OrganizationMembers savedOrgMember = organizationMembersRepository.save(organizationMembers);
            return OrganizationMembersMapper.toSpecificUserOrganizationMember(savedOrgMember);
        } catch (Exception e) {
            throw new RuntimeException(
                    e.getMessage().length() > 0 ? e.getMessage() : "Error while trying to onboard user", e);
        }
    }

    @Transactional
    public UserDetailsFromOrganizationMember onBoardUser(OnBoardRequest onBoardRequest) {
        try {

            CreateOrganizationRequest organizationEntity = OrganizationMembersMapper
                    .toOrganizationEntity(onBoardRequest);
            Organizations savedOrg = organizationsService.createAnOrganization(organizationEntity);

            Roles defaultRole = rolesService.getRoleByName("Admin");

            OrganizationMembers organizationMembers = new OrganizationMembers();

            Users user = userService.getUserByEmail(onBoardRequest.getUserEmail());

            if (user == null) {
                CreateUserRequest userEntity = OrganizationMembersMapper.toUserEntity(onBoardRequest);
                Users savedUser = userService.createUser(userEntity);
                organizationMembers.setUser(savedUser);
            } else {
                organizationMembers.setUser(user);
            }

            organizationMembers.setOrganization(savedOrg);
            if (organizationMembers.getRole() == null) {
                organizationMembers.setRole(new HashSet<>());
            }
            organizationMembers.getRole().add(defaultRole);

            auth0Service.addMembersToAnOrg(savedOrg.getAuth0Id(), List.of(onBoardRequest.getUserAuth0Id()));

            auth0Service.assignRolesToUser(savedOrg.getAuth0Id(), onBoardRequest.getUserAuth0Id(),
                    List.of(defaultRole.getAuth0Id()));

            organizationMembers.setJoinedAt(LocalDate.now());
            OrganizationMembers savedOrganizationMembers = organizationMembersRepository.save(organizationMembers);
            return OrganizationMembersMapper.toSpecificUserOrganizationMember(savedOrganizationMembers);
        } catch (Exception e) {
            throw new RuntimeException("Error while trying to onboard user", e);
        }
    }

    @Transactional
    public String deleteById(UUID id, String subId) {
        try {
            OrganizationMembers organizationMembers = organizationMembersRepository.findById(id)
                    .orElseThrow(() -> new NotFoundException());

            String auth0Id = "auth0|" + subId;

            UserResponseDto user = userService.getUserByAuth0Id(auth0Id);

            organizationMembers.setIsDeleted(true);
            organizationMembers.setDeletedAt(Instant.now());
            organizationMembers.setDeletedBy(user.getId());

            return "Organization Members details with id " + id + " is removed successfully!";
        } catch (Exception e) {
            throw new RuntimeException("Error while trying to delete organiation member with id {}" + id, e);
        }
    }

    @Transactional
    public String assignRolesToAnUser(UUID id, AssignRoleToUserDto assignRoleToUserDto) {
        try {
            OrganizationMembers organizationMember = organizationMembersRepository.findById(id)
                    .orElseThrow(() -> new NotFoundException());

            List<Roles> roles = rolesService.getAllByIds(assignRoleToUserDto.getRoleIds());

            auth0Service.assignRolesToUser(organizationMember.getOrganization().getAuth0Id(),
                    organizationMember.getUser().getAuth0Id(), assignRoleToUserDto.getRoleIds());

            organizationMember.getRole().addAll(roles);

            organizationMembersRepository.save(organizationMember);

            return "Roles has been assigned successfully!";

        } catch (Exception e) {
            throw new RuntimeException("Error while trying to assign roles to an user", e);
        }
    }

    @Transactional
    public String removeRolesFromAnUser(UUID id, AssignRoleToUserDto assignRoleToUserDto) {
        try {
            OrganizationMembers organizationMember = organizationMembersRepository.findById(id)
                    .orElseThrow(() -> new NotFoundException());

            List<Roles> roles = rolesService.getAllByIds(assignRoleToUserDto.getRoleIds());

            auth0Service.removeRolesFromUser(organizationMember.getOrganization().getAuth0Id(),
                    organizationMember.getUser().getAuth0Id(), assignRoleToUserDto.getRoleIds());

            organizationMember.getRole().removeAll(roles);

            organizationMembersRepository.save(organizationMember);

            return "Roles has been removed successfully!";
        } catch (Exception e) {
            throw new RuntimeException("Error while trying to remove roles to an user", e);
        }
    }

}
