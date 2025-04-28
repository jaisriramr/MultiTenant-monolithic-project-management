package com.projectmanagement.multitenantprojectmanagement.core.worklog;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.projectmanagement.multitenantprojectmanagement.core.worklog.dto.request.CreateWorklogRequest;
import com.projectmanagement.multitenantprojectmanagement.core.worklog.dto.request.UpdateWorklogRequest;
import com.projectmanagement.multitenantprojectmanagement.core.worklog.dto.response.WorklogResponse;
import com.projectmanagement.multitenantprojectmanagement.core.worklog.mapper.WorklogMapper;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class WorkLogController {

    private final WorkLogService workLogService;
    
    @GetMapping("/v1/worklog/{id}")
    public ResponseEntity<WorklogResponse> getWorklogById(@PathVariable UUID id, @AuthenticationPrincipal Jwt jwt) {
        WorkLog workLog = workLogService.getWorklogById(id);

        return ResponseEntity.ok(WorklogMapper.toWorklogResponse(workLog));
    }

    @PostMapping("/v1/worklog")
    public ResponseEntity<WorklogResponse> createWorklog(@RequestBody CreateWorklogRequest createWorklogRequest, @AuthenticationPrincipal Jwt jwt) {
        WorklogResponse workLog = workLogService.createWorklog(createWorklogRequest);

        return ResponseEntity.ok(workLog);
    }

    @PutMapping("/v1/worklog")
    public ResponseEntity<WorklogResponse> updateWorklog(@RequestBody UpdateWorklogRequest updateWorklogRequest, @AuthenticationPrincipal Jwt jwt) {
        WorklogResponse worklog = workLogService.updateWorklog(updateWorklogRequest);

        return ResponseEntity.ok(worklog);
    }

    @DeleteMapping("/v1/worklog/{id}")
    public ResponseEntity<WorklogResponse> deleteWorklog(@PathVariable UUID id, @AuthenticationPrincipal Jwt jwt) {
        WorklogResponse worklog = workLogService.deleteWorklogById(id);

        return ResponseEntity.ok(worklog);
    }

}
