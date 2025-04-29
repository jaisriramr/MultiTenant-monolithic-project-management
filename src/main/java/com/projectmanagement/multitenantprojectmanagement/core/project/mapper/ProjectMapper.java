package com.projectmanagement.multitenantprojectmanagement.core.project.mapper;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;

import com.projectmanagement.multitenantprojectmanagement.core.project.Projects;
import com.projectmanagement.multitenantprojectmanagement.core.project.dto.request.CreateProjectRequest;
import com.projectmanagement.multitenantprojectmanagement.core.project.dto.response.ProjectDetailsResponse;
import com.projectmanagement.multitenantprojectmanagement.core.project.dto.response.ProjectMemberMiniResponse;
import com.projectmanagement.multitenantprojectmanagement.core.project.dto.response.ProjectOrgResponse;
import com.projectmanagement.multitenantprojectmanagement.core.project.dto.response.ProjectUserResponse;
import com.projectmanagement.multitenantprojectmanagement.core.project.dto.response.ProjectsResponse;
import com.projectmanagement.multitenantprojectmanagement.core.project.enums.ProjectStatus;
import com.projectmanagement.multitenantprojectmanagement.core.projectMember.ProjectMember;
import com.projectmanagement.multitenantprojectmanagement.core.sprint.dto.response.ListSprintResponse;
import com.projectmanagement.multitenantprojectmanagement.core.sprint.mapper.SprintMapper;
import com.projectmanagement.multitenantprojectmanagement.organizationmembers.OrganizationMembers;
import com.projectmanagement.multitenantprojectmanagement.organizations.dto.response.PaginatedResponseDto;
import com.projectmanagement.multitenantprojectmanagement.roles.dto.response.RoleResponse;
import com.projectmanagement.multitenantprojectmanagement.roles.mapper.RoleMapper;
import com.projectmanagement.multitenantprojectmanagement.users.dto.mapper.UserMapper;
import com.projectmanagement.multitenantprojectmanagement.users.dto.response.UserListResponseDto;

public class ProjectMapper {

    public static Projects toProjectEntity(CreateProjectRequest createProjectRequest, OrganizationMembers member) {
        Projects project = new Projects();
        project.setName(createProjectRequest.getName());
        project.setKey(createProjectRequest.getKey());

        project.setOrganization(member.getOrganization());
        project.setCreatedBy(member.getUser());
        project.setStatus(ProjectStatus.ACTIVE);

        return project;
    }

    public static ProjectDetailsResponse toProjectDetailsResponse(Projects project) {

        ProjectOrgResponse org = ProjectOrgResponse.builder().id(project.getOrganization().getId())
                .auth0Id(project.getOrganization().getAuth0Id()).name(project.getName()).build();

        ProjectUserResponse user = null;

        if (project.getCreatedBy() != null) {
            user = ProjectUserResponse.builder().id(project.getCreatedBy().getId())
                    .auth0Id(project.getCreatedBy().getAuth0Id()).name(project.getCreatedBy().getName())
                    .profilePic(project.getCreatedBy().getProfilePic()).build();
        }

        List<ListSprintResponse> sprints = SprintMapper.toListSprintResponse(project.getSprints());

        List<ProjectMemberMiniResponse> members = new ArrayList<>();
        
        for(ProjectMember projectMember: project.getProjectMembers()) {
            UserListResponseDto mem = UserListResponseDto.builder()
                                        .id(projectMember.getUser().getId())
                                        .name(projectMember.getUser().getName())
                                        .profilePic(projectMember.getUser().getProfilePic())
                                        .build();
            
            
            RoleResponse role = RoleMapper.toRoleResponse(projectMember.getRole());

            ProjectMemberMiniResponse m = ProjectMemberMiniResponse.builder()
                                            .id(projectMember.getId())
                                            .user(mem)
                                            .role(role)
                                            .build();
            
            members.add(m);
            }

        return ProjectDetailsResponse.builder()
                    .id(project.getId())
                    .name(project.getName())
                    .key(project.getKey())
                    .organization(org)
                    .createdBy(user)
                    .status(project.getStatus())
                    .sprints(sprints)
                    .projectMembers(members)
                    // .workflowScheme(project.getWorkflowScheme())
                    .createdAt(project.getCreatedAt().toString())
                    .updatedAt(project.getUpdatedAt().toString())
                    .build();
    }

    public static ProjectsResponse toSingleProjectsResponse(Projects project) {
        return ProjectsResponse.builder().id(project.getId()).name(project.getName()).status(project.getStatus())
                .createdAt(project.getCreatedAt().toString()).updatedAt(project.getUpdatedAt().toString()).build();
    }

    public static PaginatedResponseDto<ProjectsResponse> toProjectsResponse(Page<Projects> projects) {

        List<ProjectsResponse> response = new ArrayList<>();

        for (Projects project : projects.getContent()) {

            ProjectOrgResponse org = ProjectOrgResponse.builder().id(project.getOrganization().getId())
                    .auth0Id(project.getOrganization().getAuth0Id()).name(project.getName()).build();

            ProjectUserResponse user = null;

            if (project.getCreatedBy() != null) {
                user = ProjectUserResponse.builder().id(project.getCreatedBy().getId())
                        .auth0Id(project.getCreatedBy().getAuth0Id()).name(project.getCreatedBy().getName())
                        .profilePic(project.getCreatedBy().getProfilePic()).build();
            }

            ProjectsResponse p = ProjectsResponse.builder().id(project.getId()).name(project.getName())
                    .organization(org).createdBy(user).status(project.getStatus()).createdAt(project.getCreatedAt().toString())
                    .updatedAt(project.getUpdatedAt().toString()).build();

            response.add(p);
        }

        return PaginatedResponseDto.<ProjectsResponse>builder().data(response).page(projects.getNumber())
                .totalPages(projects.getTotalPages()).totalElements(projects.getTotalElements())
                .size(projects.getSize()).build();
    }

}
