package com.projectmanagement.multitenantprojectmanagement.core.sprint;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

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

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SprintService {

    private final SprintRepository sprintRepository;
    private final ProjectService projectService;
    private final MaskingString maskingString;
    private static final Logger logger = LoggerFactory.getLogger(SprintService.class);


    public Sprint getSprintEntity(UUID id) {
        logger.info("Getting Sprint details for the given ID: {}", maskingString.maskSensitive(id.toString()));

        Sprint sprint = sprintRepository.findById(id).orElseThrow(() -> new NotFoundException("Sprint not found for the given ID: " + id));

        logger.debug("Fetched sprint ID: {}", maskingString.maskSensitive(sprint.getId().toString()));

        return sprint;
    }

    public List<ListSprintResponse> getAllSprintByProjectId(UUID projectId) {
        logger.info("Getting all sprint for the given project ID: {}", maskingString.maskSensitive(projectId.toString()));

        List<Sprint> sprints = sprintRepository.findAllByProjectId(projectId);

        logger.debug("Fetched {} sprints", sprints.size());

        return SprintMapper.toListSprintResponse(sprints);
    }


    @Transactional
    public SprintDetailedResponse createSprint(CreateSprintRequest createSprintRequest) {
        logger.info("Creating sprint");

        Projects project = projectService.getProjectById(createSprintRequest.getProjectId());

        Sprint sprint = sprintRepository.findByNameAndProjectId(createSprintRequest.getName(), createSprintRequest.getProjectId()).orElse(null);

        if(sprint != null) {
            throw new BadRequestException("Sprint with that name already exist in your project");
        }

        Sprint sprintEntity = SprintMapper.toSprintEntity(createSprintRequest, project);

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
