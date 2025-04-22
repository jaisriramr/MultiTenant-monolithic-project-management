package com.projectmanagement.multitenantprojectmanagement.core.project;


import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.projectmanagement.multitenantprojectmanagement.auth0.utils.JWTUtils;
import com.projectmanagement.multitenantprojectmanagement.core.project.dto.request.CreateProjectRequest;
import com.projectmanagement.multitenantprojectmanagement.core.project.dto.request.UpdateProjectRequest;
import com.projectmanagement.multitenantprojectmanagement.core.project.dto.response.ProjectDetailsResponse;
import com.projectmanagement.multitenantprojectmanagement.core.project.dto.response.ProjectsResponse;
import com.projectmanagement.multitenantprojectmanagement.core.project.mapper.ProjectMapper;
import com.projectmanagement.multitenantprojectmanagement.core.workflow.status.StatusService;
import com.projectmanagement.multitenantprojectmanagement.exception.NotFoundException;
import com.projectmanagement.multitenantprojectmanagement.helper.MaskingString;
import com.projectmanagement.multitenantprojectmanagement.organizationmembers.OrganizationMembers;
import com.projectmanagement.multitenantprojectmanagement.organizationmembers.OrganizationMembersService;
import com.projectmanagement.multitenantprojectmanagement.organizations.dto.response.PaginatedResponseDto;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final OrganizationMembersService organizationMembersService;
    private final JWTUtils jwtUtils;
    private static final Logger logger = LoggerFactory.getLogger(ProjectService.class);
    private final MaskingString maskingString;

    public Projects getProjectById(UUID id) {
        logger.info("Getting project for the given ID: {}", maskingString.maskSensitive(id.toString()));
        
        Projects project = projectRepository.findById(id).orElseThrow(() -> new NotFoundException("Project not found for the given ID: " + id));
        
        logger.debug("Fetched project ID: {}", maskingString.maskSensitive(project.getId().toString()));

        return project;
    }

    public ProjectDetailsResponse getProjectByIdController(UUID id) {
        Projects project = getProjectById(id);

        return ProjectMapper.toProjectDetailsResponse(project);
    }

    public PaginatedResponseDto<ProjectsResponse> getAllProjectsByOrganizationId(UUID orgId, Pageable pageable) {
        logger.info("Getting all project that belongs to a org ID: {}", maskingString.maskSensitive(orgId.toString()));

        Page<Projects> projects = projectRepository.findAllByOrganizationId(orgId, pageable);

        logger.debug("Fetched {} projects", projects.getTotalElements());

        return ProjectMapper.toProjectsResponse(projects);
    }

    @Transactional
    public ProjectDetailsResponse createProject(@Valid CreateProjectRequest createProjectRequest) {
        logger.info("Creating project for the given name: {}", maskingString.maskSensitive(createProjectRequest.getName()));

        String auth0UserId = jwtUtils.getCurrentUserId();
        String auth0OrgId = jwtUtils.getAuth0OrgId();

        logger.debug("Fetched Auth0 User ID: {} and Auth0 Org ID: {}", maskingString.maskSensitive(auth0UserId), maskingString.maskSensitive(auth0OrgId));
    
        OrganizationMembers member = organizationMembersService.getOrganizationMemberbyAuth0UserIdAndAuth0OrgId(auth0UserId, auth0OrgId);

        Projects projectEntity = ProjectMapper.toProjectEntity(createProjectRequest, member);
        
        Projects savedProject = projectRepository.save(projectEntity);

        logger.debug("Saved project ID: {}", maskingString.maskSensitive(savedProject.getId().toString()));

        return ProjectMapper.toProjectDetailsResponse(savedProject);
    }

    @Transactional
    public ProjectDetailsResponse updateProject(@Valid UpdateProjectRequest updateProjectRequest) {
        logger.info("Updating project for the given ID: {}", maskingString.maskSensitive(updateProjectRequest.getId().toString()));

        Projects project = projectRepository.findById(updateProjectRequest.getId()).orElseThrow(() -> new NotFoundException("Project not found the given ID: " + updateProjectRequest.getId()));

        logger.debug("Fetched project ID: {}", maskingString.maskSensitive(project.getId().toString()));

        if(updateProjectRequest.getName() != null)  {
            project.setName(updateProjectRequest.getName());
        }

        if(updateProjectRequest.getKey() != null) {
            project.setKey(updateProjectRequest.getKey());
        }

        Projects updatedProject = projectRepository.save(project);

        logger.debug("Updated project ID: {}", maskingString.maskSensitive(updatedProject.getId().toString()));

        return ProjectMapper.toProjectDetailsResponse(updatedProject);
    }

    @Transactional
    public ProjectDetailsResponse deleteProjectById(UUID id) {
        logger.info("Deleting project for the given ID: {}",maskingString.maskSensitive(id.toString()));

        Projects project = projectRepository.findById(id).orElseThrow(() -> new NotFoundException("Project not found for the given ID: " + id));

        logger.debug("Fetched project ID: {}", maskingString.maskSensitive(project.getId().toString()));

        projectRepository.delete(project);

        logger.debug("Deleted Project with ID: {}", maskingString.maskSensitive(project.getId().toString()));

        return ProjectMapper.toProjectDetailsResponse(project);
    }



    
}
