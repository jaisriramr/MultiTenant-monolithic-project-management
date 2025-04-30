package com.projectmanagement.multitenantprojectmanagement.core.sprint;

import java.util.UUID;

import org.springframework.data.domain.Pageable;
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

import com.projectmanagement.multitenantprojectmanagement.core.sprint.dto.request.CreateSprintRequest;
import com.projectmanagement.multitenantprojectmanagement.core.sprint.dto.request.UpdateSprintRequest;
import com.projectmanagement.multitenantprojectmanagement.core.sprint.dto.response.ListSprintResponse;
import com.projectmanagement.multitenantprojectmanagement.core.sprint.dto.response.MinimalSprintResponse;
import com.projectmanagement.multitenantprojectmanagement.core.sprint.dto.response.SprintDetailedResponse;
import com.projectmanagement.multitenantprojectmanagement.core.sprint.mapper.SprintMapper;
import com.projectmanagement.multitenantprojectmanagement.organizations.dto.response.PaginatedResponseDto;

import lombok.RequiredArgsConstructor;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class SprintController {

    private final SprintService sprintService;

    @GetMapping("/v1/sprint/{id}")
    public ResponseEntity<SprintDetailedResponse> getSprintById(@PathVariable UUID id, @AuthenticationPrincipal Jwt jwt) {
        Sprint sprint = sprintService.getSprintEntity(id);

        SprintDetailedResponse response = SprintMapper.toSprintDetailedResponse(sprint);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/v1/sprint/{id}/project")
    public ResponseEntity<PaginatedResponseDto<ListSprintResponse>> getAllSprintByProjectId(@PathVariable UUID id, Pageable pageable,@AuthenticationPrincipal Jwt jwt) {
        
        PaginatedResponseDto<ListSprintResponse> sprints = sprintService.getAllSprintByProjectId(id, pageable);

        return ResponseEntity.ok(sprints);

    }
    

    @PostMapping("/v1/sprint")
    public ResponseEntity<SprintDetailedResponse> createSprint(@RequestBody CreateSprintRequest createSprintRequest, @AuthenticationPrincipal Jwt jwt) {
        SprintDetailedResponse sprint = sprintService.createSprint(createSprintRequest);

        return ResponseEntity.ok(sprint);
    }

    @PutMapping("/v1/sprint")
    public ResponseEntity<SprintDetailedResponse> updateSprint(@RequestBody UpdateSprintRequest updateSprintRequest, @AuthenticationPrincipal Jwt jwt) {
        SprintDetailedResponse sprint = sprintService.updateSprint(updateSprintRequest);

        return ResponseEntity.ok(sprint);
    }

    @DeleteMapping("/v1/sprint/{id}")
    public ResponseEntity<MinimalSprintResponse> deleteSprintById(@PathVariable UUID id, @AuthenticationPrincipal Jwt jwt) {
        MinimalSprintResponse sprint = sprintService.deleteSprint(id);

        return ResponseEntity.ok(sprint);
    }

}
