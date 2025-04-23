package com.projectmanagement.multitenantprojectmanagement.core.label;

import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.projectmanagement.multitenantprojectmanagement.core.issue.Issue;
import com.projectmanagement.multitenantprojectmanagement.core.issue.IssueService;
import com.projectmanagement.multitenantprojectmanagement.core.label.dto.request.CreateLabelRequest;
import com.projectmanagement.multitenantprojectmanagement.core.label.dto.response.LabelResponse;
import com.projectmanagement.multitenantprojectmanagement.core.label.mapper.LabelMapper;
import com.projectmanagement.multitenantprojectmanagement.core.project.ProjectService;
import com.projectmanagement.multitenantprojectmanagement.core.project.Projects;
import com.projectmanagement.multitenantprojectmanagement.exception.NotFoundException;
import com.projectmanagement.multitenantprojectmanagement.helper.MaskingString;
import com.projectmanagement.multitenantprojectmanagement.organizations.dto.response.PaginatedResponseDto;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LabelService {

    private final LabelRepository labelRepository;
    private final MaskingString maskingString;
    private final ProjectService projectService;
    private final IssueService issueService;
    private static final Logger logger = LoggerFactory.getLogger(IssueService.class);

    public Label getLabelEntity(UUID id) {
        logger.info("Getting label for the given ID: {}", maskingString.maskSensitive(id.toString()));
        
        Label label = labelRepository.findById(id).orElseThrow(() -> new NotFoundException("Label not found for the given ID: " + id));

        logger.debug("Fetched label ID: {}", maskingString.maskSensitive(label.getId().toString()));
        return label;
    }

    public LabelResponse getLabelById(UUID id) {
        Label label = getLabelEntity(id);

        return LabelMapper.toLabelResponse(label);
    }

    public PaginatedResponseDto<LabelResponse> getLabelsByProjectId(UUID id, Pageable pageable) {
        logger.info("Getting labels for the given project ID: {}", maskingString.maskSensitive(id.toString()));

        Page<Label> labels = labelRepository.findAllByProjectId(id, pageable);

        logger.debug("Fetched {} labels", labels.getTotalElements());

        return LabelMapper.toPaginatedResponse(labels);
    }

    public Label createLabel(CreateLabelRequest createLabelRequest) {

        logger.info("Creating label");

        Projects project = projectService.getProjectById(createLabelRequest.getProjectId());

        Issue issue = issueService.getIssueById(createLabelRequest.getIssueId());

        Label label = LabelMapper.toLabelEntity(createLabelRequest, project, issue);

        Label savedLabel = labelRepository.save(label);

        logger.debug("Created label ID: {}", maskingString.maskSensitive(savedLabel.getId().toString()));

        return savedLabel;
    }

    public LabelResponse deleteLabelById(UUID id) {
        logger.info("Deleting label for the given ID: {}", maskingString.maskSensitive(id.toString()));

        Label label = getLabelEntity(id);

        labelRepository.delete(label);

        logger.debug("Deleted label with ID: {}", maskingString.maskSensitive(id.toString()));

        return LabelMapper.toLabelResponse(label);
    }

}
