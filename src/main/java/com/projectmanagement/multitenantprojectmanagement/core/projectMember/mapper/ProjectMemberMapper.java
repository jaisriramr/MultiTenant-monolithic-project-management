package com.projectmanagement.multitenantprojectmanagement.core.projectMember.mapper;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;

import com.projectmanagement.multitenantprojectmanagement.core.project.Projects;
import com.projectmanagement.multitenantprojectmanagement.core.projectMember.ProjectMember;
import com.projectmanagement.multitenantprojectmanagement.core.projectMember.dto.response.ProjectMemberDetailedResponse;
import com.projectmanagement.multitenantprojectmanagement.core.projectMember.dto.response.ProjectMemberProjectDto;
import com.projectmanagement.multitenantprojectmanagement.core.projectMember.dto.response.ProjectMembersResponse;
import com.projectmanagement.multitenantprojectmanagement.organizations.Organizations;
import com.projectmanagement.multitenantprojectmanagement.organizations.dto.response.PaginatedResponseDto;
import com.projectmanagement.multitenantprojectmanagement.roles.Roles;
import com.projectmanagement.multitenantprojectmanagement.roles.dto.response.RoleResponse;
import com.projectmanagement.multitenantprojectmanagement.roles.mapper.RoleMapper;
import com.projectmanagement.multitenantprojectmanagement.users.Users;
import com.projectmanagement.multitenantprojectmanagement.users.dto.mapper.UserMapper;
import com.projectmanagement.multitenantprojectmanagement.users.dto.response.UserListResponseDto;
import com.projectmanagement.multitenantprojectmanagement.users.dto.response.UserResponseDto;

public class ProjectMemberMapper {

    public static ProjectMemberDetailedResponse toProjectMemberDetailedResponse(ProjectMember projectMember) {

        ProjectMemberProjectDto project = ProjectMemberProjectDto.builder()
                                            .id(projectMember.getProject().getId())
                                            .name(projectMember.getProject().getName())
                                            .key(projectMember.getProject().getKey())
                                            .build();

        UserResponseDto user = UserMapper.toUserReponse(projectMember.getUser());
        
        RoleResponse role = RoleMapper.toRoleResponse(projectMember.getRole());

        return ProjectMemberDetailedResponse.builder()
                .id(projectMember.getId())
                .project(project)
                .user(user)
                .role(role)
                .createdAt(projectMember.getCreatedAt())
                .updatedAt(projectMember.getUpdatedAt())
                .build();
    }

    public static ProjectMember toProjectMemberEntity(Users user, Projects project, Roles role, Organizations organization) {
        ProjectMember projectMember = new ProjectMember();

        projectMember.setProject(project);
        projectMember.setUser(user);
        projectMember.setRole(role);
        projectMember.setOrganization(organization);

        if(project.getProjectMembers() == null) {
            project.setProjectMembers(new ArrayList<>());
        }

        project.getProjectMembers().add(projectMember);

        return projectMember;

    }

    public static PaginatedResponseDto<ProjectMembersResponse> toPaginatedReponse(Page<ProjectMember> projectMembers) {

        List<ProjectMembersResponse> members = new ArrayList<>();

        for(ProjectMember member: projectMembers.getContent()) {
            UserListResponseDto user = UserListResponseDto.builder()
                                        .id(member.getUser().getId())
                                        .name(member.getUser().getName())
                                        .profilePic(member.getUser().getProfilePic())
                                        .build();
            
            RoleResponse role = RoleMapper.toRoleResponse(member.getRole());

            ProjectMembersResponse mem = ProjectMembersResponse.builder()
                                            .id(member.getId())
                                            .user(user)
                                            .role(role)
                                            .build();
            members.add(mem);
        }

        return PaginatedResponseDto.<ProjectMembersResponse>builder()
                .data(members)
                .size(projectMembers.getSize())
                .page(projectMembers.getNumber())
                .totalElements(projectMembers.getTotalElements())
                .totalPages(projectMembers.getTotalPages())
                .build();

    }

}
