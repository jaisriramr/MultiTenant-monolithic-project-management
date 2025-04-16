package com.projectmanagement.multitenantprojectmanagement.organizationmembers;

import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.projectmanagement.multitenantprojectmanagement.auth0.utils.JWTUtils;
import com.projectmanagement.multitenantprojectmanagement.organizationmembers.dto.request.AssignRoleToUserDto;
import com.projectmanagement.multitenantprojectmanagement.organizationmembers.dto.request.OnBoardRequest;
import com.projectmanagement.multitenantprojectmanagement.organizationmembers.dto.response.ListUsersOfAnOrganizationDto;
import com.projectmanagement.multitenantprojectmanagement.organizationmembers.dto.response.OrganizationMembersResponseDto;
import com.projectmanagement.multitenantprojectmanagement.organizationmembers.dto.response.UserDetailsFromOrganizationMember;
import com.projectmanagement.multitenantprojectmanagement.users.dto.response.PaginatedResponseDto;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class OrganizationMembersController {

    private final OrganizationMembersService organizationMembersService;
    private final JWTUtils jwtUtils;

    @GetMapping("/v1/organization-members")
    public ResponseEntity<PaginatedResponseDto<OrganizationMembersResponseDto>> getAllMembers(Pageable pageable) {
        PaginatedResponseDto<OrganizationMembersResponseDto> members = organizationMembersService.getAllMembers(pageable);
        return ResponseEntity.ok(members);
    }

    @GetMapping("/v1/organization-members/{id}")
    public ResponseEntity<OrganizationMembersResponseDto> getOrganizationMemberById(@PathVariable UUID id) {
        OrganizationMembersResponseDto organizationMember = organizationMembersService.getOrganizationMemberById(id);
        return ResponseEntity.ok(organizationMember);
    }

    @GetMapping("/v1/organization-members/user/{auth0UserId}")
    public ResponseEntity<PaginatedResponseDto<OrganizationMembersResponseDto>> getOrgsWhereUserIsAMember(@PathVariable String auth0UserId, Pageable pageable) {
        PaginatedResponseDto<OrganizationMembersResponseDto> organizationMembers = organizationMembersService.getOrgsWhereUserIsAMember(auth0UserId, pageable);
        return ResponseEntity.ok(organizationMembers);
    }

    @GetMapping("/v1/organization-members/organization/{orgId}")
    public ResponseEntity<PaginatedResponseDto<ListUsersOfAnOrganizationDto>> getAllMembersInAnOrganization(@PathVariable UUID orgId, Pageable pageable) {
        PaginatedResponseDto<ListUsersOfAnOrganizationDto> organizationMembers = organizationMembersService.getAllMembersInAnOrganization(orgId, pageable);
        return ResponseEntity.ok(organizationMembers);
    }

    @GetMapping("/v1/organization-members/organization/{orgId}/user/{userId}")
    public ResponseEntity<UserDetailsFromOrganizationMember> getOrganizationMemberByUserIdandOrgId(@PathVariable String userId, @PathVariable String orgId) {
        UserDetailsFromOrganizationMember organizationMember = organizationMembersService.getSpecificMember("auth0|" + userId, orgId);
        return ResponseEntity.ok(organizationMember);
    }

    @GetMapping("/v1/organization-members/organization/{orgId}/role/{roleId}")
    public ResponseEntity<PaginatedResponseDto<ListUsersOfAnOrganizationDto>> getMembersByRoleInAnOrg(@PathVariable String orgId, @PathVariable String roleId, Pageable pageable) {
        PaginatedResponseDto<ListUsersOfAnOrganizationDto> organizationMembers = organizationMembersService.getMembersByRoleInAnOrg(orgId, roleId, pageable);
        return ResponseEntity.ok(organizationMembers);
    }

    @PostMapping("/v1/organization-member/onboard")
    public ResponseEntity<UserDetailsFromOrganizationMember> onBoardUser(@RequestBody OnBoardRequest onBoardRequest) {
        UserDetailsFromOrganizationMember onBoardedUserDetails = organizationMembersService.onBoardUser(onBoardRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(onBoardedUserDetails);
    }

    @PostMapping("/v1/organization-member/{orgMemberId}/assign/roles")
    public ResponseEntity<String> assignRolesToAnUser(@PathVariable UUID orgMemberId ,@RequestBody AssignRoleToUserDto assignRoleToUserDto) {
        String response = organizationMembersService.assignRolesToAnUser(orgMemberId, assignRoleToUserDto);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/v1/organization-member/{orgMemberId}/remove/roles")
    public ResponseEntity<String> removeRolesToAnUser(@PathVariable UUID orgMemberId ,@RequestBody AssignRoleToUserDto assignRoleToUserDto) {
        String response = organizationMembersService.removeRolesFromAnUser(orgMemberId, assignRoleToUserDto);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/v1/organization-member/{id}")
    public ResponseEntity<String> deleteOrganzationMemberById(@PathVariable UUID id) {
        // removed static from jwtutils
        String subId = jwtUtils.getCurrentUserId();

        String response = organizationMembersService.deleteById(id, subId.replace("auth0|", ""));
        return ResponseEntity.ok(response);
    }

}
