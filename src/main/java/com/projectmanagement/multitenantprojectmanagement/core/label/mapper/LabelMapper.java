package com.projectmanagement.multitenantprojectmanagement.core.label.mapper;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Page;

import com.projectmanagement.multitenantprojectmanagement.core.issue.Issue;
import com.projectmanagement.multitenantprojectmanagement.core.label.Label;
import com.projectmanagement.multitenantprojectmanagement.core.label.dto.request.CreateLabelRequest;
import com.projectmanagement.multitenantprojectmanagement.core.label.dto.response.LabelResponse;
import com.projectmanagement.multitenantprojectmanagement.core.project.Projects;
import com.projectmanagement.multitenantprojectmanagement.organizations.Organizations;
import com.projectmanagement.multitenantprojectmanagement.organizations.dto.response.PaginatedResponseDto;

public class LabelMapper {

    public static Label toLabelEntity(CreateLabelRequest createLabelRequest, Projects project, Issue issue, Organizations organization) {

        Label label = new Label();
        label.setName(createLabelRequest.getName());
        label.setProject(project);  
        label.setOrganization(organization);  
        
        if(label.getIssues() == null) {
            label.setIssues(new ArrayList<>());
        }

        label.getIssues().add(issue);

        if(issue.getLabels() == null) {
            issue.setLabels(new HashSet<>());
        }

        issue.getLabels().add(label);

        return label;
    }

    public static LabelResponse toLabelResponse(Label label) {
        return LabelResponse.builder()
                .id(label.getId())
                .name(label.getName())
                .color(label.getColor())
                .build();
    }

    public static List<LabelResponse> toListLabelResponse(List<Label> labels) {
        List<LabelResponse> response = new ArrayList<>();

        for(Label label: labels) {
            response.add(toLabelResponse(label));
        }

        return response;
    }

    public static Set<LabelResponse> toSetLabelResponse(Set<Label> labels) {

        Set<LabelResponse> response = new HashSet<>();

        for(Label label: labels) {
            response.add(toLabelResponse(label));
        }

        return response;

    }

    public static PaginatedResponseDto<LabelResponse> toPaginatedResponse(Page<Label> labels) {

        return PaginatedResponseDto.<LabelResponse>builder()
                .data(toListLabelResponse(labels.getContent()))
                .size(labels.getSize())
                .totalElements(labels.getTotalElements())
                .totalPages(labels.getTotalPages())
                .page(labels.getNumber())
                .build();

    }

}
