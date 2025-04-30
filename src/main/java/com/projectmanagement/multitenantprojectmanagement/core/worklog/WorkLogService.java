package com.projectmanagement.multitenantprojectmanagement.core.worklog;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.projectmanagement.multitenantprojectmanagement.auth0.utils.JWTUtils;
import com.projectmanagement.multitenantprojectmanagement.core.issue.Issue;
import com.projectmanagement.multitenantprojectmanagement.core.issue.IssueService;
import com.projectmanagement.multitenantprojectmanagement.core.worklog.dto.request.CreateWorklogRequest;
import com.projectmanagement.multitenantprojectmanagement.core.worklog.dto.request.UpdateWorklogRequest;
import com.projectmanagement.multitenantprojectmanagement.core.worklog.dto.response.WorklogResponse;
import com.projectmanagement.multitenantprojectmanagement.core.worklog.mapper.WorklogMapper;
import com.projectmanagement.multitenantprojectmanagement.exception.NotFoundException;
import com.projectmanagement.multitenantprojectmanagement.helper.MaskingString;
import com.projectmanagement.multitenantprojectmanagement.organizations.Organizations;
import com.projectmanagement.multitenantprojectmanagement.organizations.OrganizationsService;
import com.projectmanagement.multitenantprojectmanagement.users.UserService;
import com.projectmanagement.multitenantprojectmanagement.users.Users;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class WorkLogService {

    private final WorkLogRepository workLogRepository;
    private final IssueService issueService;
    private final UserService userService;
    private static final Logger logger = LoggerFactory.getLogger(WorkLogService.class);
    private final MaskingString maskingString;
    private final OrganizationsService organizationsService;
    private final JWTUtils jwtUtils;

    public WorkLog getWorklogById(UUID id) {
        logger.info("Getting worklog for the given ID: {}", maskingString.maskSensitive(id.toString()));

        String auth0OrgId = jwtUtils.getAuth0OrgId();

        WorkLog workLog = workLogRepository.findByIdAndOrganization_Auth0Id(id, auth0OrgId).orElseThrow(() -> new NotFoundException("Worklog not found for the given ID: " + id));

        logger.debug("Fetched worklog ID: {}", maskingString.maskSensitive(workLog.getId().toString()));

        return workLog;
    }

    @Transactional
    public WorklogResponse createWorklog(@Valid CreateWorklogRequest createWorklogRequest) {
        logger.info("Creating worklog for the given Issue ID: {}", maskingString.maskSensitive(createWorklogRequest.getIssueId().toString()));

        Issue issue = issueService.getIssueById(createWorklogRequest.getIssueId());

        Users user = userService.getUserEntity(createWorklogRequest.getUserId());

        String auth0OrgId = jwtUtils.getAuth0OrgId();

        Organizations organization = organizationsService.getOrganizationByAuth0Id(auth0OrgId);

        WorkLog workLog = WorklogMapper.toworklogEntity(createWorklogRequest, issue, user, organization);

        WorkLog savedWorkLog = workLogRepository.save(workLog);

        logger.debug("Created worklog ID: {}", maskingString.maskSensitive(savedWorkLog.getId().toString()));

        return WorklogMapper.toWorklogResponse(savedWorkLog);
    }

    @Transactional
    public WorklogResponse updateWorklog(@Valid UpdateWorklogRequest updateWorklogRequest) {
        logger.info("Updating worklog for the given ID: {}", maskingString.maskSensitive(updateWorklogRequest.getId().toString()));

        WorkLog workLog = getWorklogById(updateWorklogRequest.getId());

        WorkLog updateWorkLogEntity = WorklogMapper.toUpdateWorklogEntity(updateWorklogRequest, workLog);

        WorkLog updatedWorklog = workLogRepository.save(updateWorkLogEntity);

        logger.debug("Updated worklog ID: {}", maskingString.maskSensitive(updatedWorklog.getId().toString()));

        return WorklogMapper.toWorklogResponse(updatedWorklog);
    }

    @Transactional
    public WorklogResponse deleteWorklogById(UUID id) {
        logger.info("Deleting worklog for the given ID: {}", maskingString.maskSensitive(id.toString()));

        WorkLog workLog = getWorklogById(id);

        Issue issue = workLog.getIssue();
        if(issue != null) {
            issue.setWorkLog(null);
        }

        workLogRepository.delete(workLog);

        logger.debug("Deleted worklog ID: {}", maskingString.maskSensitive(id.toString()));

        return WorklogMapper.toWorklogResponse(workLog);
    }


}
