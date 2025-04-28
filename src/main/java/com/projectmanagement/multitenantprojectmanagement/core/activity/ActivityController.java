package com.projectmanagement.multitenantprojectmanagement.core.activity;

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

import com.projectmanagement.multitenantprojectmanagement.auth0.utils.JWTUtils;
import com.projectmanagement.multitenantprojectmanagement.core.activity.dto.request.CreateActivityRequest;
import com.projectmanagement.multitenantprojectmanagement.core.activity.dto.response.ActivityResponse;
import com.projectmanagement.multitenantprojectmanagement.core.activity.mapper.ActivityMapper;
import com.projectmanagement.multitenantprojectmanagement.organizations.dto.response.PaginatedResponseDto;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ActivityController {

    private final ActivityService activityService;
    private final JWTUtils jwtUtils;

    @GetMapping("/v1/activity/{id}")
    public ResponseEntity<ActivityResponse> getActivityById(@PathVariable UUID id,@AuthenticationPrincipal Jwt jwt) {
        Activity activity = activityService.getActivityById(id);

        return ResponseEntity.ok(ActivityMapper.toActivityResponse(activity));
    }

    @GetMapping("/v1/activity/{id}/entity")
    public ResponseEntity<PaginatedResponseDto<ActivityResponse>> getActivityByEntityId(@PathVariable UUID id, Pageable pageable,@AuthenticationPrincipal Jwt jwt) {
        PaginatedResponseDto<ActivityResponse> activites = activityService.getActivitiesByEntityId(id, pageable);

        return ResponseEntity.ok(activites);
    }

    @GetMapping("/v1/activity/{id}/project")
    public ResponseEntity<PaginatedResponseDto<ActivityResponse>> getActivityByProjectId(@PathVariable UUID id, Pageable pageable,@AuthenticationPrincipal Jwt jwt) {
        String auth0OrgId = jwtUtils.getAuth0OrgId();

        PaginatedResponseDto<ActivityResponse> activites = activityService.getActivitiesByProjectIdAndOrgId(id, auth0OrgId, pageable);

        return ResponseEntity.ok(activites);
    }

    @PostMapping("/v1/activity")
    public ResponseEntity<ActivityResponse> createActivity(@RequestBody CreateActivityRequest createActivityRequest,@AuthenticationPrincipal Jwt jwt) {
        ActivityResponse activityResponse = activityService.createActivity(createActivityRequest);

        return ResponseEntity.ok(activityResponse);
    }

    @DeleteMapping("/v1/activity/{id}")
    public ResponseEntity<ActivityResponse> deleteActivityById(@PathVariable UUID id,@AuthenticationPrincipal Jwt jwt) {
        ActivityResponse activityResponse = activityService.deleteActivityById(id);

        return ResponseEntity.ok(activityResponse);
    }

}
