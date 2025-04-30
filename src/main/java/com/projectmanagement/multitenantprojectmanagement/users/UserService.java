package com.projectmanagement.multitenantprojectmanagement.users;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.projectmanagement.multitenantprojectmanagement.auth0.utils.JWTUtils;
import com.projectmanagement.multitenantprojectmanagement.exception.BadRequestException;
import com.projectmanagement.multitenantprojectmanagement.exception.ConflictException;
import com.projectmanagement.multitenantprojectmanagement.exception.NotFoundException;
import com.projectmanagement.multitenantprojectmanagement.helper.MaskingString;
import com.projectmanagement.multitenantprojectmanagement.organizationmembers.OrganizationMembersRepository;
import com.projectmanagement.multitenantprojectmanagement.organizations.Organizations;
import com.projectmanagement.multitenantprojectmanagement.organizations.OrganizationsService;
import com.projectmanagement.multitenantprojectmanagement.s3.s3Service;
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
    private final MaskingString maskingString;
    private final s3Service s3Service;
    private final JWTUtils jwtUtils;
    private final OrganizationsService organizationsService;

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    public Users getUserEntity(UUID id) {
        logger.info("Getting user for the given ID: {} ", maskingString.maskSensitive(id.toString()));
        
        String auth0OrgId = jwtUtils.getAuth0OrgId();

        logger.debug("Fetched org ID: {}", maskingString.maskSensitive(auth0OrgId));

        Users user = userRepository.findByIdAndOrganization_Auth0Id(id, auth0OrgId)
                .orElseThrow(() -> new NotFoundException("User not found for the given User Id " + id));

        logger.debug("Fetched user ID: {} ", maskingString.maskSensitive(user.getId().toString()));
        if (user.getIsDeleted() != null && user.getIsDeleted()) {
            logger.error("User is deleted for the given user ID: {}", maskingString.maskSensitive(id.toString()));
            throw new NotFoundException("User not found for the given User Id " + id);
        }

        return user;
    }

    public PaginatedResponseDto<UserListResponseDto> getAllUsers(Pageable pageable) {
        logger.info("Getting all users: {}", pageable);

        Page<Users> users = userRepository.findAll(pageable);

        logger.debug("Fetched {} users", users.getTotalElements());

        List<UserListResponseDto> responseList = UserMapper.toUserListResponse(users.getContent());

        return UserMapper.toUserPaginatedResponse(responseList, users);
    }

    public List<UserOrganizations> getUserOrganizations(UUID id) {
        logger.info("Getting all organizations by user ID: {}", maskingString.maskSensitive(id.toString()));

        Users user = getUserEntity(id);

        logger.debug("Fetched user ID: {} ", maskingString.maskSensitive(user.getId().toString()));

        if (user.getIsDeleted()) {
            logger.error("User is deleted for the given user ID: {}", maskingString.maskSensitive(id.toString()));
            throw new NotFoundException("User not found for the given User Id " + id);
        }

        List<Organizations> organizations = organizationMembersRepository.findOrganizationsByUserId(id);

        logger.debug("Fetched {} organizations", organizations.size());

        if (organizations.isEmpty()) {
            logger.error("No organizations found for the given user ID: {}", maskingString.maskSensitive(id.toString()));
            throw new NotFoundException("No organizations found for the given user ID " + id);
        }

        return UserMapper.toUserOrganizations(organizations);
    }

    public List<UserListResponseDto> searchUsersByName(String name) {
        logger.info("Searching users by name: {}", maskingString.maskSensitive(name));

        String auth0OrgId = jwtUtils.getAuth0OrgId();

        List<Users> users = userRepository.findAllByNameContainingIgnoreCaseAndIsDeletedFalseAndOrganization_Auth0Id(name, auth0OrgId).orElse(null);

        logger.debug("Fetched {} users", users.size());

        if (users.isEmpty()) {
            logger.error("No users found for the given name: {}", maskingString.maskSensitive(name));
            throw new NotFoundException("No users found for the given name " + name);
        }

        return UserMapper.toUserListResponse(users);
    }

    public Users getUserByEmail(String email) {
        logger.info("Getting user by email: {} ", maskingString.maskSensitive(email));

        String auth0OrgId = jwtUtils.getAuth0OrgId();

        Users user = userRepository.findByEmailAndOrganization_Auth0Id(email, auth0OrgId).orElse(null);

        logger.debug("Fetched User ID : {}", maskingString.maskSensitive(user != null ? user.getId().toString() : null));

        return user;
    }

    public UserResponseDto getUserByAuth0Id(String auth0Id) {
        logger.info("Getting user by auth0Id: {} ", maskingString.maskSensitive(auth0Id));

        String auth0OrgId = jwtUtils.getAuth0OrgId();

        Users user = userRepository.findByAuth0IdAndOrganization_Auth0Id(auth0Id, auth0OrgId).orElseThrow(() -> new NotFoundException("User not found for the given auth0Id " + auth0Id));
        if (user.getIsDeleted()) {
            logger.error("User is deleted for the given auth0Id: {}", maskingString.maskSensitive(auth0Id));
            throw new NotFoundException("User not found for the given auth0Id " + auth0Id);
        }

        logger.debug("Fetched user by auth0Id: {} ", user != null ? maskingString.maskSensitive(user.getId().toString()) : null);

        return UserMapper.toUserReponse(user);
    }

    @Transactional
    public Users createUser(CreateUserRequest createUserRequest) {
        logger.info("Creating user for the given request: {} ", maskingString.maskSensitive(createUserRequest.toString()));
            String auth0OrgId = jwtUtils.getAuth0OrgId();

            Organizations organization = organizationsService.getOrganizationByAuth0Id(auth0OrgId);

            Users user = UserMapper.toCreateUserEntity(createUserRequest);
            user.setOrganization(organization);

            getUserByEmail(createUserRequest.getEmail());

            // getUserByAuth0Id(createUserRequest.getAuth0UserId());

            Users savedUser = userRepository.save(user);

            System.out.println("savedUser " +savedUser );

            logger.debug("Created user ID: {}", maskingString.maskSensitive(savedUser.getId().toString()));

            return savedUser;
    }

    @Transactional
    public UserResponseDto updateUser(UpdateUserRequest updateUserRequest
    ) {
        logger.info("Updating user for the given user ID: {} ", maskingString.maskSensitive(updateUserRequest.getId().toString()));
        try {
            Users user = getUserEntity(updateUserRequest.getId());

            if (user.getAbout() == null) {
                user.setAbout(updateUserRequest.getAbout());
            } else {
                if (updateUserRequest.getAbout().getJobTitle() != null) {
                    user.getAbout().setJobTitle(updateUserRequest.getAbout().getJobTitle());
                }
                if (updateUserRequest.getAbout().getDepartment() != null) {
                    user.getAbout().setDepartment(updateUserRequest.getAbout().getDepartment());
                }
                if (updateUserRequest.getAbout().getCompanyName() != null) {
                    user.getAbout().setCompanyName(updateUserRequest.getAbout().getCompanyName());
                }
                if (updateUserRequest.getAbout().getLocation() != null) {
                    user.getAbout().setLocation(updateUserRequest.getAbout().getLocation());
                }
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
            logger.debug("Updated user ID: {} ", maskingString.maskSensitive(updatedUser.getId().toString()));
            // make api call to auth0 to update user to auth0

            return UserMapper.toUserReponse(updatedUser);
        } catch (NotFoundException e) {
            logger.error("User not found for the given ID: {}", maskingString.maskSensitive(updateUserRequest.getId().toString()), e);
            throw e;
        } catch (Exception e) {
            logger.error("Error while trying to update an user with ID: {}", maskingString.maskSensitive(updateUserRequest.getId().toString()), e);
            throw new RuntimeException("Error while trying to update an user", e);
        }
    }

    @Transactional
    public String deleteUserById(UUID id
    ) {
        logger.info("Deleting user by ID: {} ", id);
        try {
            Users user = getUserEntity(id);

            user.setIsDeleted(true);

            user.setDeletedAt(LocalDateTime.now());

            userRepository.save(user);

            logger.debug("User with ID: {} has been deleted", id);

            return "User with ID " + id + " has be removed successfully!";
        } catch (NotFoundException e) {
            logger.error("User not found for the given ID: {}", id, e);
            throw e;
        } catch (Exception e) {
            logger.error("Error while trying to delete an user with ID: {}", id, e);
            throw new RuntimeException("Error while trying to delete an user with ID: " + id, e);
        }
    }

    @Transactional
    public UserResponseDto uploadProfilePicOrCoverPic(UUID id, MultipartFile file, String type) {
        logger.info("Uploading file for the type: {}", type);
        try {

            Users user =  getUserEntity(id);

            String s3Url = s3Service.uploadFile(file, id, type.toString());

            logger.debug("File uploaded to s3");
            
            if("profile".equals(type)) {
                user.setProfilePic(s3Url);
            }else if("cover".equals(type)) {
                user.setCoverPic(s3Url);
            }else{
                throw new BadRequestException("Type must be either 'profile' or 'cover' ");
            }

            Users savedUser = userRepository.save(user);

            logger.debug("Saved User ID: {}", maskingString.maskSensitive(savedUser.getId().toString()));

            return UserMapper.toUserReponse(savedUser);

        }catch(NotFoundException e) {
            throw e;
        }catch(BadRequestException e) {
            throw e;
        }catch (Exception e) {
            logger.error("Error uploading file for user ID: {}", id, e);
            throw new RuntimeException("An unexpected error occurred while uploading the file.");
        }

    }

}
