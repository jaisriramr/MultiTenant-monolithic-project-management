package com.projectmanagement.multitenantprojectmanagement.core.comment.mapper;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;

import com.projectmanagement.multitenantprojectmanagement.core.comment.Comment;
import com.projectmanagement.multitenantprojectmanagement.core.comment.dto.request.CreateCommentReplyRequest;
import com.projectmanagement.multitenantprojectmanagement.core.comment.dto.request.CreateCommentRequest;
import com.projectmanagement.multitenantprojectmanagement.core.comment.dto.response.CommentResponse;
import com.projectmanagement.multitenantprojectmanagement.core.issue.Issue;
import com.projectmanagement.multitenantprojectmanagement.core.project.Projects;
import com.projectmanagement.multitenantprojectmanagement.organizations.Organizations;
import com.projectmanagement.multitenantprojectmanagement.organizations.dto.response.PaginatedResponseDto;
import com.projectmanagement.multitenantprojectmanagement.users.Users;
import com.projectmanagement.multitenantprojectmanagement.users.dto.mapper.UserMapper;

public class CommentMapper {

    public static CommentResponse toCommentResponse(Comment comment) {

        return CommentResponse.builder()
                .id(comment.getId())
                .author(UserMapper.toUserSingleListResponse(comment.getAuthor()))
                .content(comment.getContent())
                .depth(comment.getDepth())
                .path(comment.getPath())
                .createdAt(comment.getCreatedAt())
                .updatedAt(comment.getUpdatedAt())
                .build();

    }

    public static List<CommentResponse> toListCommentResponse(List<Comment> comments) {

        List<CommentResponse> response = new ArrayList<>();

        for(Comment comment: comments) {
            response.add(toCommentResponse(comment));
        }

        return response;
    }

    public static PaginatedResponseDto<CommentResponse> toPaginatedResponseDto(Page<Comment> comments) {

        return PaginatedResponseDto.<CommentResponse>builder()
                .data(toListCommentResponse(comments.getContent()))
                .size(comments.getSize())
                .totalElements(comments.getTotalElements())
                .page(comments.getNumber())
                .totalPages(comments.getTotalPages())
                .build();
    }

    public static Comment toCommentEntity(CreateCommentRequest createCommentRequest, Users author, Issue issue, Organizations organization, Projects project) {

        Comment comment = new Comment();

        
        comment.setContent(createCommentRequest.getComment());
        comment.setIssue(issue);
        comment.setAuthor(author);
        comment.setOrganization(organization);
        comment.setProject(project);
        comment.setDepth(0);
        
        comment.setParent(null);
        

        return comment;
    }

    public static Comment toCommentReply(CreateCommentReplyRequest createCommentReplyRequest, Users author, Comment parent, Organizations organization, Projects project) {
        Comment comment = new Comment();
        
        comment.setContent(createCommentReplyRequest.getComment());
        comment.setDepth(parent.getDepth() + 1);
        comment.setParent(parent);
        comment.setIssue(parent.getIssue());
        comment.setAuthor(author);
        comment.setOrganization(organization);
        comment.setProject(project);

        return comment;
    }

    

}
