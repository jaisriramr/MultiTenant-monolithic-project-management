package com.projectmanagement.multitenantprojectmanagement.users;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.projectmanagement.multitenantprojectmanagement.auth0.utils.JWTUtils;
import com.projectmanagement.multitenantprojectmanagement.exception.GlobalExceptionHandler;
import com.projectmanagement.multitenantprojectmanagement.exception.NotFoundException;
import com.projectmanagement.multitenantprojectmanagement.exception.enums.Exceptions;
import com.projectmanagement.multitenantprojectmanagement.organizationmembers.OrganizationMembersRepository;
import com.projectmanagement.multitenantprojectmanagement.organizations.Organizations;
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
    private final OrganizationMembersRepository organizationMembersRepository;
    private final JWTUtils jwtUtils;
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    public UserResponseDto getUserById(UUID id) {
        logger.info("Getting User By Id: {} ", id);
        Users user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found for the given User Id " + id));

        logger.debug("Fetched User Details: {} ", user != null ? user.getId() : "User Not Found");

        return UserMapper.toUserReponse(user);
    }

    public PaginatedResponseDto<UserListResponseDto> getAllUsers(Pageable pageable) {
        logger.info("Getting All Users From the DB: {}", pageable);
        try {
            Page<Users> users = userRepository.findAll(pageable);
            logger.debug("Fetched Users from DB", users != null ? users.getContent().getFirst().getId() : null);

            List<UserListResponseDto> responseList = UserMapper.toUserListResponse(users.getContent());

            return UserMapper.toUserPaginatedResponse(responseList, users);

        } catch (Exception e) {
            logger.error(Exceptions.INTERNAL_SERVER_ERROR.toString(), e);
            throw new RuntimeException(Exceptions.INTERNAL_SERVER_ERROR.toString(), e);
        }
    }

    public List<UserOrganizations> getUserOrganizations(UUID id) {
        logger.info("Fetching all organizations by userId ", id);
        try {
            List<Organizations> organizations = organizationMembersRepository.findOrganizationsByUserId(id);

            logger.debug("Fetched User By UserId and its first org name ",
                    organizations != null ? organizations.getFirst().getId() : null);

            return UserMapper.toUserOrganizations(organizations);

        } catch (Exception e) {
            logger.error("Error while trying to fetch orgs with user Id: {} ", id, e);
            throw new RuntimeException(
                    Exceptions.INTERNAL_SERVER_ERROR.toString(), e);
        }
    }

    public List<UserListResponseDto> searchUsersByName(String name) {
        logger.info("Searching users by name");
        try {
            List<Users> users = userRepository.findAllByNameContainingIgnoreCase(name).orElse(null);
            logger.debug("Fetched Users by name ", users != null ? users.getFirst().getId() : null);
            return UserMapper.toUserListResponse(users);
        } catch (Exception e) {
            logger.error("Error while trying to search for users with name: {} ", name, e);
            throw new RuntimeException(Exceptions.INTERNAL_SERVER_ERROR.toString(), e);
        }
    }

    public Users getUserByEmail(String email) {
        logger.info("fetching user by email ");
        Users user = userRepository.findByEmail(email).orElse(null);
        logger.debug("Fetched User by email ", user.getId());
        return user;
    }

    public UserResponseDto getUserByAuth0Id(String auth0Id) {
        logger.info("fetching user by auth0Id: {} ", auth0Id);
        Users user = userRepository.findByAuth0Id(auth0Id).orElseThrow(() -> new NotFoundException());
        logger.debug("Fetched user by auth0Id: {} ", user != null ? user.getId() : null);
        return UserMapper.toUserReponse(user);
    }

    @Transactional
    public Users createUser(CreateUserRequest createUserRequest) {
        logger.info("Creating User");
        try {
            Users user = UserMapper.toCreateUserEntity(createUserRequest);

            Users savedUser = userRepository.save(user);

            logger.debug("User Created ", savedUser.getId());

            return savedUser;
        } catch (Exception e) {
            logger.error("Error while trying to create an user ", e);
            throw new RuntimeException(Exceptions.INTERNAL_SERVER_ERROR.toString(), e);
        }
    }

    @Transactional
    public UserResponseDto updateUser(UpdateUserRequest updateUserRequest) {
        logger.info("Updating User");
        try {
            Users user = userRepository.findById(updateUserRequest.getId()).orElseThrow(() -> new NotFoundException());

            logger.debug("Update User - Fetched User ", user != null ? user.getId() : null);

            if (user.getAbout() == null) {
                user.setAbout(updateUserRequest.getAbout());
            } else {
                if (updateUserRequest.getAbout().getJobTitle() != null)
                    user.getAbout().setJobTitle(updateUserRequest.getAbout().getJobTitle());
                if (updateUserRequest.getAbout().getDepartment() != null)
                    user.getAbout().setDepartment(updateUserRequest.getAbout().getDepartment());
                if (updateUserRequest.getAbout().getCompanyName() != null)
                    user.getAbout().setCompanyName(updateUserRequest.getAbout().getCompanyName());
                if (updateUserRequest.getAbout().getLocation() != null)
                    user.getAbout().setLocation(updateUserRequest.getAbout().getLocation());
            }

            if (updateUserRequest.getName() != null) {
                user.setName(updateUserRequest.getName());
            }
            if (updateUserRequest.getCoverPic() != null) {
                user.setCoverPic(updateUserRequest.getCoverPic());
            }
            if (updateUserRequest.getProfilePic() != null) {
                user.setProfilePic(updateUserRequest.getProfilePic());
            }

            Users updatedUser = userRepository.save(user);
            logger.debug("Update User - updated User details", user.getId());
            // make api call to auth0 to update user to auth0

            return UserMapper.toUserReponse(updatedUser);
        } catch (Exception e) {
            logger.error("Error while trying to update an user", e);
            throw new RuntimeException("Error while trying to update an user", e);
        }
    }

    @Transactional
    public String deleteUserById(UUID id) {
        logger.info("Deleting user by id ", id);
        try {
            String deletingUserId = jwtUtils.getCurrentUserId();
            // userRepository.deleteById(id);
            Users user = userRepository.findById(id).orElseThrow(() -> new NotFoundException("User Not Found for the given Id" + id));
            Users deletingUserDetail = userRepository.findByAuth0Id(deletingUserId).orElseThrow(() -> new NotFoundException("User Not Found for updating deleted by"));

            user.setIsDeleted(true);
            
            user.setDeletedAt(LocalDateTime.now());

            user.setDeletedBy(deletingUserDetail.getId());

            userRepository.save(user);

            logger.debug("User id Deleted", id);

            return "User with id " + id + " has be removed successfully!";
        } catch (Exception e) {
            logger.error("Error while trying to delete an user with id: {}", id, e);
            throw new RuntimeException(Exceptions.INTERNAL_SERVER_ERROR.toString(), e);
        }
    }

}
