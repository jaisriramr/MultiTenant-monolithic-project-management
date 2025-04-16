package com.projectmanagement.multitenantprojectmanagement.organizationmembers.mapper;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;

import com.projectmanagement.multitenantprojectmanagement.organizationmembers.OrganizationMembers;
import com.projectmanagement.multitenantprojectmanagement.organizationmembers.dto.request.CreateOrganizationMembersDto;
import com.projectmanagement.multitenantprojectmanagement.organizationmembers.dto.request.OnBoardRequest;
import com.projectmanagement.multitenantprojectmanagement.organizationmembers.dto.response.ListUsersOfAnOrganizationDto;
import com.projectmanagement.multitenantprojectmanagement.organizationmembers.dto.response.OrganizationMembersResponseDto;
import com.projectmanagement.multitenantprojectmanagement.organizationmembers.dto.response.OrganizationMembersRoleDto;
import com.projectmanagement.multitenantprojectmanagement.organizationmembers.dto.response.OrganizationResponseForOrganizationMembersDto;
import com.projectmanagement.multitenantprojectmanagement.organizationmembers.dto.response.UserDetailsFromOrganizationMember;
import com.projectmanagement.multitenantprojectmanagement.organizationmembers.dto.response.UserResponseForOrganizationMembersDto;
import com.projectmanagement.multitenantprojectmanagement.organizations.Organizations;
import com.projectmanagement.multitenantprojectmanagement.organizations.dto.request.CreateOrganizationRequest;
import com.projectmanagement.multitenantprojectmanagement.organizations.dto.response.OrganizationResponse;
import com.projectmanagement.multitenantprojectmanagement.permissions.Permissions;
import com.projectmanagement.multitenantprojectmanagement.roles.Roles;
import com.projectmanagement.multitenantprojectmanagement.users.Users;
import com.projectmanagement.multitenantprojectmanagement.users.dto.request.CreateUserRequest;
import com.projectmanagement.multitenantprojectmanagement.users.dto.response.PaginatedResponseDto;
import com.projectmanagement.multitenantprojectmanagement.users.dto.response.UserResponseDto;

public class OrganizationMembersMapper {

    public static CreateUserRequest toUserEntity(OnBoardRequest onBoardRequest) {
    
        return CreateUserRequest.builder()
                .name(onBoardRequest.getUserName())
                .email(onBoardRequest.getUserEmail())
                .auth0UserId(onBoardRequest.getUserAuth0Id())
                .build();
    }

    public static CreateOrganizationRequest toOrganizationEntity(OnBoardRequest onBoardRequest) {

        return CreateOrganizationRequest.builder()
                .name(onBoardRequest.getOrgName())
                .displayName(onBoardRequest.getOrgDisplayName())
                .domain(onBoardRequest.getOrgDomain())
                .build();
    }

    public static OrganizationMembers toOrganizationMembersEntity(CreateOrganizationMembersDto createOrganizationMembersDto) {

        OrganizationMembers organizationMembers = new OrganizationMembers();
        organizationMembers.setUser(createOrganizationMembersDto.getUser());
        organizationMembers.setOrganization(createOrganizationMembersDto.getOrg());
        // organizationMembers.setRole(createOrganizationMembersDto.getRole());
        organizationMembers.setJoinedAt(LocalDate.now());

        return organizationMembers;
    }

    public static List<OrganizationMembersResponseDto> toOrganizationMembersResponseDto(Page<OrganizationMembers> organizationMembers) {
        List<OrganizationMembersResponseDto> response = new ArrayList<>();

        for (OrganizationMembers organizationMember : organizationMembers) {
            OrganizationMembersResponseDto org = OrganizationMembersResponseDto.builder()
                    .id(organizationMember.getId())
                    .userId(organizationMember.getUser().getId())
                    .userAuth0Id(organizationMember.getUser().getAuth0Id())
                    .organizationId(organizationMember.getOrganization().getId())
                    .organizationName(organizationMember.getOrganization().getName())
                    .orgAuth0Id(organizationMember.getOrganization().getAuth0Id())
                    // .roleId(organizationMember.getRole().getId())
                    .roles(organizationMember.getRole().stream().map(role -> new OrganizationMembersRoleDto(role.getId(), role.getName(), role.getAuth0Id())).collect(Collectors.toSet()))
                    .joinedAt(organizationMember.getJoinedAt())
                    .build();
            response.add(org);
        }

        return response;
    }

