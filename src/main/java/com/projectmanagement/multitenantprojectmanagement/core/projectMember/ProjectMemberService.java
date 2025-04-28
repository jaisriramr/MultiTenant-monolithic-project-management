package com.projectmanagement.multitenantprojectmanagement.core.projectMember;

import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.projectmanagement.multitenantprojectmanagement.auth0.utils.JWTUtils;
import com.projectmanagement.multitenantprojectmanagement.core.project.ProjectService;
import com.projectmanagement.multitenantprojectmanagement.core.project.Projects;
import com.projectmanagement.multitenantprojectmanagement.core.projectMember.dto.request.CreateProjectMember;
import com.projectmanagement.multitenantprojectmanagement.core.projectMember.dto.response.ProjectMemberDetailedResponse;
import com.projectmanagement.multitenantprojectmanagement.core.projectMember.dto.response.ProjectMembersResponse;
import com.projectmanagement.multitenantprojectmanagement.core.projectMember.mapper.ProjectMemberMapper;
import com.projectmanagement.multitenantprojectmanagement.core.workflow.status.StatusService;
import com.projectmanagement.multitenantprojectmanagement.exception.NotFoundException;
import com.projectmanagement.multitenantprojectmanagement.helper.MaskingString;
import com.projectmanagement.multitenantprojectmanagement.organizations.Organizations;
import com.projectmanagement.multitenantprojectmanagement.organizations.OrganizationsService;
import com.projectmanagement.multitenantprojectmanagement.organizations.dto.response.PaginatedResponseDto;
import com.projectmanagement.multitenantprojectmanagement.roles.Roles;
import com.projectmanagement.multitenantprojectmanagement.roles.RolesService;
import com.projectmanagement.multitenantprojectmanagement.users.UserService;
import com.projectmanagement.multitenantprojectmanagement.users.Users;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProjectMemberService {

    private final ProjectMemberRepository projectMemberRepository;
    private final ProjectService projectService;
    private final UserService userService;
    private final RolesService rolesService;
    private final OrganizationsService organizationsService;
    private final JWTUtils jwtUtils;

    private final MaskingString maskingString;
    private static final Logger logger = LoggerFactory.getLogger(ProjectMemberService.class);

    public ProjectMember getProjectMemberEntity(UUID id) {
        logger.info("Getting project member for the given ID: {}", maskingString.maskSensitive(id.toString()));

        String auth0OrgId = jwtUtils.getAuth0OrgId();

        ProjectMember member = projectMemberRepository.findByIdAndOrganization_Auth0Id(id, auth0OrgId).orElseThrow(() -> new NotFoundException("Project member not found for the given ID: " + id));

        logger.debug("Fetched project member ID: {}", maskingString.maskSensitive(member.getId().toString()));

        return member;
    }

    public ProjectMemberDetailedResponse getMemberByUserId(UUID id) {
        logger.info("Getting project member for the given user ID: {}", maskingString.maskSensitive(id.toString()));

        String auth0OrgId = jwtUtils.getAuth0OrgId();

        ProjectMember member = projectMemberRepository.findByUserIdAndOrganization_Auth0Id(id, auth0OrgId).orElseThrow(() -> new NotFoundException("Project member not found for the given ID: " + id));

        logger.debug("Fetched project member ID: {}", maskingString.maskSensitive(member.getId().toString()));

        return ProjectMemberMapper.toProjectMemberDetailedResponse(member);
    }

    public PaginatedResponseDto<ProjectMembersResponse> getAllMembersByProjectId(UUID projectId, Pageable pageable) {
        logger.info("Getting all members in a project via its ID: {}", maskingString.maskSensitive(projectId.toString()));

        String auth0OrgId = jwtUtils.getAuth0OrgId();

        Page<ProjectMember> members = projectMemberRepository.findAllByProjectIdAndOrganization_Auth0Id(projectId, auth0OrgId,pageable);

        logger.debug("Fetched {} members", members.getTotalElements());

        return ProjectMemberMapper.toPaginatedReponse(members);

    }

    @Transactional
    public ProjectMemberDetailedResponse createProjectMember(CreateProjectMember createProjectMember) {
        logger.info("Creating project member");

        Projects project = projectService.getProjectById(createProjectMember.getProjectId());

        Users user = userService.getUserEntity(createProjectMember.getUserId());

        Roles role = rolesService.findRoleEntityById(createProjectMember.getRoleId());

        String auth0OrgId = jwtUtils.getAuth0OrgId();

        Organizations organization = organizationsService.getOrganizationByAuth0Id(auth0OrgId);

        ProjectMember projectMember = ProjectMemberMapper.toProjectMemberEntity(user, project, role, organization);

        ProjectMember savedMember = projectMemberRepository.save(projectMember);

        logger.debug("Saved member ID: {}", maskingString.maskSensitive(savedMember.getId().toString()));

        return ProjectMemberMapper.toProjectMemberDetailedResponse(projectMember);

    }

    @Transactional
    public ProjectMemberDetailedResponse updateProjectMemberRole(UUID id,UUID roleId) {
        logger.info("updating project member role ID: {}", maskingString.maskSensitive(roleId.toString()));

        ProjectMember member = getProjectMemberEntity(id);

        Roles role = rolesService.findRoleEntityById(roleId);

        member.setRole(role);

        ProjectMember updatedMember = projectMemberRepository.save(member);

        logger.debug("Updated project member ID: {}", maskingString.maskSensitive(updatedMember.getId().toString()));

        return ProjectMemberMapper.toProjectMemberDetailedResponse(member);
    }

    @Transactional
    public ProjectMemberDetailedResponse removeMemberFromProject(UUID id) {
        logger.info("Removing project member");

        ProjectMember projectMember = getProjectMemberEntity(id);

        projectMember.getProject().getProjectMembers().remove(projectMember);

        return ProjectMemberMapper.toProjectMemberDetailedResponse(projectMember);
    }

}
