package com.projectmanagement.multitenantprojectmanagement.core.issue;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.projectmanagement.multitenantprojectmanagement.core.issue.dto.request.CreateIssueRequest;
import com.projectmanagement.multitenantprojectmanagement.core.issue.dto.response.ListIssuesResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class IssueController {

    private final IssueService issueService;

    @PostMapping("/v1/issue")
    public ResponseEntity<ListIssuesResponse> createIssue(@Valid @RequestBody CreateIssueRequest createIssueRequest) {
        ListIssuesResponse response = issueService.createIssue(createIssueRequest);

        return ResponseEntity.ok(response);
    }

}
