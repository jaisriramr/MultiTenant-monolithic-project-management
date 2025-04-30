package com.projectmanagement.multitenantprojectmanagement.core.watcher;

import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import com.projectmanagement.multitenantprojectmanagement.auth0.utils.JWTUtils;
import com.projectmanagement.multitenantprojectmanagement.core.issue.Issue;
import com.projectmanagement.multitenantprojectmanagement.core.issue.IssueService;
import com.projectmanagement.multitenantprojectmanagement.core.issue.event.IssueEvent;

import com.projectmanagement.multitenantprojectmanagement.core.watcher.dto.response.WatcherResponse;
import com.projectmanagement.multitenantprojectmanagement.core.watcher.mapper.WatcherMapper;
import com.projectmanagement.multitenantprojectmanagement.exception.NotFoundException;
import com.projectmanagement.multitenantprojectmanagement.helper.MaskingString;
import com.projectmanagement.multitenantprojectmanagement.organizations.Organizations;
import com.projectmanagement.multitenantprojectmanagement.organizations.OrganizationsService;
import com.projectmanagement.multitenantprojectmanagement.users.UserService;
import com.projectmanagement.multitenantprojectmanagement.users.Users;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class WatcherService {

    private final WatcherRepository watcherRepository;
    private static final Logger logger = LoggerFactory.getLogger(WatcherService.class);
    private final MaskingString maskingString;
    private final IssueService issueService;
    private final UserService userService;
    private final OrganizationsService organizationsService;
    private final JWTUtils jwtUtils;

    @EventListener
    public void createWatcherViaEvent(IssueEvent event) {
        UUID issueId = event.getIssueId();
        UUID reporterId = event.getReportId();

        createWatcher(issueId, reporterId);
    }

    public Watcher getWatcherById(UUID id) {
        logger.info("Getting watcher by ID: {}", maskingString.maskSensitive(id.toString()));

        String auth0OrgId = jwtUtils.getAuth0OrgId();

        Watcher watcher = watcherRepository.findByIdAndOrganization_Auth0Id(id, auth0OrgId).orElseThrow(() -> new NotFoundException("Watcher not found for the given ID: " + id));

        logger.debug("Fetched watcher ID: {}", maskingString.maskSensitive(watcher.getId().toString()));

        return watcher;
    }

    public List<WatcherResponse> getWatchersByIssueId(UUID id) {
        logger.info("Getting watchers for the given issue ID: {}",maskingString.maskSensitive(id.toString()));

        String auth0OrgId = jwtUtils.getAuth0OrgId();
        
        List<Watcher> watchers = watcherRepository.findAllByIssueIdAndOrganization_Auth0Id(id, auth0OrgId);

        logger.debug("Fetched {} watchers", watchers.size());

        return WatcherMapper.toListWatcherResponses(watchers);
    }

    @Transactional
    public WatcherResponse createWatcher(UUID issueId, UUID userId) {
        logger.info("Creating watcher");

        Issue issue = issueService.getIssueById(issueId);

        Users user = userService.getUserEntity(userId);

        String auth0OrgId = jwtUtils.getAuth0OrgId();

        Organizations organization = organizationsService.getOrganizationByAuth0Id(auth0OrgId);

        Watcher watcher = new Watcher();
        watcher.setIssue(issue);
        watcher.setUser(user);
        watcher.setOrganization(organization);

        Watcher savedWathWatcher = watcherRepository.save(watcher);

        logger.debug("Saved watched ID: {}", maskingString.maskSensitive(savedWathWatcher.getId().toString()));

        return WatcherMapper.toWatcherResponse(savedWathWatcher);
    }

    @Transactional
    public WatcherResponse removeWatcher(UUID id) {
        logger.info("Deleting watcher");

        Watcher watcher = getWatcherById(id);

        logger.debug("Fetched watcher ID: {}", maskingString.maskSensitive(id.toString()));

        watcherRepository.delete(watcher);

        logger.debug("Deleted watcher for the given ID: {}", maskingString.maskSensitive(id.toString()));

        return WatcherMapper.toWatcherResponse(watcher);
    }



}