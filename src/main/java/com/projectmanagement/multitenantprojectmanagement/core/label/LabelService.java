package com.projectmanagement.multitenantprojectmanagement.core.label;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.projectmanagement.multitenantprojectmanagement.auth0.utils.JWTUtils;
import com.projectmanagement.multitenantprojectmanagement.core.activity.ActivityService;
import com.projectmanagement.multitenantprojectmanagement.core.activity.dto.response.ActivityResponse;
import com.projectmanagement.multitenantprojectmanagement.core.activity.mapper.ActivityMapper;
import com.projectmanagement.multitenantprojectmanagement.core.issue.Issue;
import com.projectmanagement.multitenantprojectmanagement.core.issue.IssueService;
import com.projectmanagement.multitenantprojectmanagement.core.label.dto.request.CreateLabelRequest;
import com.projectmanagement.multitenantprojectmanagement.core.label.dto.response.LabelResponse;
import com.projectmanagement.multitenantprojectmanagement.core.label.mapper.LabelMapper;
import com.projectmanagement.multitenantprojectmanagement.core.notification.RedisSubscriber;
import com.projectmanagement.multitenantprojectmanagement.core.project.ProjectService;
import com.projectmanagement.multitenantprojectmanagement.core.project.Projects;
import com.projectmanagement.multitenantprojectmanagement.exception.NotFoundException;
import com.projectmanagement.multitenantprojectmanagement.helper.MaskingString;
import com.projectmanagement.multitenantprojectmanagement.organizations.Organizations;
import com.projectmanagement.multitenantprojectmanagement.organizations.OrganizationsService;
import com.projectmanagement.multitenantprojectmanagement.organizations.dto.response.PaginatedResponseDto;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LabelService {

    private final LabelRepository labelRepository;
    private final MaskingString maskingString;
    private final ProjectService projectService;
    private final IssueService issueService;
    private static final Logger logger = LoggerFactory.getLogger(IssueService.class);
    private final JWTUtils jwtUtils;
    private final OrganizationsService organizationsService;
    private final ActivityService activityService;
    private final RedisSubscriber redisSubscriber;

    public Label getLabelEntity(UUID id) {
        logger.info("Getting label for the given ID: {}", maskingString.maskSensitive(id.toString()));
        
        String auth0OrgId = jwtUtils.getAuth0OrgId();

        Label label = labelRepository.findByIdAndOrganization_Auth0Id(id, auth0OrgId).orElseThrow(() -> new NotFoundException("Label not found for the given ID: " + id));

        logger.debug("Fetched label ID: {}", maskingString.maskSensitive(label.getId().toString()));
        return label;
    }

    public LabelResponse getLabelById(UUID id) {
        Label label = getLabelEntity(id);

        return LabelMapper.toLabelResponse(label);
    }

    public PaginatedResponseDto<LabelResponse> getLabelsByProjectId(UUID id, Pageable pageable) {
        logger.info("Getting labels for the given project ID: {}", maskingString.maskSensitive(id.toString()));

        String auth0OrgId = jwtUtils.getAuth0OrgId();

        Page<Label> labels = labelRepository.findAllByProjectIdAndOrganization_Auth0Id(id, auth0OrgId,pageable);

        logger.debug("Fetched {} labels", labels.getTotalElements());

        return LabelMapper.toPaginatedResponse(labels);
    }

    @Transactional
    public Label createLabel(CreateLabelRequest createLabelRequest) {

        logger.info("Creating label");

        Projects project = projectService.getProjectById(createLabelRequest.getProjectId());

        Issue issue = issueService.getIssueById(createLabelRequest.getIssueId());

        String auth0OrgId = jwtUtils.getAuth0OrgId();

        Organizations organization = organizationsService.getOrganizationByAuth0Id(auth0OrgId);

        Label label = LabelMapper.toLabelEntity(createLabelRequest, project, issue, organization);

        Label savedLabel = labelRepository.save(label);

        logger.debug("Created label ID: {}", maskingString.maskSensitive(savedLabel.getId().toString()));



        ActivityResponse activityResponse = activityService.createActivity(ActivityMapper.
                                                toCreateActivityRequest(label.getId(), 
                                                                            createLabelRequest.getProjectId(), 
                                                                            auth0OrgId, 
                                                                            "Created label", 
                                                                            "updated the", 
                                                                            "Labels", 
                                                                            issue.getLabels().size() == 0 ? "None" : issue.getLabels().stream().map(Label::getName).collect(Collectors.joining(" ")), 
                                                                            issue.getLabels().stream().map(Label::getName).collect(Collectors.joining(" ")), 
                                                                            "Label", 
                                                                            null));

        redisSubscriber.sendNotification(activityResponse.getId().toString());

        return savedLabel;
    }

    @Transactional
    public LabelResponse deleteLabelById(UUID id) {
        logger.info("Deleting label for the given ID: {}", maskingString.maskSensitive(id.toString()));

        Label label = getLabelEntity(id);

        labelRepository.delete(label);

        logger.debug("Deleted label with ID: {}", maskingString.maskSensitive(id.toString()));

        return LabelMapper.toLabelResponse(label);
    }

}
