package com.projectmanagement.multitenantprojectmanagement.core.projectMember;

import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.projectmanagement.multitenantprojectmanagement.core.projectMember.dto.request.CreateProjectMember;
import com.projectmanagement.multitenantprojectmanagement.core.projectMember.dto.response.ProjectMemberDetailedResponse;
import com.projectmanagement.multitenantprojectmanagement.core.projectMember.dto.response.ProjectMembersResponse;
import com.projectmanagement.multitenantprojectmanagement.organizations.dto.response.PaginatedResponseDto;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ProjectMemberController {

    private final ProjectMemberService projectMemberService;

    @GetMapping("/v1/project-member/{id}/user")
    public ResponseEntity<ProjectMemberDetailedResponse> getMemberById(@PathVariable UUID id) {
        ProjectMemberDetailedResponse member = projectMemberService.getMemberByUserId(id);

        return ResponseEntity.ok(member);
    }

    @GetMapping("/v1/project-member/{id}/project")
    public ResponseEntity<PaginatedResponseDto<ProjectMembersResponse>> getMembersByProjectId(@PathVariable UUID id, Pageable pageable) {
        PaginatedResponseDto<ProjectMembersResponse> members = projectMemberService.getAllMembersByProjectId(id, pageable);

        return ResponseEntity.ok(members);
    }

    @PostMapping("/v1/project-member")
    public ResponseEntity<ProjectMemberDetailedResponse> createProjectMember(@RequestBody CreateProjectMember createProjectMember) {
        ProjectMemberDetailedResponse member = projectMemberService.createProjectMember(createProjectMember);

        return ResponseEntity.ok(member);
    }

    @PutMapping("/v1/project-member/{id}/role/{roleId}")
    public ResponseEntity<ProjectMemberDetailedResponse> updateProjectMember(@PathVariable UUID id, @PathVariable UUID roleId ) {
        ProjectMemberDetailedResponse member = projectMemberService.updateProjectMemberRole(id, roleId);

        return ResponseEntity.ok(member);
    }

    @DeleteMapping("/v1/project-member/{id}")
    public ResponseEntity<ProjectMemberDetailedResponse> removeProjectMember(@PathVariable UUID id) {
        ProjectMemberDetailedResponse member = projectMemberService.removeMemberFromProject(id);

        return ResponseEntity.ok(member);
    }
    

}
