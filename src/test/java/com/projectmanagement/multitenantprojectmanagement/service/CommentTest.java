package com.projectmanagement.multitenantprojectmanagement.service;

import com.projectmanagement.multitenantprojectmanagement.auth0.utils.JWTUtils;
import com.projectmanagement.multitenantprojectmanagement.core.comment.*;
import com.projectmanagement.multitenantprojectmanagement.core.comment.dto.request.CreateCommentRequest;
import com.projectmanagement.multitenantprojectmanagement.core.comment.dto.response.CommentResponse;
import com.projectmanagement.multitenantprojectmanagement.core.issue.Issue;
import com.projectmanagement.multitenantprojectmanagement.core.issue.IssueService;
import com.projectmanagement.multitenantprojectmanagement.core.project.ProjectService;
import com.projectmanagement.multitenantprojectmanagement.exception.NotFoundException;
import com.projectmanagement.multitenantprojectmanagement.helper.MaskingString;
import com.projectmanagement.multitenantprojectmanagement.organizations.Organizations;
import com.projectmanagement.multitenantprojectmanagement.organizations.OrganizationsService;
import com.projectmanagement.multitenantprojectmanagement.organizations.dto.response.PaginatedResponseDto;
import com.projectmanagement.multitenantprojectmanagement.users.UserService;
import com.projectmanagement.multitenantprojectmanagement.users.Users;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class CommentTest {

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private UserService userService;

    @Mock
    private IssueService issueService;

    @Mock
    private MaskingString maskingString;

    @Mock
    private JWTUtils jwtUtils;

    @Mock
    private OrganizationsService organizationsService;

    @Mock
    private ProjectService projectService;

    @InjectMocks
    private CommentService commentService;

    private UUID commentId;
    private Comment mockComment;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        commentId = UUID.fromString("c6e447db-1920-41a8-b728-bf831bf5f096");

        Users author = new Users();
        author.setId(commentId);

        Organizations organization = new Organizations();
        organization.setId(commentId);

        Issue issue = new Issue();
        issue.setId(commentId);

        mockComment = new Comment();
        mockComment.setId(commentId);  
        mockComment.setContent("Test comment");
        mockComment.setAuthor(author);
        mockComment.setOrganization(organization);
        mockComment.setDepth(0);
        mockComment.setIssue(issue);
        
        when(jwtUtils.getAuth0OrgId()).thenReturn("orgId");
        when(maskingString.maskSensitive(anyString())).thenReturn("masked-string");

    }

    @Test
    void testGetCommentById_Success() {
        UUID commentId = UUID.randomUUID();
        String auth0OrgId = "auth0|12345";
        Comment comment = new Comment();
        comment.setId(commentId);

        when(jwtUtils.getAuth0OrgId()).thenReturn(auth0OrgId);
        when(commentRepository.findByIdAndOrganization_Auth0Id(commentId, auth0OrgId)).thenReturn(Optional.of(comment));
        when(maskingString.maskSensitive(anyString())).thenReturn("masked");

        Comment result = commentService.getCommentById(commentId);

        assertNotNull(result);
        assertEquals(commentId, result.getId());
        verify(commentRepository, times(1)).findByIdAndOrganization_Auth0Id(commentId, auth0OrgId);
    }

    @Test
    void testGetCommentById_NotFound() {
        UUID commentId = UUID.randomUUID();
        String auth0OrgId = "auth0|12345";

        when(jwtUtils.getAuth0OrgId()).thenReturn(auth0OrgId);
        when(commentRepository.findByIdAndOrganization_Auth0Id(commentId, auth0OrgId)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            commentService.getCommentById(commentId);
        });

        assertEquals("Comment not found for the given ID: " + commentId, exception.getMessage());
        verify(commentRepository, times(1)).findByIdAndOrganization_Auth0Id(commentId, auth0OrgId);
    }

    @Test
    void testGetCommentsByIssueId_Success() {
        UUID issueId = UUID.randomUUID();
        String auth0OrgId = "auth0|12345";
        Pageable pageable = Pageable.unpaged();
        Page<Comment> comments = new PageImpl<>(Collections.emptyList());

        when(jwtUtils.getAuth0OrgId()).thenReturn(auth0OrgId);
        when(commentRepository.findByIssueIdAndDepthAndOrganization_Auth0Id(issueId, 0, auth0OrgId, pageable)).thenReturn(comments);

        PaginatedResponseDto<CommentResponse> result = commentService.getCommentsByIssueId(issueId, pageable);

        assertNotNull(result);
        assertEquals(0, result.getTotalElements());
        verify(commentRepository, times(1)).findByIssueIdAndDepthAndOrganization_Auth0Id(issueId, 0, auth0OrgId, pageable);
    }

    @Test
    void testCreateComment_Success() {
        CreateCommentRequest request = new CreateCommentRequest();
        request.setIssueId(commentId);
        request.setAuthorId(commentId);
        request.setProjectId(commentId);

        when(issueService.getIssueById(request.getIssueId())).thenReturn(mockComment.getIssue());
        when(userService.getUserEntity(request.getAuthorId())).thenReturn(mockComment.getAuthor());
        when(jwtUtils.getAuth0OrgId()).thenReturn("auth0|12345");
        when(organizationsService.getOrganizationByAuth0Id("auth0|12345")).thenReturn(mockComment.getOrganization());
        when(projectService.getProjectById(request.getProjectId())).thenReturn(mockComment.getProject());
        when(commentRepository.save(any(Comment.class))).thenReturn(mockComment);

        CommentResponse result = commentService.createComment(request);

        assertNotNull(result);
        verify(commentRepository, times(2)).save(any(Comment.class)); // Saved twice: once for initial save, once for path update
    }

    @Test
    void testDeleteComment_Success() {

        when(commentRepository.findByIdAndOrganization_Auth0Id(eq(commentId), anyString())).thenReturn(Optional.of(mockComment));

        CommentResponse result = commentService.deleteComment(commentId);

        assertNotNull(result);
        assertEquals(commentId, result.getId());
    }
}
