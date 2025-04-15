package com.projectmanagement.multitenantprojectmanagement.organizationmembers;

import java.time.Instant;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.projectmanagement.multitenantprojectmanagement.auth0.Auth0Service;
import com.projectmanagement.multitenantprojectmanagement.organizationmembers.dto.request.AssignRoleToUserDto;
import com.projectmanagement.multitenantprojectmanagement.organizationmembers.dto.request.OnBoardRequest;
import com.projectmanagement.multitenantprojectmanagement.organizationmembers.dto.response.ListUsersOfAnOrganizationDto;
import com.projectmanagement.multitenantprojectmanagement.organizationmembers.dto.response.OrganizationMembersResponseDto;
import com.projectmanagement.multitenantprojectmanagement.organizationmembers.dto.response.UserDetailsFromOrganizationMember;
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

    // super admin level or support role
    public PaginatedResponseDto<OrganizationMembersResponseDto> getAllMembers(Pageable pageable) {
        try {
            Page<OrganizationMembers> organizationMembers = organizationMembersRepository.findAll(pageable);

            List<OrganizationMembersResponseDto> orgs = OrganizationMembersMapper.toOrganizationMembersResponseDto(organizationMembers);

            return OrganizationMembersMapper.toPaginatedResponseDto(orgs, organizationMembers);

        }catch(Exception e) {
            throw new RuntimeException("Error while trying to fetch all members", e);
        }
    }

    public OrganizationMembersResponseDto getOrganizationMemberById(UUID id) {
        try {
            OrganizationMembers organizationMember = organizationMembersRepository.findById(id).orElseThrow(() -> new NotFoundException());

            return OrganizationMembersMapper.toOrganizationMemberResponseDto(organizationMember);
        }catch(Exception e) {
            throw new RuntimeException("Error while trying to fetch organization member with id " + id, e);
        }
    }

    // super admin level or support role
    public PaginatedResponseDto<OrganizationMembersResponseDto> getOrgsWhereUserIsAMember(String userId, Pageable pageable) {
        try {
            Page<OrganizationMembers> organizationMembers = organizationMembersRepository.findAllByUser_Auth0Id("auth0|" + userId, pageable);

            List<OrganizationMembersResponseDto> orgs = OrganizationMembersMapper.toOrganizationMembersResponseDto(organizationMembers);
            
            return OrganizationMembersMapper.toPaginatedResponseDto(orgs, organizationMembers);
        }catch(Exception e) {
            throw new RuntimeException("Error while trying to fetch all organizations with userId {}" + userId, e);
        }
    }

    public PaginatedResponseDto<ListUsersOfAnOrganizationDto> getAllMembersInAnOrganization(UUID orgId, Pageable pageable) {
        try {
            Page<OrganizationMembers> organizationMembers = organizationMembersRepository.findAllByOrganizationId(orgId, pageable);
            
            return OrganizationMembersMapper.toListUsersOfAnOrganizationDto(organizationMembers);
        }catch(Exception e) {
            throw new RuntimeException("Error while trying to fetch all members in an organization with id {}" + orgId, e);
        }
    }

    public PaginatedResponseDto<ListUsersOfAnOrganizationDto> getMembersByRoleInAnOrg(String orgId, String roleId, Pageable pageable) {
        try {
            Page<OrganizationMembers> organizationMembers = organizationMembersRepository.findAllByOrganization_Auth0IdAndRole_Auth0Id(orgId, roleId, pageable);

            return OrganizationMembersMapper.toListUsersOfAnOrganizationDto(organizationMembers);
        }catch(Exception e) {
            throw new RuntimeException("Error while trying to fetch all members in an organization with id {}" + orgId, e);
        }
    }

    public UserDetailsFromOrganizationMember getSpecificMember(String auth0UserId, String auth0OrganizationId) {
       
        OrganizationMembers organizationMembers = organizationMembersRepository.findByUser_Auth0IdAndOrganization_Auth0Id(auth0UserId, auth0OrganizationId).orElseThrow(() -> new RuntimeException("Not Found Please Check your credentials"));

        return OrganizationMembersMapper.toSpecificUserOrganizationMember(organizationMembers);  
    }

    @Transactional
    public UserDetailsFromOrganizationMember onBoardUser(OnBoardRequest onBoardRequest) {
        try {

            // create user
            CreateUserRequest userEntity = OrganizationMembersMapper.toUserEntity(onBoardRequest);
            Users savedUser = userService.createUser(userEntity);

            // create organization
            CreateOrganizationRequest organizationEntity = OrganizationMembersMapper.toOrganizationEntity(onBoardRequest);
            Organizations savedOrg = organizationsService.createAnOrganization(organizationEntity);

            // get default role
             Roles defaultRole = rolesService.getRoleByName("Admin");

            // map user and organization to organization members and assign default role of admin
            OrganizationMembers organizationMembers = new OrganizationMembers();
            organizationMembers.setUser(savedUser);
            organizationMembers.setOrganization(savedOrg);
            if(organizationMembers.getRole() == null) {
                organizationMembers.setRole(new HashSet<>());
            }
            organizationMembers.getRole().add(defaultRole);

            // assign role to user in auth0
            auth0Service.assignRolesToUser(onBoardRequest.getUserAuth0Id(), List.of(defaultRole.getAuth0Id()));

            organizationMembers.setJoinedAt(LocalDate.now());
            OrganizationMembers savedOrganizationMembers = organizationMembersRepository.save(organizationMembers);
            return OrganizationMembersMapper.toSpecificUserOrganizationMember(savedOrganizationMembers);
        }catch(Exception e) {
            throw new RuntimeException("Error while trying to onboard user",e);
        }
    }

    @Transactional
    public String deleteById(UUID id, String subId) {
        try {
            OrganizationMembers organizationMembers = organizationMembersRepository.findById(id).orElseThrow(() -> new NotFoundException());
            
            String auth0Id = "auth0|" + subId;

            UserResponseDto user = userService.getUserByAuth0Id(auth0Id);

            organizationMembers.setIsDeleted(true);
            organizationMembers.setDeletedAt(Instant.now());
            organizationMembers.setDeletedBy(user.getId());

            return "Organization Members details with id " + id + " is removed successfully!";
        }catch(Exception e) {
            throw new RuntimeException("Error while trying to delete organiation member with id {}" + id,e);
        }
    }

    @Transactional
    public String assignRolesToAnUser(UUID id, AssignRoleToUserDto assignRoleToUserDto) {
        try {
            OrganizationMembers organizationMember = organizationMembersRepository.findById(id).orElseThrow(() -> new NotFoundException());

            List<Roles> roles = rolesService.getAllByIds(assignRoleToUserDto.getRoleIds());

            auth0Service.assignRolesToUser(organizationMember.getUser().getAuth0Id(), assignRoleToUserDto.getRoleIds());

            organizationMember.getRole().addAll(roles);

            organizationMembersRepository.save(organizationMember);

            return "Roles has been assigned successfully!";

        }catch(Exception e) {
            throw new RuntimeException("Error while trying to assign roles to an user", e);
        }
    }

    @Transactional
    public String removeRolesFromAnUser(UUID id, AssignRoleToUserDto assignRoleToUserDto) {
        try {
            OrganizationMembers organizationMember = organizationMembersRepository.findById(id).orElseThrow(() -> new NotFoundException());

            List<Roles> roles = rolesService.getAllByIds(assignRoleToUserDto.getRoleIds());

            auth0Service.removeRolesFromUser(organizationMember.getUser().getAuth0Id(), assignRoleToUserDto.getRoleIds());

            organizationMember.getRole().removeAll(roles);

            organizationMembersRepository.save(organizationMember);

            return "Roles has been removed successfully!";
        }catch(Exception e) {
            throw new RuntimeException("Error while trying to remove roles to an user", e);
        }
    }

}
