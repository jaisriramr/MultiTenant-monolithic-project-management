package com.projectmanagement.multitenantprojectmanagement.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.projectmanagement.multitenantprojectmanagement.auth0.utils.JWTUtils;
import com.projectmanagement.multitenantprojectmanagement.organizationmembers.OrganizationMembers;
import com.projectmanagement.multitenantprojectmanagement.organizationmembers.OrganizationMembersController;
import com.projectmanagement.multitenantprojectmanagement.organizationmembers.OrganizationMembersService;
import com.projectmanagement.multitenantprojectmanagement.organizationmembers.dto.request.AssignRoleToUserDto;
import com.projectmanagement.multitenantprojectmanagement.organizationmembers.dto.request.OnBoardInvitedUserRequest;
import com.projectmanagement.multitenantprojectmanagement.organizationmembers.dto.request.OnBoardRequest;
import com.projectmanagement.multitenantprojectmanagement.organizationmembers.dto.response.ListUsersOfAnOrganizationDto;
import com.projectmanagement.multitenantprojectmanagement.organizationmembers.dto.response.OrganizationMembersResponseDto;
import com.projectmanagement.multitenantprojectmanagement.organizationmembers.dto.response.OrganizationResponseForOrganizationMembersDto;
import com.projectmanagement.multitenantprojectmanagement.organizationmembers.dto.response.UserDetailsFromOrganizationMember;
import com.projectmanagement.multitenantprojectmanagement.organizationmembers.dto.response.UserResponseForOrganizationMembersDto;
import com.projectmanagement.multitenantprojectmanagement.organizationmembers.mapper.OrganizationMembersMapper;
import com.projectmanagement.multitenantprojectmanagement.organizations.Organizations;
import com.projectmanagement.multitenantprojectmanagement.organizations.mapper.OrganizationMapper;
import com.projectmanagement.multitenantprojectmanagement.roles.Roles;
import com.projectmanagement.multitenantprojectmanagement.users.Users;
import com.projectmanagement.multitenantprojectmanagement.users.dto.mapper.UserMapper;
import com.projectmanagement.multitenantprojectmanagement.users.dto.response.PaginatedResponseDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.HashSet;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OrganizationMembersController.class)
@AutoConfigureMockMvc(addFilters = false)
public class OrganizationMemberControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OrganizationMembersService organizationMembersService;
    
    @MockBean
    private JWTUtils jwtUtils; 

    @Autowired
    private ObjectMapper objectMapper;

    private UUID memberId;
    private UUID orgId;
    private OrganizationMembersResponseDto mockOrganizationMembersResponseDto;
    private OrganizationMembers mockOrganizationMembers;
    private UserDetailsFromOrganizationMember mockUserDetailsFromOrganizationMember;
    private PaginatedResponseDto<ListUsersOfAnOrganizationDto> mockPaginatedResponseDto;
    private PaginatedResponseDto<OrganizationMembersResponseDto> mockPaginatedResponseDto2;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        memberId = UUID.randomUUID();
        orgId = UUID.randomUUID();

        Organizations organization = new Organizations();
        organization.setId(orgId);
        organization.setName("Test Organization");

        Roles roles = new Roles();
        roles.setId(UUID.randomUUID());
        roles.setName("Admin");
        roles.setOrganization(organization);
        roles.setPermissions(new HashSet<>());

        mockOrganizationMembers = new OrganizationMembers();
        mockOrganizationMembers.setId(memberId);
        mockOrganizationMembers.setRole(new HashSet<>());
        mockOrganizationMembers.getRole().add(roles);
        
        Users user = new Users();
        user.setId(UUID.randomUUID());
        user.setEmail("test@gmail.com");
        user.setName("Test User");

        mockOrganizationMembers.setOrganization(organization);
        mockOrganizationMembers.setUser(user);

        mockOrganizationMembersResponseDto = OrganizationMembersMapper.toOrganizationMemberResponseDto(mockOrganizationMembers);

        mockUserDetailsFromOrganizationMember = new UserDetailsFromOrganizationMember();
        mockUserDetailsFromOrganizationMember.setId(memberId);
        mockUserDetailsFromOrganizationMember.setUser(UserMapper.toUserReponse(user));
        mockUserDetailsFromOrganizationMember.setOrganization(OrganizationMapper.toOrganizationResponse(organization));
        mockUserDetailsFromOrganizationMember.setRoles(new HashSet<>());
        mockUserDetailsFromOrganizationMember.setPermissions(new HashSet<>());
        mockUserDetailsFromOrganizationMember.setIsDeleted(false);
        mockUserDetailsFromOrganizationMember.setDeletedAt(null);
        mockUserDetailsFromOrganizationMember.setDeletedBy(null);
        mockUserDetailsFromOrganizationMember.setJoinedAt(null);
        mockUserDetailsFromOrganizationMember.setDeletedBy(null);

        mockPaginatedResponseDto = PaginatedResponseDto.<ListUsersOfAnOrganizationDto>builder()
                                    .data(Collections.singletonList(ListUsersOfAnOrganizationDto.builder()
                                            .id(memberId)
                                            .user(UserResponseForOrganizationMembersDto.builder().build())
                                            .organization(OrganizationResponseForOrganizationMembersDto.builder().build())
                                            .roles(new HashSet<>())
                                            .joinedAt(null)
                                            .build()))
                                    .totalElements(1)
                                    .totalPages(1)
                                    .page(0)
                                    .size(10)
                                    .build();

        mockPaginatedResponseDto2 = PaginatedResponseDto.<OrganizationMembersResponseDto>builder()
                                        .data(Collections.singletonList(mockOrganizationMembersResponseDto))
                                        .totalElements(1)
                                        .totalPages(1)
                                        .page(0)
                                        .size(10)
                                        .build();

    }

    @Test
    void testGetOrganizationMemberById_Success() throws Exception {

        when(organizationMembersService.getOrganizationMemberById(memberId)).thenReturn(mockOrganizationMembers);

        mockMvc.perform(get("/api/v1/organization-members/{id}", memberId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(memberId.toString()));

        verify(organizationMembersService, times(1)).getOrganizationMemberById(memberId);
    }

    @Test
    void testGetOrgsWhereUserIsAMember_Success() throws Exception {
        

        when(organizationMembersService.getOrgsWhereUserIsAMember(anyString(), any())).thenReturn(mockPaginatedResponseDto2);

        mockMvc.perform(get("/api/v1/organization-members/user/{auth0UserId}", "auth0|user123")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray());

        verify(organizationMembersService, times(1)).getOrgsWhereUserIsAMember(anyString(), any());
    }

    @Test
    void testGetAllMembersInAnOrganization_Success() throws Exception {

        Pageable pageable = Pageable.unpaged();

        when(organizationMembersService.getAllMembersInAnOrganization(orgId, pageable)).thenReturn(mockPaginatedResponseDto);

        mockMvc.perform(get("/api/v1/organization-members/organization/{orgId}", orgId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
                // .andExpect(jsonPath("$.data").isArray());

    }

    @Test
    void testOnBoardUser_Success() throws Exception {
        OnBoardRequest request = new OnBoardRequest();

        when(organizationMembersService.onBoardUser(any(OnBoardRequest.class))).thenReturn(mockUserDetailsFromOrganizationMember);

        mockMvc.perform(post("/api/v1/organization-member/onboard")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        verify(organizationMembersService, times(1)).onBoardUser(any(OnBoardRequest.class));
    }

    @Test
    void testOnBoardInvitedUser_Success() throws Exception {
        OnBoardInvitedUserRequest request = new OnBoardInvitedUserRequest();

        when(organizationMembersService.onBoardInvitedUser(any(OnBoardInvitedUserRequest.class))).thenReturn(mockUserDetailsFromOrganizationMember);

        mockMvc.perform(post("/api/v1/organization-member/onboard/invitee")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        verify(organizationMembersService, times(1)).onBoardInvitedUser(any(OnBoardInvitedUserRequest.class));
    }

    @Test
    void testAssignRolesToAnUser_Success() throws Exception {
        AssignRoleToUserDto request = new AssignRoleToUserDto();
        String responseMessage = "Roles assigned successfully";

        when(organizationMembersService.assignRolesToAnUser(eq(memberId), any(AssignRoleToUserDto.class))).thenReturn(responseMessage);

        mockMvc.perform(post("/api/v1/organization-member/{orgMemberId}/assign/roles", memberId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string(responseMessage));

        verify(organizationMembersService, times(1)).assignRolesToAnUser(eq(memberId), any(AssignRoleToUserDto.class));
    }

    @Test
    void testRemoveRolesFromAnUser_Success() throws Exception {
        AssignRoleToUserDto request = new AssignRoleToUserDto();
        String responseMessage = "Roles removed successfully";

        when(organizationMembersService.removeRolesFromAnUser(eq(memberId), any(AssignRoleToUserDto.class))).thenReturn(responseMessage);

        mockMvc.perform(post("/api/v1/organization-member/{orgMemberId}/remove/roles", memberId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string(responseMessage));

        verify(organizationMembersService, times(1)).removeRolesFromAnUser(eq(memberId), any(AssignRoleToUserDto.class));
    }

    @Test
    void testDeleteOrganizationMemberById_Success() throws Exception {
        String responseMessage = "Organization member deleted successfully";

        when(jwtUtils.getCurrentUserId()).thenReturn("auth0|123456");

        when(organizationMembersService.deleteById(eq(memberId), anyString())).thenReturn(responseMessage);
        when(organizationMembersService.deleteById(eq(memberId), eq("123456"))).thenReturn(responseMessage);

        mockMvc.perform(delete("/api/v1/organization-member/{id}", memberId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(responseMessage));

        verify(organizationMembersService, times(1)).deleteById(eq(memberId), anyString());
    }
}
