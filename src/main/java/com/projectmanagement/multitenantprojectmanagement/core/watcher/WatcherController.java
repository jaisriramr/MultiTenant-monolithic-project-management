package com.projectmanagement.multitenantprojectmanagement.core.watcher;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.projectmanagement.multitenantprojectmanagement.core.watcher.dto.request.WatcherRequest;
import com.projectmanagement.multitenantprojectmanagement.core.watcher.dto.response.WatcherResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class WatcherController {

    private final WatcherService watcherService;

    @GetMapping("/v1/watcher/{id}")
    public ResponseEntity<WatcherResponse> getWatcherById(@PathVariable UUID id) {
        WatcherResponse watcherResponse = watcherService.getWatcherById(id);

        return ResponseEntity.ok(watcherResponse);
    }

    @GetMapping("/v1/watcher/{id}/issue")
    public ResponseEntity<List<WatcherResponse>> getWatchersByIssueId(@PathVariable UUID id) {
        List<WatcherResponse> watcherResponses = watcherService.getWatchersByIssueId(id);

        return ResponseEntity.ok(watcherResponses);
    }

    @PostMapping("/v1/watcher")
    public ResponseEntity<WatcherResponse> createWatcher(@RequestBody WatcherRequest watcherRequest) {
        WatcherResponse watcherResponse = watcherService.createWatcher(watcherRequest.getIssueId(), watcherRequest.getUserId());

        return ResponseEntity.ok(watcherResponse);
    }

    @DeleteMapping("/v1/watcher/{id}")
    public ResponseEntity<WatcherResponse> deleteWatcherById(@PathVariable UUID id) {
        WatcherResponse watcherResponse = watcherService.removeWatcher(id);

        return ResponseEntity.ok(watcherResponse);
    }

}
