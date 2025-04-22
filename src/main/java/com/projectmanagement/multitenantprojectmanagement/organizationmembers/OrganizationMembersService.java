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
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;

import com.projectmanagement.multitenantprojectmanagement.auth0.Auth0Service;
import com.projectmanagement.multitenantprojectmanagement.auth0.utils.JWTUtils;
import com.projectmanagement.multitenantprojectmanagement.exception.NotFoundException;
import com.projectmanagement.multitenantprojectmanagement.helper.MaskingString;
import com.projectmanagement.multitenantprojectmanagement.exception.AccessDenied;
import com.projectmanagement.multitenantprojectmanagement.exception.BadRequestException;
import com.projectmanagement.multitenantprojectmanagement.exception.ForbiddenException;
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
        private static final Logger logger = LoggerFactory.getLogger(OrganizationMembersService.class);
        private final MaskingString maskingString;
        private final JWTUtils jwtUtils;

        public boolean hasPermission(String userId, List<String> permission, String orgId) {

                logger.info("Checking permission for the user ID: {} ", maskingString.maskSensitive(userId));

                OrganizationMembers organizationMembers = organizationMembersRepository
                                .findByUser_Auth0IdAndOrganization_Auth0Id(userId, orgId)
                                .orElseThrow(() -> new ForbiddenException("Forbidden Access"));

                logger.debug("Fetched User ID: {}",
                                maskingString.maskSensitive(organizationMembers.getUser().getId().toString()));

                UserPermissionsDto userPermissionsDto = OrganizationMembersMapper
                                .toUserPermissions(organizationMembers);

                logger.debug("Fetched {} Permissions", userPermissionsDto.getPermissions().size());

                if (userPermissionsDto.getPermissions().containsAll(permission)) {
                        return true;
                } else {
                        List<String> providedPermissions = new ArrayList<>(userPermissionsDto.getPermissions());
                        throw new AccessDenied("Access denied: missing required scope", permission,
                                        providedPermissions);
                }

        }

        // super admin level or support role
        public PaginatedResponseDto<OrganizationMembersResponseDto> getAllMembers(Pageable pageable) {
                logger.info("Getting all organization members");

                Page<OrganizationMembers> organizationMembers = organizationMembersRepository.findAll(pageable);

                logger.debug("Fetched {} organization members", organizationMembers.getTotalElements());

                List<OrganizationMembersResponseDto> orgs = OrganizationMembersMapper
                                .toOrganizationMembersResponseDto(organizationMembers);

                return OrganizationMembersMapper.toPaginatedResponseDto(orgs, organizationMembers);
        }

        public OrganizationMembersResponseDto getOrganizationMemberById(UUID id) {
                logger.info("Getting organization member for the given ID: {} ",
                                maskingString.maskSensitive(id.toString()));

                OrganizationMembers organizationMember = organizationMembersRepository.findById(id).orElseThrow(
                                () -> new NotFoundException("Organization member not found for the given id " + id));

                logger.debug("Fetched organization member ID: {} ",
                                maskingString.maskSensitive(organizationMember.getId().toString()));

                if (organizationMember.getIsDeleted()) {
                        throw new NotFoundException("Organization member not found for the given id " + id);
                }

                return OrganizationMembersMapper.toOrganizationMemberResponseDto(organizationMember);
        }

        // super admin level or support role
        public PaginatedResponseDto<OrganizationMembersResponseDto> getOrgsWhereUserIsAMember(String userId,
                        Pageable pageable) {
                logger.info("Getting organizations where user ID: {} is a member", maskingString.maskSensitive(userId));
                Page<OrganizationMembers> organizationMembers = organizationMembersRepository
                                .findAllByUser_Auth0Id("auth0|" + userId, pageable);

                logger.debug("Fetched {} organizations for user ID: {}", organizationMembers.getTotalElements(),
                                maskingString.maskSensitive(userId));

                List<OrganizationMembersResponseDto> orgs = OrganizationMembersMapper
                                .toOrganizationMembersResponseDto(organizationMembers);

                return OrganizationMembersMapper.toPaginatedResponseDto(orgs, organizationMembers);
        }

        public PaginatedResponseDto<ListUsersOfAnOrganizationDto> getAllMembersInAnOrganization(UUID orgId,
                        Pageable pageable) {
                logger.info("Getting all members in organization ID: {} ",
                                maskingString.maskSensitive(orgId.toString()));
                Page<OrganizationMembers> organizationMembers = organizationMembersRepository
                                .findAllByOrganizationId(orgId, pageable);
                logger.debug("Fetched {} members in organization ID: {}", organizationMembers.getTotalElements(),
                                maskingString.maskSensitive(orgId.toString()));

                return OrganizationMembersMapper.toListUsersOfAnOrganizationDto(organizationMembers);
        }

        public PaginatedResponseDto<ListUsersOfAnOrganizationDto> getMembersByRoleInAnOrg(String orgId, String roleId,
                        Pageable pageable) {
                logger.info("Getting members by role ID: {} in organization ID: {} ",
                                maskingString.maskSensitive(roleId), maskingString.maskSensitive(orgId));
                Page<OrganizationMembers> organizationMembers = organizationMembersRepository
                                .findAllByOrganization_Auth0IdAndRole_Auth0Id(orgId, roleId, pageable);

                logger.debug("Fetched {} members by role ID: {} in organization ID: {}",
                                organizationMembers.getTotalElements(), maskingString.maskSensitive(roleId),
                                maskingString.maskSensitive(orgId));

                return OrganizationMembersMapper.toListUsersOfAnOrganizationDto(organizationMembers);
        }

        public OrganizationMembers getOrganizationMemberbyAuth0UserIdAndAuth0OrgId(String auth0UserId,
                        String auth0OrganizationId) {
                logger.info("Getting organization member for user ID: {} in organization ID: {} ",
                                maskingString.maskSensitive(auth0UserId),
                                maskingString.maskSensitive(auth0OrganizationId));

                OrganizationMembers organizationMembers = organizationMembersRepository
                                .findByUser_Auth0IdAndOrganization_Auth0Id(auth0UserId, auth0OrganizationId)
                                .orElseThrow(() -> new BadRequestException(
                                                "Please provide valid user id and organization id"));
                logger.debug("Fetched organization member ID: {} ",
                                maskingString.maskSensitive(organizationMembers.getId().toString()));
                return organizationMembers;
        }

        public UserDetailsFromOrganizationMember getSpecificMember() {

                String auth0UserId = jwtUtils.getCurrentUserId();
                String auth0OrganizationId = jwtUtils.getAuth0OrgId();

                logger.info("Getting specific member for user ID: {} in organization ID: {} ",
                                auth0UserId,
                                auth0OrganizationId);

                OrganizationMembers organizationMembers = organizationMembersRepository
                                .findByUser_Auth0IdAndOrganization_Auth0Id(auth0UserId, auth0OrganizationId)
                                .orElseThrow(() -> new BadRequestException(
                                                "Please provide valid user id and organization id"));
                
                System.out.println("OOOOOO " + organizationMembers.toString());
                                                
                logger.debug("Fetched organization member ID: {} ",
                                maskingString.maskSensitive(organizationMembers.getId().toString()));

                if (organizationMembers.getIsDeleted()) {
                        logger.error("User not found for the given user id and organization id");
                        throw new NotFoundException("User not found for the given user id and organization id");
                }

                return OrganizationMembersMapper.toSpecificUserOrganizationMember(organizationMembers);
        }

        public UserPermissionsDto getUserPermissions(String auth0UserId, String auth0OrgId) {
                logger.info("Getting user permissions for user ID: {} in organization ID: {} ",
                                maskingString.maskSensitive(auth0UserId), maskingString.maskSensitive(auth0OrgId));

                OrganizationMembers organizationMembers = organizationMembersRepository
                                .findByUser_Auth0IdAndOrganization_Auth0Id(auth0UserId, auth0OrgId)
                                .orElseThrow(() -> new NotFoundException(
                                                "Users permissions not found for the given user id and organization id"));

                logger.debug("Fetched organization member ID: {} ",
                                maskingString.maskSensitive(organizationMembers.getId().toString()));

                if (organizationMembers.getIsDeleted()) {
                        logger.error("Users permissions not found for the given user id and organization id");
                        throw new NotFoundException(
                                        "Users permissions not found for the given user id and organization id");
                }

                return OrganizationMembersMapper.toUserPermissions(organizationMembers);
        }

        @Transactional
        public UserDetailsFromOrganizationMember onBoardInvitedUser(
                        OnBoardInvitedUserRequest onBoardInvitedUserRequest) {
                logger.info("Onboarding invited user with email: {} and organization ID: {} ",
                                maskingString.maskSensitive(onBoardInvitedUserRequest.getEmail()),
                                maskingString.maskSensitive(onBoardInvitedUserRequest.getAuth0OrgId()));
                try {

                        Users user = userService.getUserByEmail(onBoardInvitedUserRequest.getEmail());

                        Organizations organization = organizationsService
                                        .getOrganizationByAuth0Id(onBoardInvitedUserRequest.getAuth0OrgId());

                        logger.debug("Fetched organization ID: {} ",
                                        maskingString.maskSensitive(organization.getId().toString()));

                        Roles role = rolesService.getRoleByName(onBoardInvitedUserRequest.getRoleName());

                        logger.debug("Fetched role ID: {} ", maskingString.maskSensitive(role.getId().toString()));

                        OrganizationMembers organizationMembers = new OrganizationMembers();
                        organizationMembers.setOrganization(organization);
                        organizationMembers.setRole(new HashSet<>());
                        organizationMembers.getRole().add(role);
                        organizationMembers.setJoinedAt(LocalDate.now());

                        if (user != null) {
                                logger.debug("Fetched user ID: {} ",
                                                maskingString.maskSensitive(user.getId().toString()));
                                user.setIsDeleted(false);
                                user.setDeletedAt(null);
                                user.setDeletedBy(null);
                                user.setUpdatedAt(Instant.now());
                                organizationMembers.setUser(user);
                        } else {
                                CreateUserRequest newUser = CreateUserRequest.builder()
                                                .name(onBoardInvitedUserRequest.getName())
                                                .email(onBoardInvitedUserRequest.getEmail())
                                                .auth0UserId(onBoardInvitedUserRequest.getAuth0UserId()).build();

                                Users savedUser = userService.createUser(newUser);

                                organizationMembers.setUser(savedUser);
                        }
                        auth0Service.addMembersToAnOrg(onBoardInvitedUserRequest.getAuth0OrgId(),
                                        List.of(onBoardInvitedUserRequest.getAuth0UserId()));

                        auth0Service.assignRolesToUser(onBoardInvitedUserRequest.getAuth0OrgId(),
                                        onBoardInvitedUserRequest.getAuth0UserId(), List.of(role.getAuth0Id()));

                        OrganizationMembers savedOrgMember = organizationMembersRepository.save(organizationMembers);

                        logger.debug("Saved organization member ID: {} ",
                                        maskingString.maskSensitive(savedOrgMember.getId().toString()));

                        return OrganizationMembersMapper.toSpecificUserOrganizationMember(savedOrgMember);
                } catch (HttpClientErrorException | HttpServerErrorException e) {
                        logger.error("Error calling Auth0 API for add member: {}", e.getMessage(), e);
                        throw new RuntimeException("Error communicating with Auth0 while adding member to an org.", e);
                } catch (Exception e) {
                        throw new RuntimeException("Error while trying to onboard user", e);
                }
        }

        @Transactional
        public UserDetailsFromOrganizationMember onBoardUser(OnBoardRequest onBoardRequest) {
                logger.info("Onboarding user with email: {} and organization ID: {} ",
                                maskingString.maskSensitive(onBoardRequest.getUserEmail()),
                                maskingString.maskSensitive(onBoardRequest.getOrgName()));
                try {

                        CreateOrganizationRequest organizationEntity = OrganizationMembersMapper
                                        .toOrganizationEntity(onBoardRequest);

                        Organizations savedOrg = organizationsService.createAnOrganization(organizationEntity);
                        logger.debug("Saved organization ID: {} ",
                                        maskingString.maskSensitive(savedOrg.getId().toString()));

                        Roles defaultRole = rolesService.getRoleByName("Admin");
                        logger.debug("Fetched default role ID: {} ",
                                        maskingString.maskSensitive(defaultRole.getId().toString()));

                        OrganizationMembers organizationMembers = new OrganizationMembers();

                        Users user = userService.getUserByEmail(onBoardRequest.getUserEmail());

                        if (user == null) {
                                CreateUserRequest userEntity = OrganizationMembersMapper.toUserEntity(onBoardRequest);
                                Users savedUser = userService.createUser(userEntity);
                                organizationMembers.setUser(savedUser);
                        } else {
                                logger.debug("Fetched user ID: {} ",
                                                maskingString.maskSensitive(user.getId().toString()));
                                organizationMembers.setUser(user);
                        }

                        organizationMembers.setOrganization(savedOrg);
                        organizationMembers.setRole(new HashSet<>());
                        organizationMembers.getRole().add(defaultRole);

                        auth0Service.addMembersToAnOrg(savedOrg.getAuth0Id(), List.of(onBoardRequest.getUserAuth0Id()));

                        auth0Service.assignRolesToUser(savedOrg.getAuth0Id(), onBoardRequest.getUserAuth0Id(),
                                        List.of(defaultRole.getAuth0Id()));

                        organizationMembers.setJoinedAt(LocalDate.now());

                        OrganizationMembers savedOrganizationMembers = organizationMembersRepository
                                        .save(organizationMembers);

                        return OrganizationMembersMapper.toSpecificUserOrganizationMember(savedOrganizationMembers);

                } catch (HttpClientErrorException | HttpServerErrorException e) {
                        logger.error("Error calling Auth0 API for adding member to an org: {}", e.getMessage(), e);
                        throw new RuntimeException("Error communicating with Auth0 while adding member to an org.", e);
                } catch (Exception e) {
                        throw new RuntimeException("Error while trying to onboard user", e);
                }
        }

        @Transactional
        public String deleteById(UUID id, String subId) {
                logger.info("Deleting organization member with ID: {} ", maskingString.maskSensitive(id.toString()));
                try {
                        OrganizationMembers organizationMembers = organizationMembersRepository.findById(id)
                                        .orElseThrow(() -> new NotFoundException(
                                                        "Organization member not found for the given id " + id));

                        logger.debug("Fetched organization member ID: {} ",
                                        maskingString.maskSensitive(organizationMembers.getId().toString()));

                        String auth0Id = "auth0|" + subId;

                        UserResponseDto user = userService.getUserByAuth0Id(auth0Id);

                        logger.debug("Fetched user ID: {} ", maskingString.maskSensitive(user.getId().toString()));

                        organizationMembers.setIsDeleted(true);
                        organizationMembers.setDeletedAt(Instant.now());
                        organizationMembers.setDeletedBy(user.getId());

                        OrganizationMembers deletedOrg = organizationMembersRepository.save(organizationMembers);

                        logger.debug("Deleted organization member ID: {} ",
                                        maskingString.maskSensitive(deletedOrg.getId().toString()));

                        return "Organization Members details with id " + id + " is removed successfully!";
                } catch (NotFoundException e) {
                        logger.error("Organization member not found: {}", e.getMessage(), e);
                        throw e;
                } catch (Exception e) {
                        throw new RuntimeException("Error while trying to delete organiation member with id {}" + id,
                                        e);
                }
        }

        @Transactional
        public String assignRolesToAnUser(UUID id, AssignRoleToUserDto assignRoleToUserDto) {
                logger.info("Assigning roles to user ID: {}", maskingString.maskSensitive(id.toString()));
                try {
                        OrganizationMembers organizationMember = organizationMembersRepository.findById(id)
                                        .orElseThrow(() -> new NotFoundException(
                                                        "Organization member not found for the given id " + id));

                        logger.debug("Fetched organization member ID: {} ",
                                        maskingString.maskSensitive(organizationMember.getId().toString()));

                        List<Roles> roles = rolesService.getRolesByAuth0Ids(assignRoleToUserDto.getRoleIds());

                        logger.debug("Fetched roles ID: {} ",
                                        maskingString.maskSensitive(roles.stream().map(Roles::getId).toString()));

                        auth0Service.assignRolesToUser(organizationMember.getOrganization().getAuth0Id(),
                                        organizationMember.getUser().getAuth0Id(), assignRoleToUserDto.getRoleIds());

                        organizationMember.getRole().addAll(roles);

                        organizationMembersRepository.save(organizationMember);

                        logger.debug("Assigned roles to user ID: {}", maskingString.maskSensitive(id.toString()));

                        return "Roles has been assigned successfully!";

                } catch (NotFoundException e) {
                        logger.error("Organization member not found: {}", e.getMessage(), e);
                        throw e;
                } catch (HttpClientErrorException | HttpServerErrorException e) {
                        logger.error("Error calling Auth0 API for assigning roles to user: {}", e.getMessage(), e);
                        throw new RuntimeException("Error communicating with Auth0 while assigning roles to user.", e);
                } catch (Exception e) {
                        throw new RuntimeException("Error while trying to assign roles to an user", e);
                }
        }

        @Transactional
        public String removeRolesFromAnUser(UUID id, AssignRoleToUserDto assignRoleToUserDto) {
                logger.info("Removing roles from user ID: {}", maskingString.maskSensitive(id.toString()));
                try {
                        OrganizationMembers organizationMember = organizationMembersRepository.findById(id)
                                        .orElseThrow(() -> new NotFoundException(
                                                        "Organization member not found for the given id " + id));

                        logger.debug("Fetched organization member ID: {} ",
                                        maskingString.maskSensitive(organizationMember.getId().toString()));

                        List<Roles> roles = rolesService.getRolesByAuth0Ids(assignRoleToUserDto.getRoleIds());

                        auth0Service.removeRolesFromUser(organizationMember.getOrganization().getAuth0Id(),
                                        organizationMember.getUser().getAuth0Id(), assignRoleToUserDto.getRoleIds());

                        organizationMember.getRole().removeAll(roles);

                        organizationMembersRepository.save(organizationMember);

                        logger.debug("Removed roles from user ID: {}", maskingString.maskSensitive(id.toString()));

                        return "Roles has been removed successfully!";
                } catch (NotFoundException e) {
                        logger.error("Organization member not found: {}", e.getMessage(), e);
                        throw e;
                } catch (HttpClientErrorException | HttpServerErrorException e) {
                        logger.error("Error calling Auth0 API for removing roles from user: {}", e.getMessage(), e);
                        throw new RuntimeException("Error communicating with Auth0 while removing roles from user.", e);
                } catch (Exception e) {
                        throw new RuntimeException("Error while trying to remove roles to an user", e);
                }
        }

}
