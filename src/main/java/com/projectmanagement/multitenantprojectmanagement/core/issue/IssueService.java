package com.projectmanagement.multitenantprojectmanagement.core.issue;

import java.util.ArrayList;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.projectmanagement.multitenantprojectmanagement.core.epic.Epic;
import com.projectmanagement.multitenantprojectmanagement.core.epic.EpicService;
import com.projectmanagement.multitenantprojectmanagement.core.epic.dto.request.CreateEpicRequest;
import com.projectmanagement.multitenantprojectmanagement.core.issue.dto.request.CreateEpicIssueRequest;
import com.projectmanagement.multitenantprojectmanagement.core.issue.dto.request.CreateIssueRequest;
import com.projectmanagement.multitenantprojectmanagement.core.issue.dto.request.CreateSubIssueRequest;
import com.projectmanagement.multitenantprojectmanagement.core.issue.dto.request.UpdateIssueRequest;
import com.projectmanagement.multitenantprojectmanagement.core.issue.dto.response.IssueResponse;
import com.projectmanagement.multitenantprojectmanagement.core.issue.dto.response.ListIssuesResponse;
import com.projectmanagement.multitenantprojectmanagement.core.issue.enums.IssuePriority;
import com.projectmanagement.multitenantprojectmanagement.core.issue.enums.IssueStatus;
import com.projectmanagement.multitenantprojectmanagement.core.issue.enums.IssueType;
import com.projectmanagement.multitenantprojectmanagement.core.issue.event.IssueEvent;
import com.projectmanagement.multitenantprojectmanagement.core.issue.mapper.IssueMapper;
import com.projectmanagement.multitenantprojectmanagement.core.issuerelation.IssueRelationService;
import com.projectmanagement.multitenantprojectmanagement.core.issuerelation.enums.IssueRelationType;
import com.projectmanagement.multitenantprojectmanagement.core.project.ProjectService;
import com.projectmanagement.multitenantprojectmanagement.core.project.Projects;
import com.projectmanagement.multitenantprojectmanagement.core.sprint.Sprint;
import com.projectmanagement.multitenantprojectmanagement.core.sprint.SprintService;
import com.projectmanagement.multitenantprojectmanagement.core.watcher.WatcherService;
import com.projectmanagement.multitenantprojectmanagement.core.workflow.status.StatusService;
import com.projectmanagement.multitenantprojectmanagement.exception.BadRequestException;
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
    private final EpicService epicService;
    private final IssueRelationService issueRelationService;
    private final ApplicationEventPublisher eventPublisher;

    public Issue getIssueById(UUID id) {
        logger.info("Getting issue for the given ID: {}", maskingString.maskSensitive(id.toString()));

        Issue issue = issueRepository.findById(id).orElseThrow(() -> new NotFoundException("Issue not found for the given ID: " + id));

        logger.debug("Fetched issue Id: {}", maskingString.maskSensitive(issue.getId().toString()));

        return issue;
    }

    public IssueResponse getIssueByKey(String key) {
        logger.info("Getting issue for the given key: {}", key);
        
        Issue issue = issueRepository.findByKey(key).orElseThrow(() -> new NotFoundException("Issue not found for the given Key: " + key));

        logger.debug("Fetched issue ID: {}", maskingString.maskSensitive(issue.getId().toString()));

        return IssueMapper.toIssueResponse(issue);
    }

    public PaginatedResponseDto<ListIssuesResponse> getIssuesesBySprintId(UUID sprintId, Pageable pageable) {
        logger.info("Getting all issues associated with sprint ID: {}", maskingString.maskSensitive(sprintId.toString()));

        Page<Issue> issues = issueRepository.findAllBySprintIdAndIsSubTaskFalseAndTypeNot(sprintId, IssueType.EPIC, pageable);

        logger.debug("Fetched {} issues", issues.getTotalElements());

        return IssueMapper.toPaginatedResponse(issues);

    }

    public PaginatedResponseDto<ListIssuesResponse> getBacklogIssues(UUID projectId, Pageable pagable) {
        logger.info("Getting backlog issues for project ID: {}", maskingString.maskSensitive(projectId.toString()));

        Page<Issue> issues = issueRepository.findAllByProjectIdAndSprintIsNullAndIsSubTaskFalseAndTypeNot(projectId,IssueType.EPIC, pagable);

        logger.debug("Fetched {} issues", issues.getTotalElements());

        return IssueMapper.toPaginatedResponse(issues);
    }

    public PaginatedResponseDto<ListIssuesResponse> getIssueChildWorks(UUID parentId, Pageable pageable) {
        PaginatedResponseDto<ListIssuesResponse> issues = issueRelationService.findChildWorksByParentId(parentId, pageable);

        return issues;
    }

    @Transactional
    public void assigneeUserToAnIssue(UUID issueId, UUID userId) {
        logger.info("Assigning user to an issue with ID: {}", maskingString.maskSensitive(issueId.toString()));

        Issue issue = getIssueById(issueId);

        Users user = userService.getUserEntity(userId);

        issue.setAssignee(user);

        issueRepository.save(issue);

        logger.debug("User Assigned to the given issue ID: {}", maskingString.maskSensitive(issue.getId().toString()));
    }

    @Transactional
    public void unAssigneeUserToAnIssue(UUID issueId) {
        logger.info("Assigning user to an issue with ID: {}", maskingString.maskSensitive(issueId.toString()));

        Issue issue = getIssueById(issueId);

        issue.setAssignee(null);

        issueRepository.save(issue);

        logger.debug("User Assigned to the given issue ID: {}", maskingString.maskSensitive(issue.getId().toString()));
    }

    @Transactional
    public ListIssuesResponse linkIssueToEpic(UUID epicParentId, UUID issueId) {
        logger.info("Linking issue with ID: {} to epic ID: {}", maskingString.maskSensitive(issueId.toString()), maskingString.maskSensitive(epicParentId.toString()));

        Issue parent = getIssueById(epicParentId);

        if(!"EPIC".equals(parent.getType().toString())) {
            throw new BadRequestException("Given parent id is not epic");
        }

        Issue childWork = getIssueById(issueId);

        if(parent.getEpic().getLinkedIssues() == null) {
            parent.getEpic().setLinkedIssues(new ArrayList<>());
        }

        childWork.setEpic(parent.getEpic());

        Issue savedIssue = issueRepository.save(childWork);

        issueRelationService.CreateIssueRelation(parent, childWork, IssueRelationType.SUB_TASK);

        logger.debug("Linked child work ID: {}", maskingString.maskSensitive(savedIssue.getId().toString()));

        return IssueMapper.toListIssuesResponse(savedIssue);

    }

    @Transactional
    public ListIssuesResponse unlinkIssueToEpic(UUID issueId) {
        logger.info("UnLinking issue with ID: {}", maskingString.maskSensitive(issueId.toString()));

        Issue childWork = getIssueById(issueId);

        childWork.setEpic(null);

        Issue savedIssue = issueRepository.save(childWork);

        issueRelationService.deleteIssueRelationById(issueId);

        logger.debug("UnLinked child work ID: {}", maskingString.maskSensitive(savedIssue.getId().toString()));

        return IssueMapper.toListIssuesResponse(savedIssue);

    }

    @Transactional
    public ListIssuesResponse createEpicIssue(@Valid CreateEpicIssueRequest createEpicIssueRequest) {

        logger.info("Creating epic issue for the given project ID: {}", maskingString.maskSensitive(createEpicIssueRequest.getProjectId().toString()));

        Projects project = projectService.getProjectById(createEpicIssueRequest.getProjectId());

        Long issueCount = project.getIssueCount() + 1;
        
        project.setIssueCount(issueCount);
        projectService.updateProjectIssueCount(createEpicIssueRequest.getProjectId(), issueCount);

        String key = project.getKey() + "-" + issueCount;

        Users reporter = userService.getUserEntity(createEpicIssueRequest.getReporterId());

        Sprint sprint = null;

        if(createEpicIssueRequest.getSprintId() != null) {
            sprint = sprintService.getSprintEntity(createEpicIssueRequest.getSprintId());
        }

        CreateEpicRequest epicRequest = IssueMapper.toCreateEpicIssueRequest(createEpicIssueRequest);

        Epic epic = epicService.createEpicEntity(epicRequest);

        Issue issue = IssueMapper.toEpicIssueEntity(createEpicIssueRequest, project, sprint, reporter, key, epic);

        Issue savedIssue = issueRepository.save(issue);

        logger.debug("Saved epic issue ID: {}", maskingString.maskSensitive(savedIssue.getId().toString()));

        return IssueMapper.toListIssuesResponse(savedIssue);

    }

    @Transactional
    public ListIssuesResponse createSubIssue(@Valid CreateSubIssueRequest createSubIssueRequest) {
        logger.info("Creating sub issue for the given project ID: {}", maskingString.maskSensitive(createSubIssueRequest.getProjectId().toString()));

        Projects project = projectService.getProjectById(createSubIssueRequest.getProjectId());

        Issue Parent = getIssueById(createSubIssueRequest.getIssueId());

        Long issueCount = project.getIssueCount() + 1;
        
        project.setIssueCount(issueCount);
        projectService.updateProjectIssueCount(createSubIssueRequest.getProjectId(), issueCount);


        String key = project.getKey() + "-" + issueCount;

        Users reporter = userService.getUserEntity(createSubIssueRequest.getReporterId());

        Sprint sprint = null;

        if(createSubIssueRequest.getSprintId() != null) {
            sprint = sprintService.getSprintEntity(createSubIssueRequest.getSprintId());
        }

        Issue issue = IssueMapper.toSubIssueEntity(createSubIssueRequest, project, sprint,reporter, key, Parent);
        
        Issue savedIssue = issueRepository.save(issue);

        issueRelationService.CreateIssueRelation(Parent, savedIssue, IssueRelationType.SUB_TASK);

        logger.debug("Saved sub issue ID: {}", maskingString.maskSensitive(savedIssue.getId().toString()));

        eventPublisher.publishEvent(new IssueEvent(this, savedIssue.getId(), reporter.getId()));

        return IssueMapper.toListIssuesResponse(savedIssue);
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

        eventPublisher.publishEvent(new IssueEvent(this, savedIssue.getId(), reporter.getId()));

        logger.debug("Saved Issue ID: {}", maskingString.maskSensitive(savedIssue.getId().toString()));

        return IssueMapper.toListIssuesResponse(savedIssue);

    }

    // @Transactional
    public IssueResponse updateIssue(@Valid UpdateIssueRequest updateIssueRequest) {
        logger.info("Updating issue for the given ID: {}", maskingString.maskSensitive(updateIssueRequest.getId().toString()));

        Issue issue = issueRepository.findById(updateIssueRequest.getId()).orElseThrow(() -> new NotFoundException("Issue not found for the given ID: " + updateIssueRequest.getId()));

        if(updateIssueRequest.getTitle() != null) {
            issue.setTitle(updateIssueRequest.getTitle());
        }

        if(updateIssueRequest.getDescription() != null) {
            issue.setDescription(updateIssueRequest.getDescription());
        }

        if(updateIssueRequest.getStatus() != null) {
            if("TO_DO".equals(updateIssueRequest.getStatus())) {
                issue.setStatus(IssueStatus.TO_DO);
            }else if("IN_PROGRESS".equals(updateIssueRequest.getStatus())) {
                issue.setStatus(IssueStatus.IN_PROGRESS);
            }else if("IN_REVIEW".equals(updateIssueRequest.getStatus())) {
                issue.setStatus(IssueStatus.IN_REVIEW);
            }else if("DONE".equals(updateIssueRequest.getStatus())) {
                issue.setStatus(IssueStatus.DONE);
            }else {
                throw new IllegalArgumentException("Given status is not allowed");
            }
        }

        if(updateIssueRequest.getType() != null) {
            if("TASK".equals(updateIssueRequest.getType())) {
                issue.setType(IssueType.TASK);
            }else if("BUG".equals(updateIssueRequest.getType())) {
                issue.setType(IssueType.BUG);
            }else if("STORY".equals(updateIssueRequest.getType())) {
                issue.setType(IssueType.STORY);
            }else if("EPIC".equals(updateIssueRequest.getType())) {
                issue.setType(IssueType.EPIC);
            }else {
                throw new IllegalArgumentException("Given type is not allowed");
            }
        }

        if(updateIssueRequest.getPriority() != null) {
            if("LOW".equals(updateIssueRequest.getPriority())) {
                issue.setPriority(IssuePriority.LOW);
            }else if("MEDIUM".equals(updateIssueRequest.getPriority())) {
                issue.setPriority(IssuePriority.MEDIUM);
            }else if("HIGH".equals(updateIssueRequest.getPriority())) {
                issue.setPriority(IssuePriority.HIGH);
            }else if("CRITICAL".equals(updateIssueRequest.getPriority())) {
                issue.setPriority(IssuePriority.CRITICAL);
            }else {
                throw new IllegalArgumentException("Given type is not allowed");
            }
        }

        if(updateIssueRequest.getSprintId() != null) {

            Sprint sprint = sprintService.getSprintEntity(updateIssueRequest.getSprintId());

            issue.setSprint(sprint);
        }

        if(updateIssueRequest.getAssigneeId() != null) {
            Users user = userService.getUserEntity(updateIssueRequest.getAssigneeId());

            issue.setAssignee(user);
        }

        if(updateIssueRequest.getReporterId() != null) {
            Users user = userService.getUserEntity(updateIssueRequest.getReporterId());

            issue.setReporter(user);
        }

        if(updateIssueRequest.getStoryPoints() != null) {
            issue.setStoryPoints(updateIssueRequest.getStoryPoints());
        }

        Issue updatedIssue = issueRepository.saveAndFlush(issue);

        logger.debug("Updated issue ID: {}", maskingString.maskSensitive(updatedIssue.getId().toString()));

        return IssueMapper.toIssueResponse(updatedIssue);
    }

}
