package com.projectmanagement.multitenantprojectmanagement.organizationmembers;

import java.util.List;
import java.util.UUID;

import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.projectmanagement.multitenantprojectmanagement.auth0.Auth0Service;
import com.projectmanagement.multitenantprojectmanagement.organizationmembers.dto.response.OrganizationMembersResponseDto;
import com.projectmanagement.multitenantprojectmanagement.organizationmembers.mapper.OrganizationMembersMapper;
import com.projectmanagement.multitenantprojectmanagement.organizations.OrganizationsRepository;
import com.projectmanagement.multitenantprojectmanagement.users.dto.response.PaginatedResponseDto;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrganizationMembersService {

    private final OrganizationMembersRepository organizationMembersRepository;
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


    // super admin level or support role
    public PaginatedResponseDto<OrganizationMembersResponseDto> getAllByUserId(UUID userId, Pageable pageable) {
        try {
            Page<OrganizationMembers> organizationMembers = organizationMembersRepository.findAllByUserId(userId, pageable);

            List<OrganizationMembersResponseDto> orgs = OrganizationMembersMapper.toOrganizationMembersResponseDto(organizationMembers);
            
            return OrganizationMembersMapper.toPaginatedResponseDto(orgs, organizationMembers);
        }catch(Exception e) {
            throw new RuntimeException("Error while trying to fetch all organizations with userId {}" + userId, e);
        }
    }

    public OrganizationMembersResponseDto getCurrentUsersOrganizationMemberDetails(String auth0UserId, String auth0OrganizationId) {
        try {

            OrganizationMembers organizationMembers = organizationMembersRepository.findByUser_Auth0IdAndOrganization_Auth0Id(auth0UserId, auth0OrganizationId).orElseThrow(() -> new NotFoundException());

            return OrganizationMembersMapper.toOrganizationMemberResponseDto(organizationMembers);
        }catch(Exception e) {
            throw new RuntimeException("Error while trying to fetch current user's organization details", e);
        }
    }

}
