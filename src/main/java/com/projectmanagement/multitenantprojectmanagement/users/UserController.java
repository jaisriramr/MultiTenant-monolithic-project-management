package com.projectmanagement.multitenantprojectmanagement.users;

import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.projectmanagement.multitenantprojectmanagement.users.dto.mapper.UserMapper;
import com.projectmanagement.multitenantprojectmanagement.users.dto.request.CreateUserRequest;
import com.projectmanagement.multitenantprojectmanagement.users.dto.request.UpdateUserRequest;
import com.projectmanagement.multitenantprojectmanagement.users.dto.response.PaginatedResponseDto;
import com.projectmanagement.multitenantprojectmanagement.users.dto.response.UserListResponseDto;
import com.projectmanagement.multitenantprojectmanagement.users.dto.response.UserResponseDto;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    
    @PreAuthorize("@organizationMembersService.hasPermission(#jwt.subject, {\"super:admin\"}, #jwt.claims[\"org_id\"])")
    @GetMapping("/v1/users")
    public ResponseEntity<PaginatedResponseDto<UserListResponseDto>> getAllUsers(Pageable pageable) {
        PaginatedResponseDto<UserListResponseDto> users = userService.getAllUsers(pageable);
        return ResponseEntity.ok(users);
    }

    @PreAuthorize("@organizationMembersService.hasPermission(#jwt.subject, {\"view:user\"}, #jwt.claims[\"org_id\"])")
    @GetMapping("/v1/user/{id}")
    public ResponseEntity<UserResponseDto> getUserById(@Valid @PathVariable UUID id) {
        Users user = userService.getUserEntity(id);
        return ResponseEntity.ok(UserMapper.toUserReponse(user));
    }

    @PreAuthorize("@organizationMembersService.hasPermission(#jwt.subject, {\"view:user\"}, #jwt.claims[\"org_id\"])")
    @GetMapping("/v1/user/by/auth/{id}")
    public ResponseEntity<UserResponseDto> getUserByAuth0Id(@Valid @PathVariable String id) {
        Users user = userService.getUserByAuth0Id("auth0|" + id);
        return ResponseEntity.ok(UserMapper.toUserReponse(user));
    }

    @PostMapping("/v1/user")
    public ResponseEntity<UserResponseDto> createUser(@Valid @RequestBody CreateUserRequest createUserRequest) {
        Users user = userService.createUser(createUserRequest);
        UserResponseDto userDto = UserMapper.toUserReponse(user);
        return ResponseEntity.ok(userDto);
    }

    @PreAuthorize("@organizationMembersService.hasPermission(#jwt.subject, {\"update:user\"}, #jwt.claims[\"org_id\"])")
    @PutMapping("/v1/user")
    public ResponseEntity<UserResponseDto> updateUser(@Valid @RequestBody UpdateUserRequest updateUserRequest) {
        UserResponseDto user = userService.updateUser(updateUserRequest);
        return ResponseEntity.ok(user);
    }

    @PreAuthorize("@organizationMembersService.hasPermission(#jwt.subject, {\"update:user\"}, #jwt.claims[\"org_id\"])")
    @PutMapping("/v1/user/{id}/upload")
    public ResponseEntity<UserResponseDto> uploadImage(@PathVariable UUID id, @Valid @RequestPart("file") MultipartFile file, @RequestParam("type") String type ) {
        UserResponseDto user = userService.uploadProfilePicOrCoverPic(id, file, type);
        return ResponseEntity.ok(user);
    }

    @PreAuthorize("@organizationMembersService.hasPermission(#jwt.subject, {\"delete:user\"}, #jwt.claims[\"org_id\"])")
    @DeleteMapping("/v1/user/{id}")
    public ResponseEntity<String> deleteUserById(@Valid @PathVariable UUID id) {
        String response = userService.deleteUserById(id);
        return ResponseEntity.ok(response);
    }

}
