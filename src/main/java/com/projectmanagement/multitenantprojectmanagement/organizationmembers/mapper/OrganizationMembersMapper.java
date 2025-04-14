package com.projectmanagement.multitenantprojectmanagement.organizationmembers.mapper;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;

import com.projectmanagement.multitenantprojectmanagement.organizationmembers.OrganizationMembers;
import com.projectmanagement.multitenantprojectmanagement.organizationmembers.dto.response.OrganizationMembersResponseDto;
import com.projectmanagement.multitenantprojectmanagement.users.dto.response.PaginatedResponseDto;

public class OrganizationMembersMapper {

    public static List<OrganizationMembersResponseDto> toOrganizationMembersResponseDto(
            Page<OrganizationMembers> organizationMembers) {
        List<OrganizationMembersResponseDto> response = new ArrayList<>();

        for (OrganizationMembers organizationMember : organizationMembers) {
            OrganizationMembersResponseDto org = OrganizationMembersResponseDto.builder()
                    .id(organizationMember.getId())
                    .userId(organizationMember.getUser().getId())
                    .organizationId(organizationMember.getOrganization().getId())
                    .organizationName(organizationMember.getOrganization().getName())
                    .roleId(organizationMember.getRole().getId())
                    .roleName(organizationMember.getRole().getName())
                    .joinedAt(organizationMember.getJoinedAt())
                    .build();
            response.add(org);
        }

        return response;
    }

    public static OrganizationMembersResponseDto toOrganizationMemberResponseDto(
            OrganizationMembers organizationMember) {
        return OrganizationMembersResponseDto.builder()
                .id(organizationMember.getId())
                .userId(organizationMember.getUser().getId())
                .organizationId(organizationMember.getOrganization().getId())
                .organizationName(organizationMember.getOrganization().getName())
                .roleId(organizationMember.getRole().getId())
                .roleName(organizationMember.getRole().getName())
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
