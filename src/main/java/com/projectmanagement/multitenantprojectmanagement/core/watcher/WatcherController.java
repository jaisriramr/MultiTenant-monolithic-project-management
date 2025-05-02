package com.projectmanagement.multitenantprojectmanagement.core.watcher;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.projectmanagement.multitenantprojectmanagement.core.watcher.dto.request.WatcherRequest;
import com.projectmanagement.multitenantprojectmanagement.core.watcher.dto.response.WatcherResponse;
import com.projectmanagement.multitenantprojectmanagement.core.watcher.mapper.WatcherMapper;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class WatcherController {

    private final WatcherService watcherService;

    @PreAuthorize("@organizationMembersService.hasPermission(#jwt.subject, {\"view:task\"}, #jwt.claims[\"org_id\"])")
    @GetMapping("/v1/watcher/{id}")
    public ResponseEntity<WatcherResponse> getWatcherById(@PathVariable UUID id,@AuthenticationPrincipal Jwt jwt) {
        Watcher watcherResponse = watcherService.getWatcherById(id);

        return ResponseEntity.ok(WatcherMapper.toWatcherResponse(watcherResponse));
    }

    @PreAuthorize("@organizationMembersService.hasPermission(#jwt.subject, {\"view:task\"}, #jwt.claims[\"org_id\"])")
    @GetMapping("/v1/watcher/{id}/issue")
    public ResponseEntity<List<WatcherResponse>> getWatchersByIssueId(@PathVariable UUID id,@AuthenticationPrincipal Jwt jwt) {
        List<WatcherResponse> watcherResponses = watcherService.getWatchersByIssueId(id);

        return ResponseEntity.ok(watcherResponses);
    }

    @PreAuthorize("@organizationMembersService.hasPermission(#jwt.subject, {\"create:task\"}, #jwt.claims[\"org_id\"])")
    @PostMapping("/v1/watcher")
    public ResponseEntity<WatcherResponse> createWatcher(@RequestBody WatcherRequest watcherRequest,@AuthenticationPrincipal Jwt jwt) {
        WatcherResponse watcherResponse = watcherService.createWatcher(watcherRequest.getIssueId(), watcherRequest.getUserId());

        return ResponseEntity.ok(watcherResponse);
    }

    @PreAuthorize("@organizationMembersService.hasPermission(#jwt.subject, {\"delete:task\"}, #jwt.claims[\"org_id\"])")
    @DeleteMapping("/v1/watcher/{id}")
    public ResponseEntity<WatcherResponse> deleteWatcherById(@PathVariable UUID id,@AuthenticationPrincipal Jwt jwt) {
        WatcherResponse watcherResponse = watcherService.removeWatcher(id);

        return ResponseEntity.ok(watcherResponse);
    }

}
