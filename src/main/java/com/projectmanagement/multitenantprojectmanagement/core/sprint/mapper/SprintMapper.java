package com.projectmanagement.multitenantprojectmanagement.core.sprint.mapper;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import com.projectmanagement.multitenantprojectmanagement.core.issue.Issue;
import com.projectmanagement.multitenantprojectmanagement.core.project.Projects;
import com.projectmanagement.multitenantprojectmanagement.core.project.dto.response.ProjectDetailsResponse;
import com.projectmanagement.multitenantprojectmanagement.core.project.dto.response.ProjectsResponse;
import com.projectmanagement.multitenantprojectmanagement.core.project.mapper.ProjectMapper;
import com.projectmanagement.multitenantprojectmanagement.core.sprint.Sprint;
import com.projectmanagement.multitenantprojectmanagement.core.sprint.dto.request.CreateSprintRequest;
import com.projectmanagement.multitenantprojectmanagement.core.sprint.dto.response.ListSprintResponse;
import com.projectmanagement.multitenantprojectmanagement.core.sprint.dto.response.MinimalSprintResponse;
import com.projectmanagement.multitenantprojectmanagement.core.sprint.dto.response.SprintDetailedResponse;
import com.projectmanagement.multitenantprojectmanagement.core.sprint.enums.SprintStatus;

public class SprintMapper {

    public static Sprint toSprintEntity(CreateSprintRequest createSprintRequest, Projects project) {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        Sprint sprint = new Sprint();

        sprint.setName(createSprintRequest.getName());
        sprint.setGoal(createSprintRequest.getDescription());
        LocalDate startDate = LocalDate.parse(createSprintRequest.getStartDate(), formatter);
        LocalDate endDate = LocalDate.parse(createSprintRequest.getEndDate(), formatter);

        sprint.setStartDate(startDate);
        sprint.setEndDate(endDate);
        sprint.setProject(project);
        sprint.setStatus(SprintStatus.ACTIVE);
        sprint.setIssues(new ArrayList<>());

        return sprint;
    }

    public static MinimalSprintResponse toMinimalSprintResponse(Sprint sprint) {

        ProjectsResponse project = ProjectMapper.toSingleProjectsResponse(sprint.getProject());

        return MinimalSprintResponse.builder()
                .id(sprint.getId())
                .name(sprint.getName())
                .startDate(sprint.getStartDate())
                .endDate(sprint.getEndDate())
                .status(sprint.getStatus().toString())
                .project(project)
                .createdAt(sprint.getCreatedAt())
                .updatedAt(sprint.getUpdatedAt())
                .build();

    }

    public static List<ListSprintResponse> toListSprintResponse(List<Sprint> sprints) {

        List<ListSprintResponse> listSprints = new ArrayList<>();

        for(Sprint sprint: sprints) {
            List<Issue> issues = new ArrayList<>();

            ListSprintResponse singleSprint = ListSprintResponse.builder()
                    .id(sprint.getId())
                    .name(sprint.getName())
                    .goal(sprint.getGoal())
                    .status(sprint.getStatus().toString())
                    // .issues(issues)
                    .createdAt(sprint.getCreatedAt())
                    .updatedAt(sprint.getUpdatedAt())
                    .build();

                    listSprints.add(singleSprint);
        }

        return listSprints;
    }

    public static SprintDetailedResponse toSprintDetailedResponse(Sprint sprint) {
        ProjectsResponse project = ProjectMapper.toSingleProjectsResponse(sprint.getProject());

        return SprintDetailedResponse.builder()
                .id(sprint.getId())
                .name(sprint.getName())
                .startDate(sprint.getStartDate())
                .endDate(sprint.getEndDate())
                .status(sprint.getStatus().toString())
                .project(project)
                .createdAt(sprint.getCreatedAt())
                .updatedAt(sprint.getUpdatedAt())
                .build();

    }

}
