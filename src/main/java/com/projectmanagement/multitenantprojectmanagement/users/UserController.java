package com.projectmanagement.multitenantprojectmanagement.users;

import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    
    @GetMapping("/v1/users")
    public ResponseEntity<PaginatedResponseDto<UserListResponseDto>> getAllUsers(Pageable pageable) {
        PaginatedResponseDto<UserListResponseDto> users = userService.getAllUsers(pageable);
        return ResponseEntity.ok(users);
    }

    @GetMapping("/v1/user/{id}")
    public ResponseEntity<UserResponseDto> getUserById(@Valid @PathVariable UUID id) {
        UserResponseDto user = userService.getUserById(id);
        return ResponseEntity.ok(user);
    }

    @GetMapping("/v1/user/by/auth/{id}")
    public ResponseEntity<UserResponseDto> getUserByAuth0Id(@Valid @PathVariable String id) {
        UserResponseDto user = userService.getUserByAuth0Id("auth0|" + id);
        return ResponseEntity.ok(user);
    }

    @PostMapping("/v1/user")
    public ResponseEntity<UserResponseDto> createUser(@Valid @RequestBody CreateUserRequest createUserRequest) {
        Users user = userService.createUser(createUserRequest);
        UserResponseDto userDto = UserMapper.toUserReponse(user);
        return ResponseEntity.ok(userDto);
    }

    @PutMapping("/v1/user")
    public ResponseEntity<UserResponseDto> updateUser(@Valid @RequestBody UpdateUserRequest updateUserRequest) {
        UserResponseDto user = userService.updateUser(updateUserRequest);
        return ResponseEntity.ok(user);
    }

    @DeleteMapping("/v1/user/{id}")
    public ResponseEntity<String> deleteUserById(@Valid @PathVariable UUID id) {
        String response = userService.deleteUserById(id);
        return ResponseEntity.ok(response);
    }

}
