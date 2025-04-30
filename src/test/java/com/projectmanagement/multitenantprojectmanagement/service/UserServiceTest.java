package com.projectmanagement.multitenantprojectmanagement.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.lang.foreign.Linker.Option;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.context.annotation.Description;
import org.springframework.test.context.ActiveProfiles;

import com.projectmanagement.multitenantprojectmanagement.auth0.utils.JWTUtils;
import com.projectmanagement.multitenantprojectmanagement.helper.MaskingString;
import com.projectmanagement.multitenantprojectmanagement.organizationmembers.OrganizationMembers;
import com.projectmanagement.multitenantprojectmanagement.organizationmembers.OrganizationMembersRepository;
import com.projectmanagement.multitenantprojectmanagement.organizations.Organizations;
import com.projectmanagement.multitenantprojectmanagement.organizations.OrganizationsRepository;
import com.projectmanagement.multitenantprojectmanagement.organizations.OrganizationsService;
import com.projectmanagement.multitenantprojectmanagement.s3.s3Service;
import com.projectmanagement.multitenantprojectmanagement.users.UserRepository;
import com.projectmanagement.multitenantprojectmanagement.users.UserService;
import com.projectmanagement.multitenantprojectmanagement.users.Users;
import com.projectmanagement.multitenantprojectmanagement.users.dto.request.CreateUserRequest;
import com.projectmanagement.multitenantprojectmanagement.users.dto.request.UpdateUserRequest;
import com.projectmanagement.multitenantprojectmanagement.users.dto.response.UserListResponseDto;
import com.projectmanagement.multitenantprojectmanagement.users.dto.response.UserOrganizations;
import com.projectmanagement.multitenantprojectmanagement.users.dto.response.UserResponseDto;
import com.projectmanagement.multitenantprojectmanagement.users.embeddable.About;

@ActiveProfiles("test")
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private OrganizationMembersRepository organizationMembersRepository;

    @Mock 
    private OrganizationsRepository organizationsRepository;

    @Mock
    private MaskingString maskingString;

    @Mock
    private JWTUtils jwtUtils;

    @Mock
    private OrganizationsService organizationsService;

    @InjectMocks
    private UserService userService;

    @Mock
    private s3Service s3Service;

    private UUID userId;
    private Users mockUser;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);

        // Set up mock data
        userId = UUID.fromString("c6e447db-1920-41a8-b728-bf831bf5f096");
        mockUser = new Users();
        mockUser.setId(userId);
        mockUser.setName("Test User");
        mockUser.setEmail("test@gmail.com");
        mockUser.setAuth0Id("auth0|something");
        mockUser.setIsDeleted(false);

        when(jwtUtils.getAuth0OrgId()).thenReturn("orgId");
        when(maskingString.maskSensitive(anyString())).thenReturn("masked-string");
    }

    @Test
    @Description("Get User detail")
    public void testGetUserById() {
        // Mock the behavior of dependencies
        when(userRepository.findByIdAndOrganization_Auth0Id(eq(userId), eq("orgId"))).thenReturn(Optional.of(mockUser));

        // Call the method
        Users result = userService.getUserEntity(userId);

        // Assertions
        assertNotNull(result);
        assertEquals(mockUser.getId(), result.getId());
    }

    @Test
    @Description("Test Get User Orgs")
    public void testGetUserOrganization() {
        Organizations organization = new Organizations();
        organization.setName("Org");
        organization.setDisplayName("org");
        organization.setAuth0Id("org_something");

        when(userRepository.findByIdAndOrganization_Auth0Id(eq(userId), eq("orgId"))).thenReturn(Optional.of(mockUser));
        when(organizationMembersRepository.findOrganizationsByUserId(userId)).thenReturn(List.of(organization));

        List<UserOrganizations> result = userService.getUserOrganizations(userId);

        assertNotNull(result);
        assertEquals(organization.getId(), result.getFirst().getId());
    }

    @Test
    @Description("Test search user by name")
    public void testSearchUserByName() {

        List<Users> mockUsers = List.of(mockUser);

        when(userRepository.findAllByNameContainingIgnoreCaseAndIsDeletedFalseAndOrganization_Auth0Id("test","orgId")).thenReturn(Optional.of(mockUsers));

        List<UserListResponseDto> users = userService.searchUsersByName("test");

        assertNotNull(users);
        assertEquals(mockUsers.getFirst().getId(), users.getFirst().getId());
    }

    @Test
    public void testGetUserByEmail() {

        when(userRepository.findByEmailAndOrganization_Auth0Id("test@gmail.com", "orgId")).thenReturn(Optional.of(mockUser));

        Users user = userService.getUserByEmail("test@gmail.com");

        assertNotNull(user);
        assertEquals(user.getId(), mockUser.getId());
    }

    @Test
    public void testGetUserByAuth0Id() {
        when(userRepository.findByAuth0IdAndOrganization_Auth0Id("auth0|something", "orgId")).thenReturn(Optional.of(mockUser));

        Users user = userService.getUserByAuth0Id("auth0|something");

        assertNotNull(user);
        assertEquals(user.getId(), mockUser.getId());
    }

    @Test
    public void testCreateUser() {

        CreateUserRequest createUserRequest = CreateUserRequest.builder()
                                                .auth0UserId("auth0|something")
                                                .email("test@gmail.com")
                                                .name("test")
                                                .build();

        Organizations organization = new Organizations();
        organization.setName("Org");
        organization.setDisplayName("org");
        organization.setAuth0Id("orgId");

        when(jwtUtils.getAuth0OrgId()).thenReturn("orgId");

        when(organizationsRepository.findByAuth0Id("orgId")).thenReturn(Optional.of(organization));
        
        when(userRepository.findByEmailAndOrganization_Auth0Id("test@gmail.com", "orgId")).thenReturn(Optional.empty());

        when(userRepository.findByAuth0IdAndOrganization_Auth0Id("auth0|something", "orgId")).thenReturn(Optional.empty());

        when(userRepository.save(any(Users.class))).thenReturn(mockUser);

        Users user = userService.createUser(createUserRequest);

        assertNotNull(user, "User should not be null");
        assertEquals(user.getId(), mockUser.getId(), "User ID should match the mock user ID");

    }

    @Test
    public void TestUpdateUser() {

        UpdateUserRequest updateUserRequest = new UpdateUserRequest();
        updateUserRequest.setId(userId);
        updateUserRequest.setName("Name");
        
        About about = new About();
        about.setCompanyName("slack");
        about.setDepartment("Engineering");
        about.setJobTitle("SDE");
        about.setLocation("BLR");

        updateUserRequest.setAbout(about);

        when(userRepository.findByIdAndOrganization_Auth0Id(userId, "orgId")).thenReturn(Optional.of(mockUser));
        when(userRepository.save(any(Users.class))).thenReturn(mockUser);

        UserResponseDto users = userService.updateUser(updateUserRequest);
        
        assertNotNull(users);
        assertEquals(mockUser.getId(), users.getId());

    }

    @Test
    public void TestDeleteUser() {
        when(userRepository.findByIdAndOrganization_Auth0Id(userId, "orgId")).thenReturn(Optional.of(mockUser));
        when(userRepository.save(any(Users.class))).thenReturn(mockUser);

        String response = userService.deleteUserById(userId);

        assertNotNull(response);
        assertEquals(response, "User with ID " + userId + " has be removed successfully!");

    }

}
