package com.projectmanagement.multitenantprojectmanagement.organizationmembers;

import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.projectmanagement.multitenantprojectmanagement.auth0.utils.JWTUtils;
import com.projectmanagement.multitenantprojectmanagement.organizationmembers.dto.request.AssignRoleToUserDto;
import com.projectmanagement.multitenantprojectmanagement.organizationmembers.dto.request.OnBoardInvitedUserRequest;
import com.projectmanagement.multitenantprojectmanagement.organizationmembers.dto.request.OnBoardRequest;
import com.projectmanagement.multitenantprojectmanagement.organizationmembers.dto.response.ListUsersOfAnOrganizationDto;
import com.projectmanagement.multitenantprojectmanagement.organizationmembers.dto.response.OrganizationMembersResponseDto;
import com.projectmanagement.multitenantprojectmanagement.organizationmembers.dto.response.UserDetailsFromOrganizationMember;
import com.projectmanagement.multitenantprojectmanagement.organizationmembers.mapper.OrganizationMembersMapper;
import com.projectmanagement.multitenantprojectmanagement.users.dto.response.PaginatedResponseDto;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class OrganizationMembersController {

    private final OrganizationMembersService organizationMembersService;
    private final JWTUtils jwtUtils;

    @PreAuthorize("@organizationMembersService.hasPermission(#jwt.subject, {\"super:admin\"}, #jwt.claims[\"org_id\"])")
    @GetMapping("/v1/organization-members")
    public ResponseEntity<PaginatedResponseDto<OrganizationMembersResponseDto>> getAllMembers(Pageable pageable, @AuthenticationPrincipal Jwt jwt) {
        PaginatedResponseDto<OrganizationMembersResponseDto> members = organizationMembersService.getAllMembers(pageable);
        return ResponseEntity.ok(members);
    }

    @PreAuthorize("@organizationMembersService.hasPermission(#jwt.subject, {\"view:member\", \"view:organization\"}, #jwt.claims[\"org_id\"])")
    @GetMapping("/v1/organization-members/{id}")
    public ResponseEntity<OrganizationMembersResponseDto> getOrganizationMemberById(@PathVariable UUID id, @AuthenticationPrincipal Jwt jwt) {
        OrganizationMembers organizationMember = organizationMembersService.getOrganizationMemberById(id);
        return ResponseEntity.ok(OrganizationMembersMapper.toOrganizationMemberResponseDto(organizationMember));
    }

    @PreAuthorize("@organizationMembersService.hasPermission(#jwt.subject, {\"view:member\", \"view:organization\"}, #jwt.claims[\"org_id\"])")
    @GetMapping("/v1/organization-members/user/{auth0UserId}")
    public ResponseEntity<PaginatedResponseDto<OrganizationMembersResponseDto>> getOrgsWhereUserIsAMember(@PathVariable String auth0UserId, Pageable pageable, @AuthenticationPrincipal Jwt jwt) {
        PaginatedResponseDto<OrganizationMembersResponseDto> organizationMembers = organizationMembersService.getOrgsWhereUserIsAMember(auth0UserId, pageable);
        return ResponseEntity.ok(organizationMembers);
    }
    
    @PreAuthorize("@organizationMembersService.hasPermission(#jwt.subject, {\"view:member\", \"view:organization\", \"list:members\"}, #jwt.claims[\"org_id\"])")
    @GetMapping("/v1/organization-members/organization/{orgId}")
    public ResponseEntity<PaginatedResponseDto<ListUsersOfAnOrganizationDto>> getAllMembersInAnOrganization(@PathVariable UUID orgId, Pageable pageable, @AuthenticationPrincipal Jwt jwt) {
        PaginatedResponseDto<ListUsersOfAnOrganizationDto> organizationMembers = organizationMembersService.getAllMembersInAnOrganization(orgId, pageable);
        return ResponseEntity.ok(organizationMembers);
    }

    @PreAuthorize("@organizationMembersService.hasPermission(#jwt.subject, {\"view:member\", \"view:organization\"}, #jwt.claims[\"org_id\"])")
    @GetMapping("/v1/organization-members/me")
    public ResponseEntity<UserDetailsFromOrganizationMember> getOrganizationMemberByUserIdandOrgId(@AuthenticationPrincipal Jwt jwt) {

        String auth0UserId = jwtUtils.getCurrentUserId();
        String auth0OrgId = jwtUtils.getAuth0OrgId();

        OrganizationMembers organizationMember = organizationMembersService.getOrganizationMemberbyAuth0UserIdAndAuth0OrgId(auth0UserId, auth0OrgId);



        return ResponseEntity.ok(OrganizationMembersMapper.toSpecificUserOrganizationMember(organizationMember));
    }

    @PreAuthorize("@organizationMembersService.hasPermission(#jwt.subject, {\"view:member\"}, #jwt.claims[\"org_id\"])")
    @GetMapping("/v1/organization-members/organization/{orgId}/role/{roleId}")
    public ResponseEntity<PaginatedResponseDto<ListUsersOfAnOrganizationDto>> getMembersByRoleInAnOrg(@PathVariable String orgId, @PathVariable String roleId, Pageable pageable, @AuthenticationPrincipal Jwt jwt) {
        PaginatedResponseDto<ListUsersOfAnOrganizationDto> organizationMembers = organizationMembersService.getMembersByRoleInAnOrg(orgId, roleId, pageable);
        return ResponseEntity.ok(organizationMembers);
    }

    @PostMapping("/v1/organization-member/onboard")
    public ResponseEntity<UserDetailsFromOrganizationMember> onBoardUser(@RequestBody OnBoardRequest onBoardRequest, @AuthenticationPrincipal Jwt jwt) {
        UserDetailsFromOrganizationMember onBoardedUserDetails = organizationMembersService.onBoardUser(onBoardRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(onBoardedUserDetails);
    }

    @PreAuthorize("@organizationMembersService.hasPermission(#jwt.subject, {\"invite:member\"}, #jwt.claims[\"org_id\"])")
    @PostMapping("/v1/organization-member/onboard/invitee")
    public ResponseEntity<UserDetailsFromOrganizationMember> onBoardInvitedUser(@RequestBody OnBoardInvitedUserRequest onboBoardInvitedUserRequest, @AuthenticationPrincipal Jwt jwt) {
        UserDetailsFromOrganizationMember onBoardedUserDetails = organizationMembersService.onBoardInvitedUser(onboBoardInvitedUserRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(onBoardedUserDetails);
    }

    @PreAuthorize("@organizationMembersService.hasPermission(#jwt.subject, {\"update:member_role\", \"assign:role\"}, #jwt.claims[\"org_id\"])")
    @PostMapping("/v1/organization-member/{orgMemberId}/assign/roles")
    public ResponseEntity<String> assignRolesToAnUser(@PathVariable UUID orgMemberId ,@RequestBody AssignRoleToUserDto assignRoleToUserDto, @AuthenticationPrincipal Jwt jwt) {
        String response = organizationMembersService.assignRolesToAnUser(orgMemberId, assignRoleToUserDto);

        return ResponseEntity.ok(response);
    }
    @PreAuthorize("@organizationMembersService.hasPermission(#jwt.subject, {\"update:member_role\", \"remove:role\"}, #jwt.claims[\"org_id\"])")
    @PostMapping("/v1/organization-member/{orgMemberId}/remove/roles")
    public ResponseEntity<String> removeRolesToAnUser(@PathVariable UUID orgMemberId ,@RequestBody AssignRoleToUserDto assignRoleToUserDto, @AuthenticationPrincipal Jwt jwt) {
        String response = organizationMembersService.removeRolesFromAnUser(orgMemberId, assignRoleToUserDto);

        return ResponseEntity.ok(response);
    }

    @PreAuthorize("@organizationMembersService.hasPermission(#jwt.subject, {\"remove:member\"}, #jwt.claims[\"org_id\"])")
    @DeleteMapping("/v1/organization-member/{id}")
    public ResponseEntity<String> deleteOrganzationMemberById(@PathVariable UUID id, @AuthenticationPrincipal Jwt jwt) {
        
        String subId = jwtUtils.getCurrentUserId();

        String response = organizationMembersService.deleteById(id, subId.replace("auth0|", ""));
        return ResponseEntity.ok(response);
    }

}
