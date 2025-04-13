package com.projectmanagement.multitenantprojectmanagement.users.dto.mapper;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;

import com.projectmanagement.multitenantprojectmanagement.organizations.Organizations;
import com.projectmanagement.multitenantprojectmanagement.users.Users;
import com.projectmanagement.multitenantprojectmanagement.users.dto.request.CreateUserRequest;
import com.projectmanagement.multitenantprojectmanagement.users.dto.request.UpdateUserRequest;
import com.projectmanagement.multitenantprojectmanagement.users.dto.response.PaginatedResponseDto;
import com.projectmanagement.multitenantprojectmanagement.users.dto.response.UserListResponseDto;
import com.projectmanagement.multitenantprojectmanagement.users.dto.response.UserOrganizations;
import com.projectmanagement.multitenantprojectmanagement.users.dto.response.UserResponseDto;
import com.projectmanagement.multitenantprojectmanagement.users.embeddable.About;

public class UserMapper {

    public static Users toCreateUserEntity(CreateUserRequest createUserRequest) {
        Users user = new Users();
        user.setName(createUserRequest.getName());
        user.setEmail(createUserRequest.getEmail());
        user.setAuth0Id(createUserRequest.getAuth0UserId());
        user.setIsActive(true);

        return user;
    }

    public static Users toUpdateUserEntity(UpdateUserRequest updateUserRequest) {
        Users users = new Users();
        About about = new About();
        about.setCompanyName(updateUserRequest.getAbout().getCompanyName());
        about.setDepartment(updateUserRequest.getAbout().getDepartment());
        about.setJobTitle(updateUserRequest.getAbout().getJobTitle());
        about.setLocation(updateUserRequest.getAbout().getLocation());

        users.setId(updateUserRequest.getId());
        users.setName(updateUserRequest.getName());
        users.setAbout(about);
        users.setCoverPic(updateUserRequest.getCoverPic());
        users.setProfilePic(updateUserRequest.getProfilePic());

        return users;
    }

    public static UserResponseDto toUserReponse(Users user) {
        return UserResponseDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .auth0Id(user.getAuth0Id())
                .about(user.getAbout())
                .coverPic(user.getCoverPic())
                .profilePic(user.getProfilePic())
                .build();
    }

    public static List<UserListResponseDto> toUserListResponse(List<Users> users) {
        List<UserListResponseDto> response = new ArrayList<>();

        for(Users user: users) {
            UserListResponseDto userResponse = UserListResponseDto.builder()
                                                .id(user.getId())
                                                .name(user.getName())
                                                .profilePic(user.getProfilePic())
                                                .build();
            response.add(userResponse);
        }

        return response;
    }

    public static List<UserOrganizations> toUserOrganizations(List<Organizations> organizations) {
        List<UserOrganizations> response = new ArrayList<>();

        for(Organizations organization: organizations) {
            UserOrganizations userOrganization = UserOrganizations.builder()
                                                .id(organization.getId())
                                                .auth0Id(organization.getAuth0Id())
                                                .name(organization.getName())
                                                .build();
            response.add(userOrganization);                                    
        }

        return response;
    }

    public static PaginatedResponseDto<UserListResponseDto> toUserPaginatedResponse(List<UserListResponseDto> usersList, Page<Users> users) {
        return PaginatedResponseDto.<UserListResponseDto>builder()
                .data(usersList)
                .page(users.getNumber())
                .size(users.getSize())
                .totalElements(users.getTotalElements())
                .totalPages(users.getTotalPages())
                .build();
    }

}
