package com.projectmanagement.multitenantprojectmanagement.core.sprint;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.projectmanagement.multitenantprojectmanagement.auth0.utils.JWTUtils;
import com.projectmanagement.multitenantprojectmanagement.core.project.ProjectService;
import com.projectmanagement.multitenantprojectmanagement.core.project.Projects;
import com.projectmanagement.multitenantprojectmanagement.core.sprint.dto.request.CreateSprintRequest;
import com.projectmanagement.multitenantprojectmanagement.core.sprint.dto.request.UpdateSprintRequest;
import com.projectmanagement.multitenantprojectmanagement.core.sprint.dto.response.ListSprintResponse;
import com.projectmanagement.multitenantprojectmanagement.core.sprint.dto.response.MinimalSprintResponse;
import com.projectmanagement.multitenantprojectmanagement.core.sprint.dto.response.SprintDetailedResponse;
import com.projectmanagement.multitenantprojectmanagement.core.sprint.mapper.SprintMapper;
import com.projectmanagement.multitenantprojectmanagement.exception.BadRequestException;
import com.projectmanagement.multitenantprojectmanagement.exception.NotFoundException;
import com.projectmanagement.multitenantprojectmanagement.helper.MaskingString;
import com.projectmanagement.multitenantprojectmanagement.organizations.Organizations;
import com.projectmanagement.multitenantprojectmanagement.organizations.OrganizationsService;
import com.projectmanagement.multitenantprojectmanagement.organizations.dto.response.PaginatedResponseDto;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SprintService {

    private final SprintRepository sprintRepository;
    private final ProjectService projectService;
    private final MaskingString maskingString;
    private static final Logger logger = LoggerFactory.getLogger(SprintService.class);
    private final OrganizationsService organizationsService;
    private final JWTUtils jwtUtils;


    public Sprint getSprintEntity(UUID id) {
        logger.info("Getting Sprint details for the given ID: {}", maskingString.maskSensitive(id.toString()));

        String auth0OrgId = jwtUtils.getAuth0OrgId();

        Sprint sprint = sprintRepository.findByIdAndOrganization_Auth0Id(id, auth0OrgId).orElseThrow(() -> new NotFoundException("Sprint not found for the given ID: " + id));

        logger.debug("Fetched sprint ID: {}", maskingString.maskSensitive(sprint.getId().toString()));

        return sprint;
    }

    public PaginatedResponseDto<ListSprintResponse> getAllSprintByProjectId(UUID projectId, Pageable pageable) {
        logger.info("Getting all sprint for the given project ID: {}", maskingString.maskSensitive(projectId.toString()));

        String auth0OrgId = jwtUtils.getAuth0OrgId();

        Page<Sprint> sprints = sprintRepository.findAllByProjectIdAndOrganization_Auth0Id(projectId, auth0OrgId, pageable);

        logger.debug("Fetched {} sprints", sprints.getTotalElements());

        return SprintMapper.toPaginatedResponseDto(sprints);
    }


    @Transactional
    public SprintDetailedResponse createSprint(CreateSprintRequest createSprintRequest) {
        logger.info("Creating sprint");

        Projects project = projectService.getProjectById(createSprintRequest.getProjectId());

        String auth0OrgId = jwtUtils.getAuth0OrgId();

        Sprint sprint = sprintRepository.findByNameAndProjectIdAndOrganization_Auth0Id(createSprintRequest.getName(), createSprintRequest.getProjectId(), auth0OrgId).orElse(null);

        if(sprint != null) {
            throw new BadRequestException("Sprint with that name already exist in your project");
        }

        Organizations organization = organizationsService.getOrganizationByAuth0Id(auth0OrgId);

        Sprint sprintEntity = SprintMapper.toSprintEntity(createSprintRequest, project, organization);

        Sprint savedSprint = sprintRepository.save(sprintEntity);

        logger.debug("Fetched sprint ID: {}", maskingString.maskSensitive(savedSprint.getId().toString()));

        return SprintMapper.toSprintDetailedResponse(savedSprint);
    }

    @Transactional
    public SprintDetailedResponse updateSprint(UpdateSprintRequest updateSprintRequest) {

        logger.info("Updating sprint");

        Sprint sprint = getSprintEntity(updateSprintRequest.getId());

        if(updateSprintRequest.getName() != null) {
            sprint.setName(updateSprintRequest.getName());
        }

        if(updateSprintRequest.getDescription() != null)  {
            sprint.setGoal(updateSprintRequest.getDescription());
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        if(updateSprintRequest.getStartDate() != null) {
            LocalDate startDate = LocalDate.parse(updateSprintRequest.getStartDate(), formatter);
            sprint.setStartDate(startDate);
        }

        if(updateSprintRequest.getEndDate() != null) {
            LocalDate endDate = LocalDate.parse(updateSprintRequest.getEndDate(), formatter);
            sprint.setEndDate(endDate);
        }

        Sprint updatedSprint = sprintRepository.save(sprint);

        logger.debug("Updated sprint ID: {}", maskingString.maskSensitive(updatedSprint.getId().toString()));

        return SprintMapper.toSprintDetailedResponse(sprint);

    }

    @Transactional
    public MinimalSprintResponse deleteSprint(UUID id) {
        logger.info("Deleting sprint for the given ID: {}", maskingString.maskSensitive(id.toString()));

        Sprint sprint = getSprintEntity(id);

        sprintRepository.delete(sprint);

        logger.debug("Deleted sprint ID: {}", maskingString.maskSensitive(id.toString()));

        return SprintMapper.toMinimalSprintResponse(sprint);
    }
    

}
