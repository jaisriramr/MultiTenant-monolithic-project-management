package com.projectmanagement.multitenantprojectmanagement.organizations;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;

import com.projectmanagement.multitenantprojectmanagement.auth0.Auth0Service;
import com.projectmanagement.multitenantprojectmanagement.auth0.utils.JWTUtils;
import com.projectmanagement.multitenantprojectmanagement.exception.ConflictException;
import com.projectmanagement.multitenantprojectmanagement.exception.NotFoundException;
import com.projectmanagement.multitenantprojectmanagement.helper.MaskingString;
import com.projectmanagement.multitenantprojectmanagement.organizations.dto.request.CreateOrganizationRequest;
import com.projectmanagement.multitenantprojectmanagement.organizations.dto.request.UpdateOrganizationRequest;
import com.projectmanagement.multitenantprojectmanagement.organizations.dto.response.OrganizationResponse;
import com.projectmanagement.multitenantprojectmanagement.organizations.dto.response.OrganizationsResponse;
import com.projectmanagement.multitenantprojectmanagement.organizations.mapper.OrganizationMapper;
import com.projectmanagement.multitenantprojectmanagement.users.UserService;
import com.projectmanagement.multitenantprojectmanagement.users.dto.response.UserResponseDto;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrganizationsService {

    private final OrganizationsRepository organizationsRepository;
    private final Auth0Service auth0Service;
    private static final Logger logger = LoggerFactory.getLogger(OrganizationsService.class);
    private final MaskingString maskingString;
    private final JWTUtils jwtUtils;

    @Cacheable(value = "organizations", key = "#id")
    public OrganizationResponse getOrganizationById(UUID id) {

        logger.info("Getting organization for the given ID: {} ", maskingString.maskSensitive(id.toString()));

        Organizations organization = organizationsRepository.findById(id).orElseThrow(() -> new NotFoundException("Organization not found for the given ID: " + id));

        logger.debug("Fetched organization ID: {} ", maskingString.maskSensitive(organization.getId().toString()));

        return OrganizationMapper.toOrganizationResponse(organization);
    }

    public Organizations getOrganizationByAuth0Id(String auth0Id) {
        logger.info("Getting organization for the given Auth0 ID: {} ", maskingString.maskSensitive(auth0Id));

        Organizations organization = organizationsRepository.findByAuth0Id(auth0Id).orElseThrow(() -> new NotFoundException("Organization not found for the given Auth0 ID: " + auth0Id));

        logger.debug("Fetched organization Auth0 ID: {} ", maskingString.maskSensitive(organization.getAuth0Id()));

        return organization;
    }

    public List<OrganizationsResponse> getOrganizations() {
        logger.info("Getting all organizations");

        List<Organizations> organizations = organizationsRepository.findAll();

        logger.debug("Fetched {} organizations", organizations.size());

        return OrganizationMapper.toOrganizationsResponse(organizations);
    }

    @Transactional
    public Organizations createAnOrganization(@Valid CreateOrganizationRequest createOrganizationRequest) {
        logger.info("Creating organization with name: {} and display name: {}", maskingString.maskSensitive(createOrganizationRequest.getName()), maskingString.maskSensitive(createOrganizationRequest.getDisplayName()));
        try {

            organizationsRepository.findByName(createOrganizationRequest.getName()).ifPresent(org -> {
                throw new ConflictException("Organization with the same name already exists");
            });

            organizationsRepository.findByDomain(createOrganizationRequest.getDomain()).ifPresent(org -> {
                throw new ConflictException("Organization with the same domain already exists");
            });

            ResponseEntity<Map<String, Object>> auth0Response = auth0Service.createAnOrganization(createOrganizationRequest.getName(), createOrganizationRequest.getDisplayName());

            Map<String, Object> body = auth0Response.getBody();

            if (body != null) {
                String id = (String) body.get("id");

                logger.debug("Created org ID from auth0: {}", maskingString.maskSensitive(id));

                Organizations organization = OrganizationMapper.toEntityOrganization(createOrganizationRequest, id);

                Organizations savedOrganization = organizationsRepository.save(organization);

                logger.debug("Saved org ID: {}", maskingString.maskSensitive(savedOrganization.getId().toString()));

                return savedOrganization;
            } else {
                logger.error("Error while creating organization in Auth0");
                throw new RuntimeException("Error while trying to create an organization");
            }

        } catch (ConflictException e) {
            logger.error("Organization already exists: {}", e.getMessage(), e);
            throw e;
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            logger.error("Error calling Auth0 API for org creation: {}", e.getMessage(), e);
            throw new RuntimeException("Error communicating with Auth0 while creating the org.", e);
        } catch (Exception e) {
            logger.error("Error while creating organization: {}", e.getMessage(), e);
            throw e;
        }
    }

    @Transactional
    public OrganizationResponse updateAnOrganiation(UpdateOrganizationRequest updateOrganizationRequest) {
        logger.info("Updating organization with ID: {} and name: {} and display name: {}", maskingString.maskSensitive(updateOrganizationRequest.getId().toString()), maskingString.maskSensitive(updateOrganizationRequest.getName()), maskingString.maskSensitive(updateOrganizationRequest.getDisplayName()));
        try {
            Organizations organizations = organizationsRepository.findById(updateOrganizationRequest.getId()).orElseThrow(() -> new NotFoundException("Organization not found for the given ID: " + updateOrganizationRequest.getId()));
            logger.debug("Fetched organization ID: {} ", maskingString.maskSensitive(organizations.getId().toString()));
            if (updateOrganizationRequest.getName() != null) {
                String displayName = updateOrganizationRequest.getDisplayName() != null ? updateOrganizationRequest.getDisplayName() : updateOrganizationRequest.getName();
                auth0Service.updateAnOrganization(organizations.getAuth0Id(), updateOrganizationRequest.getName(), displayName);
                logger.debug("Organization details updated in auth0");
                organizations.setName(updateOrganizationRequest.getName());
            }

            if (updateOrganizationRequest.getDisplayName() != null) {
                organizations.setDisplayName(updateOrganizationRequest.getDisplayName());
            }

            Organizations updatedOrganization = organizationsRepository.save(organizations);
            logger.debug("Updated organization ID: {} ", maskingString.maskSensitive(updatedOrganization.getId().toString()));
            return OrganizationMapper.toOrganizationResponse(updatedOrganization);
        } catch (NotFoundException e) {
            logger.error("Organization not found: {}", e.getMessage(), e);
            throw e;
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            logger.error("Error calling Auth0 API for org updation: {}", e.getMessage(), e);
            throw new RuntimeException("Error communicating with Auth0 while updating the org.", e);
        } catch (Exception e) {
            throw new RuntimeException("Error while trying to update an organization", e);
        }
    }

    @Transactional
    public String deleteOrganizationById(UUID id, UUID userId) {
        try {
            Organizations org = organizationsRepository.findById(id).orElseThrow(() -> new NotFoundException("Organization not found for the given ID: " + id));
            logger.debug("Fetched organization ID: {} ", maskingString.maskSensitive(org.getId().toString()));

            String auth0UserID = jwtUtils.getCurrentUserId();

            logger.debug("Fetched Auth0 user ID: {} ", maskingString.maskSensitive(auth0UserID));

            // UserResponseDto user = userService.getUserByAuth0Id(auth0UserID);

            logger.debug("Fetched user ID: {} ", maskingString.maskSensitive(userId.toString()));

            org.setIsDeleted(true);
            org.setDeletedBy(userId);
            org.setDeletedAt(LocalDateTime.now());
            organizationsRepository.save(org);

            logger.debug("Organization marked as deleted ID: {} ", maskingString.maskSensitive(org.getId().toString()));

            return "Organization with Id " + id + " is removed successfully!";
        } catch (NotFoundException e) {
            logger.error("Organization not found: {}", e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Error while trying to delete an organization", e);
        }
    }

}
