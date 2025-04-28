package com.projectmanagement.multitenantprojectmanagement.core.issue;

import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.projectmanagement.multitenantprojectmanagement.core.issue.dto.request.CreateEpicIssueRequest;
import com.projectmanagement.multitenantprojectmanagement.core.issue.dto.request.CreateIssueRequest;
import com.projectmanagement.multitenantprojectmanagement.core.issue.dto.request.CreateSubIssueRequest;
import com.projectmanagement.multitenantprojectmanagement.core.issue.dto.request.UpdateIssueRequest;
import com.projectmanagement.multitenantprojectmanagement.core.issue.dto.response.IssueResponse;
import com.projectmanagement.multitenantprojectmanagement.core.issue.dto.response.ListIssuesResponse;
import com.projectmanagement.multitenantprojectmanagement.core.issue.mapper.IssueMapper;
import com.projectmanagement.multitenantprojectmanagement.organizations.dto.response.PaginatedResponseDto;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class IssueController {

    private final IssueService issueService;

    @GetMapping("/v1/issue/{id}")
    public ResponseEntity<IssueResponse> getIssueById(@PathVariable UUID id,@AuthenticationPrincipal Jwt jwt) {
        Issue issue = issueService.getIssueById(id);

        return ResponseEntity.ok(IssueMapper.toIssueResponse(issue));
    }

    @GetMapping("/v1/issue")
    public ResponseEntity<IssueResponse> getIssueByKey(@RequestParam String key,@AuthenticationPrincipal Jwt jwt) {
        IssueResponse issue = issueService.getIssueByKey(key);

        return ResponseEntity.ok(issue);
    }

    @GetMapping("/v1/issue/{id}/backlog")
    public ResponseEntity<PaginatedResponseDto<ListIssuesResponse>> getProjectBacklogs(@PathVariable UUID id, Pageable pageable,@AuthenticationPrincipal Jwt jwt) {
        PaginatedResponseDto<ListIssuesResponse> issues = issueService.getBacklogIssues(id, pageable);

        return ResponseEntity.ok(issues);
    }

    @GetMapping("/v1/issue/{id}/sprint")
    public ResponseEntity<PaginatedResponseDto<ListIssuesResponse>> getSprintIssues(@PathVariable UUID id, Pageable pageable,@AuthenticationPrincipal Jwt jwt) {
        PaginatedResponseDto<ListIssuesResponse> issues = issueService.getIssuesesBySprintId(id, pageable);

        return ResponseEntity.ok(issues);
    }

    @GetMapping("/v1/issue/{id}/child-works")
    public ResponseEntity<PaginatedResponseDto<ListIssuesResponse>> getIssueChildWorks(@PathVariable UUID id, Pageable pageable,@AuthenticationPrincipal Jwt jwt) {
        PaginatedResponseDto<ListIssuesResponse> issues = issueService.getIssueChildWorks(id, pageable);

        return ResponseEntity.ok(issues);
    }

    @PostMapping("/v1/issue/link/{id}/epic/{epicId}")
    public ResponseEntity<ListIssuesResponse> linkIssueToEpic(@PathVariable UUID id, @PathVariable UUID epicId,@AuthenticationPrincipal Jwt jwt) {
        ListIssuesResponse issue = issueService.linkIssueToEpic(epicId, id);

        return ResponseEntity.ok(issue);
    }

    @PostMapping("/v1/issue/unlink/{id}/epic")
    public ResponseEntity<ListIssuesResponse> unlinkIssueToEpic(@PathVariable UUID id,@AuthenticationPrincipal Jwt jwt) {
        ListIssuesResponse issue = issueService.unlinkIssueToEpic(id);

        return ResponseEntity.ok(issue);
    }

    @PostMapping("/v1/issue")
    public ResponseEntity<ListIssuesResponse> createIssue(@Valid @RequestBody CreateIssueRequest createIssueRequest,@AuthenticationPrincipal Jwt jwt) {
        ListIssuesResponse response = issueService.createIssue(createIssueRequest);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/v1/issue/sub-task")
    public ResponseEntity<ListIssuesResponse> createSubIssue(@Valid @RequestBody CreateSubIssueRequest createSubIssueRequest,@AuthenticationPrincipal Jwt jwt) {
        ListIssuesResponse response = issueService.createSubIssue(createSubIssueRequest);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/v1/issue/epic")
    public ResponseEntity<ListIssuesResponse> createEpicIssue(@Valid @RequestBody CreateEpicIssueRequest createEpicIssueRequest,@AuthenticationPrincipal Jwt jwt) {
        ListIssuesResponse response = issueService.createEpicIssue(createEpicIssueRequest);

        return ResponseEntity.ok(response);
    }

    @PutMapping("/v1/issue")
    public ResponseEntity<IssueResponse> updateIssue(@Valid @RequestBody UpdateIssueRequest updateIssueRequest,@AuthenticationPrincipal Jwt jwt) {
        IssueResponse response = issueService.updateIssue(updateIssueRequest);

        return ResponseEntity.ok(response);
    }

    @PutMapping("/v1/issue/{id}/assign/{userId}/user")
    public ResponseEntity<String> assigneeUserToAnIssue(@PathVariable UUID id, @PathVariable UUID userId,@AuthenticationPrincipal Jwt jwt) {
        issueService.assigneeUserToAnIssue(id, userId);

        return ResponseEntity.ok("User successfully assigned to the given User ID: "+ id);
    }

    @PutMapping("/v1/issue/{id}/unassign")
    public ResponseEntity<String> unassigneeUserToAnIssue(@PathVariable UUID id,@AuthenticationPrincipal Jwt jwt) {
        issueService.unAssigneeUserToAnIssue(id);

        return ResponseEntity.ok("User successfully removed to the given User ID: "+ id);
    }

}
