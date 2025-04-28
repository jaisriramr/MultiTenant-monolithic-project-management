package com.projectmanagement.multitenantprojectmanagement.core.worklog.mapper;

import com.projectmanagement.multitenantprojectmanagement.core.issue.Issue;
import com.projectmanagement.multitenantprojectmanagement.core.worklog.WorkLog;
import com.projectmanagement.multitenantprojectmanagement.core.worklog.dto.request.CreateWorklogRequest;
import com.projectmanagement.multitenantprojectmanagement.core.worklog.dto.request.UpdateWorklogRequest;
import com.projectmanagement.multitenantprojectmanagement.core.worklog.dto.response.WorklogResponse;
import com.projectmanagement.multitenantprojectmanagement.organizations.Organizations;
import com.projectmanagement.multitenantprojectmanagement.users.Users;
import com.projectmanagement.multitenantprojectmanagement.users.dto.mapper.UserMapper;

public class WorklogMapper {

    public static WorkLog toworklogEntity(CreateWorklogRequest createWorklogRequest, Issue issue, Users user, Organizations organization) {
        WorkLog workLog = new WorkLog();
        workLog.setIssue(issue);
        workLog.setUser(user);
        workLog.setStartedDateTime(createWorklogRequest.getStartedDateTime());
        workLog.setTimeSpentInMinutes(createWorklogRequest.getTimeSpentInMinutes());
        workLog.setOrganization(organization);
        
        if(createWorklogRequest.getComment() != null) {
            workLog.setComment(createWorklogRequest.getComment());
        }

        return workLog;
    }

    public static WorkLog toUpdateWorklogEntity(UpdateWorklogRequest updateWorklogRequest, WorkLog workLog) {
        if(updateWorklogRequest.getComment() != null) {
            workLog.setComment(updateWorklogRequest.getComment());
        }

        if(updateWorklogRequest.getStartedDateTime() != null) {
            workLog.setStartedDateTime(updateWorklogRequest.getStartedDateTime());
        }

        if(updateWorklogRequest.getTimeSpentInMinutes() != null) {
            workLog.setTimeSpentInMinutes(updateWorklogRequest.getTimeSpentInMinutes());
        }

        return workLog;
    }

    public static WorklogResponse toWorklogResponse(WorkLog workLog) {
        return WorklogResponse.builder()
                .id(workLog.getId())
                .user(UserMapper.toUserSingleListResponse(workLog.getUser()))
                .startedDateTime(workLog.getStartedDateTime())
                .comment(workLog.getComment())
                .timeSpent(workLog.getTimeSpentInMinutes())
                .createdAt(workLog.getCreatedAt())
                .updatedAt(workLog.getUpdatedAt())
                .build();
    }

}
