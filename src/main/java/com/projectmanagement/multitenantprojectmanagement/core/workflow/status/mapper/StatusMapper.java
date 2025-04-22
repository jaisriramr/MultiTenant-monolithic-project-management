package com.projectmanagement.multitenantprojectmanagement.core.workflow.status.mapper;

import java.util.ArrayList;
import java.util.List;

import com.projectmanagement.multitenantprojectmanagement.core.project.Projects;
import com.projectmanagement.multitenantprojectmanagement.core.workflow.status.Status;
import com.projectmanagement.multitenantprojectmanagement.core.workflow.status.dto.request.CreateStatusRequest;
import com.projectmanagement.multitenantprojectmanagement.core.workflow.status.dto.response.StatusResponse;
import com.projectmanagement.multitenantprojectmanagement.core.workflow.status.dto.response.StatusesResponse;
import com.projectmanagement.multitenantprojectmanagement.core.workflow.status.enums.Category;

public class StatusMapper {

    public static StatusResponse toStatusResponse(Status status) {
        return StatusResponse.builder()
                .id(status.getId())
                .name(status.getName())
                .category(status.getCategory().toString())
                .defaultStatus(status.getDefaultStatus())
                .projectId(status.getProject().getId())
                .build();
    }

    public static List<StatusesResponse> toStatusesResponse(List<Status> statuses) {

        List<StatusesResponse> response = new ArrayList<>();

        for(Status status: statuses) {
            
            StatusesResponse s = StatusesResponse.builder()
                                .id(status.getId())
                                .name(status.getName())
                                .category(status.getCategory().toString())
                                .defaultStatus(status.getDefaultStatus())
                                .build();

            response.add(s);
        }

        return response;
    }

    public static Status toStatusEntity(CreateStatusRequest createStatusRequest, Projects project) {

        System.out.println("CCCCCCCC " +createStatusRequest.getCategory());

        Status status = new Status();

        status.setName(createStatusRequest.getName());
        status.setDefaultStatus(createStatusRequest.getDefaultStatus());
        status.setProject(project);
        if("OPEN".equals(createStatusRequest.getCategory())) {
            status.setCategory(Category.OPEN);
        }else if("INPROGRESS".equals(createStatusRequest.getCategory())) {
            status.setCategory(Category.INPROGRESS);
        }else if("CLOSED".equals(createStatusRequest.getCategory())) {
            status.setCategory(Category.CLOSED);
        }else {
            throw new IllegalArgumentException("Only OPEN, INPROGRESS and CLOSED are allowed!");
        }

        return status;

    }

}
