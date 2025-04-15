package com.projectmanagement.multitenantprojectmanagement.users;

import java.util.List;
import java.util.UUID;

import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.projectmanagement.multitenantprojectmanagement.auth0.Auth0Service;
import com.projectmanagement.multitenantprojectmanagement.organizationmembers.OrganizationMembersRepository;
import com.projectmanagement.multitenantprojectmanagement.organizations.Organizations;
import com.projectmanagement.multitenantprojectmanagement.organizations.OrganizationsRepository;
import com.projectmanagement.multitenantprojectmanagement.users.dto.mapper.UserMapper;
import com.projectmanagement.multitenantprojectmanagement.users.dto.request.CreateUserRequest;
import com.projectmanagement.multitenantprojectmanagement.users.dto.request.UpdateUserRequest;
import com.projectmanagement.multitenantprojectmanagement.users.dto.response.PaginatedResponseDto;
import com.projectmanagement.multitenantprojectmanagement.users.dto.response.UserListResponseDto;
import com.projectmanagement.multitenantprojectmanagement.users.dto.response.UserOrganizations;
import com.projectmanagement.multitenantprojectmanagement.users.dto.response.UserResponseDto;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final Auth0Service auth0Service;
    private final OrganizationMembersRepository organizationMembersRepository;

    public UserResponseDto getUserById(UUID id) {
        try {

            Users user = userRepository.findById(id).orElseThrow(() -> new NotFoundException());

            // map role & organization to user resposne

            return UserMapper.toUserReponse(user);

        }catch(Exception e) {
            throw new RuntimeException("Error while trying to fetch user with id {}" + id);
        }
    }

    public PaginatedResponseDto<UserListResponseDto> getAllUsers(Pageable pageable) {
        try {
            Page<Users> users = userRepository.findAll(pageable);

            // if want map role or organization;

            List<UserListResponseDto> responseList = UserMapper.toUserListResponse(users.getContent());

            return UserMapper.toUserPaginatedResponse(responseList, users);

        }catch(Exception e) {
            throw new RuntimeException("Error while trying to fetch all users");
        }
    }

    public List<UserOrganizations> getUserOrganizations(UUID id) {
        try {
            List<Organizations> organizations = organizationMembersRepository.findOrganizationsByUserId(id);

            return UserMapper.toUserOrganizations(organizations);

        }catch(Exception e){
            throw new RuntimeException("Error while trying to fetch all organization associated with user with id {}" + id);
        }
    }

    public List<UserListResponseDto> searchUsersByName(String name) {
        try {
            List<Users> users = userRepository.findAllByNameContainingIgnoreCase(name).orElseThrow(() -> new RuntimeException("Error while trying to search for a name {}") );

            return UserMapper.toUserListResponse(users);
        }catch(Exception e) {
            throw new RuntimeException("Error while trying to search for a name {}" + name);
        }
    }

    public UserResponseDto getUserByAuth0Id(String auth0Id) {
        try {
            Users user = userRepository.findByAuth0Id(auth0Id).orElseThrow(() -> new NotFoundException());
            
            return UserMapper.toUserReponse(user);
        }catch(Exception e) {
            throw new RuntimeException("Error while trying to fetch user by auth0Id {}" + auth0Id);
        }
    }

    @Transactional
    public Users createUser(CreateUserRequest createUserRequest) {
        try {
            Users user = UserMapper.toCreateUserEntity(createUserRequest);

            Users savedUser = userRepository.save(user);

            return savedUser;
        }catch(Exception e) {
            throw new RuntimeException("Error while trying to create an user");
        }
    }

    @Transactional
    public UserResponseDto updateUser(UpdateUserRequest updateUserRequest) {
        try {
            
            Users user = userRepository.findById(updateUserRequest.getId()).orElseThrow(() -> new NotFoundException());
            
            if(user.getAbout() == null) {
                user.setAbout(updateUserRequest.getAbout());
            }else{
                if(user.getAbout().getJobTitle() != null) {
                    user.getAbout().setJobTitle(updateUserRequest.getAbout().getJobTitle());
                }
                if(user.getAbout().getDepartment() != null) {
                    user.getAbout().setDepartment(updateUserRequest.getAbout().getDepartment());
                }
                if(user.getAbout().getCompanyName() != null) {
                    user.getAbout().setCompanyName(updateUserRequest.getAbout().getCompanyName());
                }
                if(user.getAbout().getLocation() != null) {
                    user.getAbout().setLocation(updateUserRequest.getAbout().getLocation());
                }
            }

            if(updateUserRequest.getName() != null) {
                user.setName(updateUserRequest.getName());
            }
            if(updateUserRequest.getCoverPic() != null) {
                user.setCoverPic(updateUserRequest.getCoverPic());
            }
            if(updateUserRequest.getProfilePic() != null) {
                user.setProfilePic(updateUserRequest.getProfilePic());
            }
            
            Users updatedUser = userRepository.save(user);
            // make api call to auth0 to update user to auth0

            return UserMapper.toUserReponse(updatedUser);
        }catch(Exception e){
            throw new RuntimeException("Error while trying to update an user");
        }
    }

    @Transactional
    public String deleteUserById(UUID id) {
        try {
            userRepository.deleteById(id);

            // make api call to auth0 to remove user from auth0

            return "User with id " + id + " has be removed successfully!"; 
        }catch(Exception e) {
            throw new RuntimeException("Error while trying to remove an user with Id {}" + id);
        }
    }

}
