package com.projectmanagement.multitenantprojectmanagement.core.issuerelation.mapper;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;

import com.projectmanagement.multitenantprojectmanagement.core.issue.dto.response.ListIssuesResponse;
import com.projectmanagement.multitenantprojectmanagement.core.issue.mapper.IssueMapper;
import com.projectmanagement.multitenantprojectmanagement.core.issuerelation.IssueRelation;
import com.projectmanagement.multitenantprojectmanagement.organizations.dto.response.PaginatedResponseDto;

public class IssueRelationMapper {

    public static PaginatedResponseDto<ListIssuesResponse> toPaginatedResponseDto(Page<IssueRelation> issueRelations) {

        List<ListIssuesResponse> childworks = new ArrayList<>();

        for(IssueRelation issueRelation: issueRelations.getContent()) {
            childworks.add(IssueMapper.toListIssuesResponse(issueRelation.getChildIssue()));
        }

        return PaginatedResponseDto.<ListIssuesResponse>builder()
                .data(childworks)
                .totalElements(issueRelations.getTotalElements())
                .page(issueRelations.getNumber())
                .totalPages(issueRelations.getTotalPages())
                .size(issueRelations.getSize())
                .build();

    }

}