    public static UserDetailsFromOrganizationMember toSpecificUserOrganizationMember(OrganizationMembers organizationMembers) {

        UserResponseDto user = UserResponseDto.builder()
                                .id(organizationMembers.getUser().getId())
                                .name(organizationMembers.getUser().getName())
                                .email(organizationMembers.getUser().getEmail())
                                .auth0Id(organizationMembers.getUser().getAuth0Id())
                                .about(organizationMembers.getUser().getAbout())
                                .profilePic(organizationMembers.getUser().getProfilePic())
                                .coverPic(organizationMembers.getUser().getCoverPic())
                                .isActive(organizationMembers.getUser().getIsActive())
                                .build();

        OrganizationResponse organization = OrganizationResponse.builder()
                                            .id(organizationMembers.getOrganization().getId())
                                            .name(organizationMembers.getOrganization().getName())
                                            .displayName(organizationMembers.getOrganization().getDisplayName())
                                            .domain(organizationMembers.getOrganization().getDomain())
                                            .auth0Id(organizationMembers.getOrganization().getAuth0Id())
                                            .build();                        

        UserDetailsFromOrganizationMember response = UserDetailsFromOrganizationMember.builder()
                                                .id(organizationMembers.getId())
                                                .user(user)
                                                .organization(organization)
                                                .roles(organizationMembers.getRole().stream().map(role -> new OrganizationMembersRoleDto(role.getId(), role.getName(), role.getAuth0Id())).collect(Collectors.toSet()))
                                                .permissions(organizationMembers.getRole().stream().flatMap(role -> role.getPermissions().stream()).map(Permissions::getName).collect(Collectors.toSet()))
                                                .isDeleted(organizationMembers.getIsDeleted())
                                                .deletedAt(organizationMembers.getDeletedAt())
                                                .deletedBy(organizationMembers.getDeletedBy())
                                                .joinedAt(organizationMembers.getJoinedAt())
                                                .build();

        return response;
    }

    public static PaginatedResponseDto<ListUsersOfAnOrganizationDto> toListUsersOfAnOrganizationDto(Page<OrganizationMembers> organizationMembers) {

        List<ListUsersOfAnOrganizationDto> response = new ArrayList<>();

        for(OrganizationMembers organizationMember: organizationMembers.getContent()) {

            UserResponseForOrganizationMembersDto user = UserResponseForOrganizationMembersDto.builder()
                                                        .id(organizationMember.getUser().getId())
                                                        .auth0Id(organizationMember.getUser().getAuth0Id())
                                                        .name(organizationMember.getUser().getName())
                                                        .profilePic(organizationMember.getUser().getProfilePic())
                                                        .isActive(organizationMember.getUser().getIsActive())
                                                        .build();

            OrganizationResponseForOrganizationMembersDto org = OrganizationResponseForOrganizationMembersDto.builder()
                                                                .id(organizationMember.getOrganization().getId())
                                                                .name(organizationMember.getOrganization().getName())
                                                                .auth0Id(organizationMember.getOrganization().getAuth0Id())
                                                                .build();
            
            ListUsersOfAnOrganizationDto listUsers = ListUsersOfAnOrganizationDto.builder()
                                                    .id(organizationMember.getId())
                                                    .user(user)
                                                    .organization(org)
                                                    .roles(organizationMember.getRole().stream().map(role -> new OrganizationMembersRoleDto(role.getId(), role.getName(), role.getAuth0Id())).collect(Collectors.toSet()))
                                                    .joinedAt(organizationMember.getJoinedAt())
                                                    .build();

            response.add(listUsers);
        }
        
        return PaginatedResponseDto.<ListUsersOfAnOrganizationDto>builder()
                .data(response)
                .size(organizationMembers.getSize())
                .page(organizationMembers.getNumber())
                .totalElements(organizationMembers.getTotalElements())
                .totalPages(organizationMembers.getTotalPages())
                .build();
    }

    public static OrganizationMembersResponseDto toOrganizationMemberResponseDto(
            OrganizationMembers organizationMember) {
        return OrganizationMembersResponseDto.builder()
                .id(organizationMember.getId())
                .userId(organizationMember.getUser().getId())
                .organizationId(organizationMember.getOrganization().getId())
                .organizationName(organizationMember.getOrganization().getName())
                .roles(organizationMember.getRole().stream().map(role -> new OrganizationMembersRoleDto(role.getId(), role.getName(), role.getAuth0Id())).collect(Collectors.toSet()))
                // .roleId(organizationMember.getRole().getId())
                // .roleName(organizationMember.getRole().getName())
                .joinedAt(organizationMember.getJoinedAt())
                .build();
    }

    public static PaginatedResponseDto<OrganizationMembersResponseDto> toPaginatedResponseDto(
            List<OrganizationMembersResponseDto> orgs, Page<OrganizationMembers> organizationMembers) {
        return PaginatedResponseDto.<OrganizationMembersResponseDto>builder()
                .data(orgs)
                .page(organizationMembers.getNumber())
                .totalElements(organizationMembers.getTotalElements())
                .totalPages(organizationMembers.getTotalPages())
                .build();
    }

}
