package com.projectmanagement.multitenantprojectmanagement.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.UUID;

import org.checkerframework.checker.units.qual.g;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.AuditorAware;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.multipart.MultipartFile;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.projectmanagement.multitenantprojectmanagement.auth0.utils.JWTUtils;
import com.projectmanagement.multitenantprojectmanagement.organizations.Organizations;
import com.projectmanagement.multitenantprojectmanagement.users.UserController;
import com.projectmanagement.multitenantprojectmanagement.users.UserService;
import com.projectmanagement.multitenantprojectmanagement.users.Users;
import com.projectmanagement.multitenantprojectmanagement.users.dto.mapper.UserMapper;
import com.projectmanagement.multitenantprojectmanagement.users.dto.request.CreateUserRequest;
import com.projectmanagement.multitenantprojectmanagement.users.dto.request.UpdateUserRequest;
import com.projectmanagement.multitenantprojectmanagement.users.dto.response.UserResponseDto;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private JWTUtils jwtUtils;

    private UUID userId;
    private Users user;
    private UserResponseDto userResponseDto;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        user = new Users();
        user.setId(userId);
        user.setEmail("test@gmail.com");
        user.setName("Test User");
    
        Organizations organization = new Organizations();
        organization.setId(UUID.randomUUID());
        organization.setName("Test Organization");
        organization.setAuth0Id("org_123");

        user.setOrganization(organization);

        userResponseDto = UserMapper.toUserReponse(user);

        when(jwtUtils.getCurrentUserId()).thenReturn("mock-user-id");
        when(jwtUtils.getAuth0OrgId()).thenReturn("orgId");
    }

    @Test
    @WithMockUser
    public void testGetUserById() throws Exception {
        
        Mockito.when(userService.getUserEntity(userId)).thenReturn(user);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/user/{id}", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userId.toString()))
                .andExpect(jsonPath("$.email").value("test@gmail.com"))
                .andExpect(jsonPath("$.name").value("Test User"));
    }

    @Test
    @WithMockUser
    public void testGetUserByAuth0Id() throws Exception {
        
        Mockito.when(userService.getUserByAuth0Id(anyString())).thenReturn(user);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/user/by/auth/{id}", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userId.toString()))
                .andExpect(jsonPath("$.email").value("test@gmail.com"))
                .andExpect(jsonPath("$.name").value("Test User"));
    }

    @Test
    @WithMockUser
    public void testCreateUser() throws Exception {
        
        CreateUserRequest createUserRequest = new CreateUserRequest();
        createUserRequest.setName("Test User");
        createUserRequest.setEmail("test@gmail.com");
        createUserRequest.setAuth0UserId("auth0|123");

        Mockito.when(userService.createUser(any(CreateUserRequest.class))).thenReturn(user);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/user", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(createUserRequest))
                        )
                .andExpect(status().isOk())
                // .andExpect(jsonPath("$.id").value(userId.toString()))
                .andExpect(jsonPath("$.email").value("test@gmail.com"))
                .andExpect(jsonPath("$.name").value("Test User"));
    }

    @Test
    @WithMockUser
    public void testUpdateUser() throws Exception {
        UpdateUserRequest updateUserRequest = new UpdateUserRequest();
        updateUserRequest.setId(userId);
        updateUserRequest.setName("Updated User");

        userResponseDto.setName("Updated User");

        Mockito.when(userService.updateUser(any(UpdateUserRequest.class))).thenReturn(userResponseDto);

        mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(updateUserRequest))
                        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userId.toString()))
                .andExpect(jsonPath("$.name").value("Updated User"));
    }

    @Test
    @WithMockUser
    public void testDeleteUserById() throws Exception {
        Mockito.when(userService.deleteUserById(userId)).thenReturn("User deleted successfully");

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/user/{id}", userId))
                .andExpect(status().isOk())
                .andExpect(content().string("User deleted successfully"));
    }

    @Test
    @WithMockUser
    public void testUploadImage() throws Exception {
        

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "profile.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                "Dummy Image Content".getBytes(StandardCharsets.UTF_8)
        );

        String type = "profile";

        when(userService.uploadProfilePicOrCoverPic(eq(userId), eq(file), eq(type))).thenReturn(userResponseDto);

        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/v1/user/{id}/upload", userId)
                        .file(file)
                        .param("type", type)
                        .with(request -> {
                            request.setMethod("PUT");
                            return request;
                        })
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userId.toString()))
                .andExpect(jsonPath("$.name").value("Test User"));
    }

}
