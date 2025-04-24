package com.projectmanagement.multitenantprojectmanagement.core.issue.mapper;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.springframework.data.domain.Page;

import com.projectmanagement.multitenantprojectmanagement.core.epic.Epic;
import com.projectmanagement.multitenantprojectmanagement.core.epic.dto.request.CreateEpicRequest;
import com.projectmanagement.multitenantprojectmanagement.core.issue.Issue;
import com.projectmanagement.multitenantprojectmanagement.core.issue.dto.request.CreateEpicIssueRequest;
import com.projectmanagement.multitenantprojectmanagement.core.issue.dto.request.CreateIssueRequest;
import com.projectmanagement.multitenantprojectmanagement.core.issue.dto.request.CreateSubIssueRequest;
import com.projectmanagement.multitenantprojectmanagement.core.issue.dto.response.IssueResponse;
import com.projectmanagement.multitenantprojectmanagement.core.issue.dto.response.ListIssuesResponse;
import com.projectmanagement.multitenantprojectmanagement.core.issue.dto.response.ListIssuesUserDto;
import com.projectmanagement.multitenantprojectmanagement.core.issue.enums.IssuePriority;
import com.projectmanagement.multitenantprojectmanagement.core.issue.enums.IssueStatus;
import com.projectmanagement.multitenantprojectmanagement.core.issue.enums.IssueType;
import com.projectmanagement.multitenantprojectmanagement.core.label.mapper.LabelMapper;
import com.projectmanagement.multitenantprojectmanagement.core.project.Projects;
import com.projectmanagement.multitenantprojectmanagement.core.sprint.Sprint;
import com.projectmanagement.multitenantprojectmanagement.organizations.dto.response.PaginatedResponseDto;
import com.projectmanagement.multitenantprojectmanagement.users.Users;

public class IssueMapper {

    public static Issue toIssueEntity(CreateIssueRequest createIssueRequest, Projects project, Users reporter, Sprint sprint, String key) {
        Issue issue = new Issue();
        issue.setTitle(createIssueRequest.getTitle());
        
        
        issue.setKey(key);
        issue.setProject(project);
        issue.setReporter(reporter);
        issue.setSprint(sprint);
        issue.setStatus(IssueStatus.TO_DO);
        issue.setPriority(IssuePriority.MEDIUM);

        if("TASK".equals(createIssueRequest.getType())) {
            issue.setType(IssueType.TASK);
        }else if("BUG".equals(createIssueRequest.getType())) {
            issue.setType(IssueType.BUG);
        }else if("STORY".equals(createIssueRequest.getType())) {
            issue.setType(IssueType.STORY);
        }else if("EPIC".equals(createIssueRequest.getType())) {
            issue.setType(IssueType.EPIC);
        }else {
            throw new IllegalArgumentException("The given type is not allowed");
        }

        return issue;
    }

    public static Issue toEpicIssueEntity(CreateEpicIssueRequest createEpicIssueRequest, Projects project, Sprint sprint, Users reporter, String key, Epic epic) {
        Issue issue = new Issue();
        issue.setTitle(createEpicIssueRequest.getTitle());
        issue.setKey(key);
        issue.setProject(project);
        issue.setReporter(reporter);
        issue.setSprint(sprint);
        issue.setStatus(IssueStatus.TO_DO);
        issue.setPriority(IssuePriority.MEDIUM);
        issue.setType(IssueType.EPIC);
        issue.setEpic(epic);

        return issue;
    }

    public static Issue toSubIssueEntity(CreateSubIssueRequest createSubIssueRequest, Projects project, Sprint sprint, Users reporter, String key, Issue parent) {
        Issue issue = new Issue();
        issue.setTitle(createSubIssueRequest.getTitle());
        
        issue.setKey(key);
        issue.setProject(project);
        issue.setReporter(reporter);
        issue.setSprint(sprint);
        issue.setStatus(IssueStatus.TO_DO);
        issue.setPriority(IssuePriority.MEDIUM);
        issue.setIsSubTask(true);

        if("TASK".equals(createSubIssueRequest.getType())) {
            issue.setType(IssueType.TASK);
        }else if("BUG".equals(createSubIssueRequest.getType())) {
            issue.setType(IssueType.BUG);
        }else if("STORY".equals(createSubIssueRequest.getType())) {
            issue.setType(IssueType.STORY);
        }else if("EPIC".equals(createSubIssueRequest.getType())) {
            issue.setType(IssueType.EPIC);
        }else {
            throw new IllegalArgumentException("The given type is not allowed");
        }

        return issue;
    }

    public static CreateEpicRequest toCreateEpicIssueRequest(CreateEpicIssueRequest createEpicIssueRequest) {
        return CreateEpicRequest.builder()
                .name(createEpicIssueRequest.getTitle())
                .projectId(createEpicIssueRequest.getProjectId())
                .color(createEpicIssueRequest.getColor())
                .build();
    }

    public static ListIssuesUserDto toListIssuesUserDto(Users user) {
        return ListIssuesUserDto.builder()
                .id(user.getId())
                .name(user.getName())
                .profilePic(user.getProfilePic())
                .build();
    }

    public static IssueResponse toIssueResponse(Issue issue) {

        return IssueResponse.builder()
                .id(issue.getId())
                .key(issue.getKey())
                .title(issue.getTitle())
                .description(issue.getDescription())
                .status(issue.getStatus().toString())
                .type(issue.getType().toString())
                .priority(issue.getPriority().toString())
                .projectId(issue.getProject().getId())
                .sprintId(issue.getSprint() != null ? issue.getSprint().getId() : null)
                .storyPoints(issue.getStoryPoints())
                .labels(LabelMapper.toSetLabelResponse(issue.getLabels()))
                .comments(issue.getComments())
                .watchers(issue.getWatchers())
                .worklogs(issue.getWorkLogs())
                .epicId(issue.getEpic() != null ? issue.getEpic().getId() : null)
                .assignee(issue.getAssignee() != null ? toListIssuesUserDto(issue.getAssignee()) : null)
                .reporter(issue.getReporter() != null ? toListIssuesUserDto(issue.getReporter()) : null)
                .createdAt(issue.getCreatedAt())
                .updatedAt(issue.getUpdatedAt())
                .build();

    }

    public static ListIssuesResponse toListIssuesResponse(Issue issue) {

        return ListIssuesResponse.builder()
                .id(issue.getId())
                .key(issue.getKey())
                .title(issue.getTitle())
                .status(issue.getStatus().toString())
                .type(issue.getType().toString())
                .priority(issue.getPriority().toString())
                .assignee(issue.getAssignee() == null ? null : toListIssuesUserDto(issue.getAssignee()))
                .storyPoints(issue.getStoryPoints())
                .build();
    }

    public static PaginatedResponseDto<ListIssuesResponse> toPaginatedResponse(Page<Issue> issues) {

        List<ListIssuesResponse> issuesResponse = new ArrayList<>();

        for(Issue issue: issues.getContent()) {
            ListIssuesResponse issuesData = toListIssuesResponse(issue);

            issuesResponse.add(issuesData);
        }


        return PaginatedResponseDto.<ListIssuesResponse>builder()
                .data(issuesResponse)
                .size(issues.getSize())
                .page(issues.getNumber())
                .totalElements(issues.getTotalElements())
                .totalPages(issues.getTotalPages())
                .build();

    }

}
