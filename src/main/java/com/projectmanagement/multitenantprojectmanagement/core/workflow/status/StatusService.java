package com.projectmanagement.multitenantprojectmanagement.core.workflow.status;

import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.projectmanagement.multitenantprojectmanagement.auth0.utils.JWTUtils;
import com.projectmanagement.multitenantprojectmanagement.core.project.ProjectService;
import com.projectmanagement.multitenantprojectmanagement.core.project.Projects;
import com.projectmanagement.multitenantprojectmanagement.core.workflow.Workflow;
import com.projectmanagement.multitenantprojectmanagement.core.workflow.status.dto.request.CreateStatusRequest;
import com.projectmanagement.multitenantprojectmanagement.core.workflow.status.dto.request.UpdateStatusRequest;
import com.projectmanagement.multitenantprojectmanagement.core.workflow.status.dto.response.StatusResponse;
import com.projectmanagement.multitenantprojectmanagement.core.workflow.status.dto.response.StatusesResponse;
import com.projectmanagement.multitenantprojectmanagement.core.workflow.status.enums.Category;
import com.projectmanagement.multitenantprojectmanagement.core.workflow.status.mapper.StatusMapper;
import com.projectmanagement.multitenantprojectmanagement.exception.NotFoundException;
import com.projectmanagement.multitenantprojectmanagement.helper.MaskingString;
import com.projectmanagement.multitenantprojectmanagement.organizationmembers.OrganizationMembersService;
import com.projectmanagement.multitenantprojectmanagement.organizations.OrganizationsService;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StatusService {

    private final StatusRepository statusRepository;
    private final MaskingString maskingString;
    private final ProjectService projectService;
    private static final Logger logger = LoggerFactory.getLogger(StatusService.class);

    public Status getFullStatusDetails(UUID id) {
        logger.info("Getting status with ID: {}", maskingString.maskSensitive(id.toString()));

        Status status = statusRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Status not found for the given ID: " + id));

        logger.debug("Fetched status with ID: {}", maskingString.maskSensitive(status.getId().toString()));

        return status;

    }

    public StatusResponse getStatusById(UUID id) {
        Status status = getFullStatusDetails(id);

        return StatusMapper.toStatusResponse(status);
    }

    public List<StatusesResponse> getAllStatusByProjectId(UUID projectId) {
        logger.info("Getting all status in a project by its ID: {}", maskingString.maskSensitive(projectId.toString()));

        List<Status> statuses = statusRepository.findAllByProjectId(projectId);

        logger.debug("Fetched {} statuses", statuses.size());

        return StatusMapper.toStatusesResponse(statuses);
    }

    @Transactional
    public StatusResponse createStatus(@Valid CreateStatusRequest createStatusRequest) {
        logger.info("Creating Status for the given project ID: {}",
                maskingString.maskSensitive(createStatusRequest.getProjectId().toString()));

        Projects project = projectService.getProjectById(createStatusRequest.getProjectId());

        Status statusEntity = StatusMapper.toStatusEntity(createStatusRequest, project);

        Status savedStatus = statusRepository.save(statusEntity);

        logger.debug("Saved status ID: {}", maskingString.maskSensitive(savedStatus.getId().toString()));

        return StatusMapper.toStatusResponse(savedStatus);
    }

    @Transactional
    public StatusResponse updateStatus(@Valid UpdateStatusRequest updateStatusRequest) {
        logger.info("Updating status for the given ID: {}",
                maskingString.maskSensitive(updateStatusRequest.getId().toString()));

        Status status = statusRepository.findById(updateStatusRequest.getId()).orElseThrow(
                () -> new NotFoundException("Status Not found fo the given ID: " + updateStatusRequest.getId()));

        if (updateStatusRequest.getName() != null) {
            status.setName(updateStatusRequest.getName());
        }

        if (updateStatusRequest.getDefaultStatus() != null) {
            status.setDefaultStatus(updateStatusRequest.getDefaultStatus());
            // if one is set to default set other in the same project to be not default
        }

        if (updateStatusRequest.getCategory() != null) {

            if ("OPEN".equals(updateStatusRequest.getCategory())) {
                status.setCategory(Category.OPEN);
            } else if ("INPROGRESS".equals(updateStatusRequest.getCategory())) {
                status.setCategory(Category.INPROGRESS);
            } else if ("CLOSED".equals(updateStatusRequest.getCategory())) {
                status.setCategory(Category.CLOSED);
            } else {
                throw new IllegalArgumentException("Only OPEN, INPROGRESS and CLOSED are allowed!");
            }
        }

        Status updatedStatus = statusRepository.save(status);

        logger.debug("Updated Status ID: {}", maskingString.maskSensitive(updatedStatus.getId().toString()));

        return StatusMapper.toStatusResponse(updatedStatus);
    }

    @Transactional
    public StatusResponse deletedStatusById(UUID id) {
        logger.info("Deleting Status for the given ID: {}", maskingString.maskSensitive(id.toString()));

        Status status = getFullStatusDetails(id);

        statusRepository.delete(status);

        logger.debug("Deleted status ID: {}", maskingString.maskSensitive(status.getId().toString()));

        return StatusMapper.toStatusResponse(status);
    }

}
