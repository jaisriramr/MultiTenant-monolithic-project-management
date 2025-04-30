package com.projectmanagement.multitenantprojectmanagement.core.project;

import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.projectmanagement.multitenantprojectmanagement.core.project.dto.request.CreateProjectRequest;
import com.projectmanagement.multitenantprojectmanagement.core.project.dto.request.UpdateProjectRequest;
import com.projectmanagement.multitenantprojectmanagement.core.project.dto.response.ProjectDetailsResponse;
import com.projectmanagement.multitenantprojectmanagement.core.project.dto.response.ProjectsResponse;
import com.projectmanagement.multitenantprojectmanagement.organizations.dto.response.PaginatedResponseDto;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class Projectcontroller {

    private final ProjectService projectService;

    @GetMapping("/v1/project/{id}")
    public ResponseEntity<ProjectDetailsResponse> getProjectById(@PathVariable UUID id,@AuthenticationPrincipal Jwt jwt) {
        ProjectDetailsResponse project = projectService.getProjectByIdForController(id);

        return ResponseEntity.ok(project);
    }

    @GetMapping("/v1/project/{id}/organization")
    public ResponseEntity<PaginatedResponseDto<ProjectsResponse>> getAllProjectsByOrgId(@PathVariable UUID id, Pageable pageable,@AuthenticationPrincipal Jwt jwt) {
        PaginatedResponseDto<ProjectsResponse> projects = projectService.getAllProjectsByOrganizationId(id, pageable);

        return ResponseEntity.ok(projects);
    }

    @PostMapping("/v1/project")
    public ResponseEntity<ProjectDetailsResponse> createProject(@Valid @RequestBody CreateProjectRequest createProjectRequest,@AuthenticationPrincipal Jwt jwt) {
        ProjectDetailsResponse project = projectService.createProject(createProjectRequest);
        return ResponseEntity.ok(project);
    }

    @PutMapping("/v1/project")
    public ResponseEntity<ProjectDetailsResponse> updateProject(@RequestBody UpdateProjectRequest updateProjectRequest,@AuthenticationPrincipal Jwt jwt) {
        ProjectDetailsResponse project = projectService.updateProject(updateProjectRequest);
        
        return ResponseEntity.ok(project);
    }

    @DeleteMapping("/v1/project/{id}")
    public ResponseEntity<ProjectDetailsResponse> deleteProjectById(@PathVariable UUID id,@AuthenticationPrincipal Jwt jwt) {
        ProjectDetailsResponse project = projectService.deleteProjectById(id);

        return ResponseEntity.ok(project);
    }
    
    

}
