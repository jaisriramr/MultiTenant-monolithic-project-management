package com.projectmanagement.multitenantprojectmanagement.core.comment;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.projectmanagement.multitenantprojectmanagement.auth0.utils.JWTUtils;
import com.projectmanagement.multitenantprojectmanagement.core.comment.dto.request.CreateCommentReplyRequest;
import com.projectmanagement.multitenantprojectmanagement.core.comment.dto.request.CreateCommentRequest;
import com.projectmanagement.multitenantprojectmanagement.core.comment.dto.request.UpdateCommentRequest;
import com.projectmanagement.multitenantprojectmanagement.core.comment.dto.response.CommentResponse;
import com.projectmanagement.multitenantprojectmanagement.core.comment.mapper.CommentMapper;
import com.projectmanagement.multitenantprojectmanagement.core.issue.Issue;
import com.projectmanagement.multitenantprojectmanagement.core.issue.IssueService;
import com.projectmanagement.multitenantprojectmanagement.core.project.ProjectService;
import com.projectmanagement.multitenantprojectmanagement.core.project.Projects;
import com.projectmanagement.multitenantprojectmanagement.exception.NotFoundException;
import com.projectmanagement.multitenantprojectmanagement.helper.MaskingString;
import com.projectmanagement.multitenantprojectmanagement.organizations.Organizations;
import com.projectmanagement.multitenantprojectmanagement.organizations.OrganizationsService;
import com.projectmanagement.multitenantprojectmanagement.organizations.dto.response.PaginatedResponseDto;
import com.projectmanagement.multitenantprojectmanagement.users.UserService;
import com.projectmanagement.multitenantprojectmanagement.users.Users;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final UserService userService;
    private final IssueService issueService;
    private final MaskingString maskingString;
    private static final Logger logger = LoggerFactory.getLogger(CommentService.class);
    private final JWTUtils jwtUtils;
    private final OrganizationsService organizationsService;
    private final ProjectService projectService;

    public Comment getCommentById(UUID id) {
        logger.info("Getting comment for the given ID: {}", maskingString.maskSensitive(id.toString()));

        String auth0OrgId = jwtUtils.getAuth0OrgId();

        Comment comment = commentRepository.findByIdAndOrganization_Auth0Id(id, auth0OrgId).orElseThrow(() -> new NotFoundException("Comment not found for the given ID: " + id));

        logger.debug("Fetched comment ID: {}", maskingString.maskSensitive(comment.getId().toString()));

        return comment;
    }

    public PaginatedResponseDto<CommentResponse> getCommentsByIssueId(UUID id, Pageable pageable) {
        logger.info("Getting comments for the given Issue ID: {}", maskingString.maskSensitive(id.toString()));

        String auth0OrgId = jwtUtils.getAuth0OrgId();

        Page<Comment> comments = commentRepository.findByIssueIdAndDepthAndOrganization_Auth0Id(id, 0, auth0OrgId,pageable);

        logger.debug("Fetched {} comments", comments.getTotalElements());

        return CommentMapper.toPaginatedResponseDto(comments);
    }

    public PaginatedResponseDto<CommentResponse> getRepliesByCommentId(UUID id, Pageable pageable) {
        logger.info("Getting replies for the given comment ID: {}", maskingString.maskSensitive(id.toString()));

        Comment comment = getCommentById(id);

        String auth0OrgId = jwtUtils.getAuth0OrgId();

        Page<Comment> replies = commentRepository.findRepliesByPathPrefix(comment.getIssue().getId(), comment.getPath(), comment.getDepth() + 1, auth0OrgId,pageable);

        return CommentMapper.toPaginatedResponseDto(replies);
    }

    @Transactional
    public CommentResponse createComment(CreateCommentRequest createCommentRequest) {

        logger.info("Creating comment for the given issue ID: {}", maskingString.maskSensitive(createCommentRequest.getIssueId().toString()));

        Issue issue = issueService.getIssueById(createCommentRequest.getIssueId());

        Users author = userService.getUserEntity(createCommentRequest.getAuthorId());

        String auth0OrgId = jwtUtils.getAuth0OrgId();

        Organizations organizations = organizationsService.getOrganizationByAuth0Id(auth0OrgId);

        Projects project = projectService.getProjectById(createCommentRequest.getProjectId());

        Comment comment = CommentMapper.toCommentEntity(createCommentRequest, author, issue, organizations,project);

        Comment savedComment = commentRepository.save(comment);

        savedComment.setPath("/" + savedComment.getId().toString());

        commentRepository.save(savedComment);

        logger.debug("Saved comment ID: {}", maskingString.maskSensitive(savedComment.getId().toString()));

        return CommentMapper.toCommentResponse(savedComment);
    }

    @Transactional
    public CommentResponse createCommentReply(CreateCommentReplyRequest createCommentReplyRequest) {

        logger.info("Creating reply for the given parent ID: {}", maskingString.maskSensitive(createCommentReplyRequest.getParentId().toString()));

        Comment parent = getCommentById(createCommentReplyRequest.getParentId());

        Users author = userService.getUserEntity(createCommentReplyRequest.getAuthorId());

        String auth0OrgId = jwtUtils.getAuth0OrgId();

        Organizations organization = organizationsService.getOrganizationByAuth0Id(auth0OrgId);

        Projects project = projectService.getProjectById(createCommentReplyRequest.getParentId());

        Comment comment = CommentMapper.toCommentReply(createCommentReplyRequest, author, parent, organization, project);

        Comment savedComment = commentRepository.save(comment);

        savedComment.setPath(parent.getPath() + "/" + savedComment.getId().toString());

        commentRepository.save(savedComment);

        logger.debug("Saved reply ID: {}", maskingString.maskSensitive(savedComment.getId().toString()));

        return CommentMapper.toCommentResponse(savedComment);
    }

    @Transactional
    public CommentResponse updateComment(UpdateCommentRequest updateCommentRequest) {
        logger.info("Updating comment for the given ID: {}", maskingString.maskSensitive(updateCommentRequest.getId().toString()));

        Comment comment = getCommentById(updateCommentRequest.getId());

        if(updateCommentRequest.getComment() != null) {
            comment.setContent(updateCommentRequest.getComment());
        }

        Comment updatedComment = commentRepository.save(comment);

        logger.debug("Updated comment ID: {}", maskingString.maskSensitive(updatedComment.getId().toString()));

        return CommentMapper.toCommentResponse(updatedComment);
    }

    @Transactional
    public CommentResponse deleteComment(UUID id) {
        logger.info("Deleting comment for the given ID: {}", maskingString.maskSensitive(id.toString()));

        Comment comment = getCommentById(id);

        commentRepository.delete(comment);

        logger.debug("Deleted comment ID: {}", maskingString.maskSensitive(comment.getId().toString()));

        return CommentMapper.toCommentResponse(comment);

    }

}
