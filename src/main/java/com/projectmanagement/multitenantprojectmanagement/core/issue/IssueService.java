package com.projectmanagement.multitenantprojectmanagement.core.issue;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.projectmanagement.multitenantprojectmanagement.core.issue.dto.request.CreateIssueRequest;
import com.projectmanagement.multitenantprojectmanagement.core.issue.dto.response.IssueResponse;
import com.projectmanagement.multitenantprojectmanagement.core.issue.dto.response.ListIssuesResponse;
import com.projectmanagement.multitenantprojectmanagement.core.issue.mapper.IssueMapper;
import com.projectmanagement.multitenantprojectmanagement.core.project.ProjectService;
import com.projectmanagement.multitenantprojectmanagement.core.project.Projects;
import com.projectmanagement.multitenantprojectmanagement.core.sprint.Sprint;
import com.projectmanagement.multitenantprojectmanagement.core.sprint.SprintService;
import com.projectmanagement.multitenantprojectmanagement.core.workflow.status.StatusService;
import com.projectmanagement.multitenantprojectmanagement.exception.NotFoundException;
import com.projectmanagement.multitenantprojectmanagement.helper.MaskingString;
import com.projectmanagement.multitenantprojectmanagement.organizations.dto.response.PaginatedResponseDto;
import com.projectmanagement.multitenantprojectmanagement.users.UserService;
import com.projectmanagement.multitenantprojectmanagement.users.Users;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class IssueService {

    private final IssueRepository issueRepository;
    private final MaskingString maskingString;
    private static final Logger logger = LoggerFactory.getLogger(IssueService.class);
    private final ProjectService projectService;
    private final SprintService sprintService;
    private final UserService userService;

    public IssueResponse getIssueByKey(String key) {
        logger.info("Getting issue for the given key: {}", key);
        
        Issue issue = issueRepository.findByKey(key).orElseThrow(() -> new NotFoundException("Issue not found for the given Key: " + key));

        logger.debug("Fetched issue ID: {}", maskingString.maskSensitive(issue.getId().toString()));

        return IssueMapper.toIssueResponse(issue);
    }

    public PaginatedResponseDto<ListIssuesResponse> getIssuesesBySprintId(UUID sprintId, Pageable pageable) {
        logger.info("Getting all issues associated with sprint ID: {}", maskingString.maskSensitive(sprintId.toString()));

        Page<Issue> issues = issueRepository.findAllBySprintId(sprintId, pageable);

        logger.debug("Fetched {} issues", issues.getTotalElements());

        return IssueMapper.toPaginatedResponse(issues);

    }

    public PaginatedResponseDto<ListIssuesResponse> getBacklogIssues(UUID projectId, Pageable pagable) {
        logger.info("Getting backlog issues for project ID: {}", maskingString.maskSensitive(projectId.toString()));

        Page<Issue> issues = issueRepository.findAllByProjectIdAndSprintIsNull(projectId, pagable);

        logger.debug("Fetched {} issues", issues.getTotalElements());

        return IssueMapper.toPaginatedResponse(issues);
    }

    @Transactional
    public ListIssuesResponse createIssue(@Valid CreateIssueRequest createIssueRequest) {

        logger.info("Creating issue for the given project ID: {}", maskingString.maskSensitive(createIssueRequest.getProjectId().toString()));

        Projects project = projectService.getProjectById(createIssueRequest.getProjectId());

        Long issueCount = project.getIssueCount() + 1;
        
        project.setIssueCount(issueCount);
        projectService.updateProjectIssueCount(createIssueRequest.getProjectId(), issueCount);


        String key = project.getKey() + "-" + issueCount;

        Users reporter = userService.getUserEntity(createIssueRequest.getReporterId());

        Sprint sprint = null;

        if(createIssueRequest.getSprintId() != null) {
            sprint = sprintService.getSprintEntity(createIssueRequest.getSprintId());
        }

        Issue issue = IssueMapper.toIssueEntity(createIssueRequest, project, reporter, sprint, key);
        
        Issue savedIssue = issueRepository.save(issue);

        logger.debug("Saved Issue ID: {}", maskingString.maskSensitive(savedIssue.getId().toString()));

        return IssueMapper.toListIssuesResponse(savedIssue);

    }

}
