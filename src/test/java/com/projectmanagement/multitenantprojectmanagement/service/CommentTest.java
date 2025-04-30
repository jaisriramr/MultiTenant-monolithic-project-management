package com.projectmanagement.multitenantprojectmanagement.service;

import com.projectmanagement.multitenantprojectmanagement.auth0.utils.JWTUtils;
import com.projectmanagement.multitenantprojectmanagement.core.comment.*;
import com.projectmanagement.multitenantprojectmanagement.core.comment.dto.response.CommentResponse;
import com.projectmanagement.multitenantprojectmanagement.core.issue.IssueService;
import com.projectmanagement.multitenantprojectmanagement.core.project.ProjectService;
import com.projectmanagement.multitenantprojectmanagement.exception.NotFoundException;
import com.projectmanagement.multitenantprojectmanagement.helper.MaskingString;
import com.projectmanagement.multitenantprojectmanagement.organizations.OrganizationsService;
import com.projectmanagement.multitenantprojectmanagement.organizations.dto.response.PaginatedResponseDto;
import com.projectmanagement.multitenantprojectmanagement.users.UserService;
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

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
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

    // @Test
    // void testCreateComment_Success() {
    //     CreateCommentRequest request = new CreateCommentRequest();
    //     request.setIssueId(UUID.randomUUID());
    //     request.setAuthorId(UUID.randomUUID());
    //     request.setProjectId(UUID.randomUUID());

    //     Issue issue = new Issue();
    //     Users author = new Users();
    //     Organizations organization = new Organizations();
    //     Projects project = new Projects();
    //     Comment comment = new Comment();

    //     when(issueService.getIssueById(request.getIssueId())).thenReturn(issue);
    //     when(userService.getUserEntity(request.getAuthorId())).thenReturn(author);
    //     when(jwtUtils.getAuth0OrgId()).thenReturn("auth0|12345");
    //     when(organizationsService.getOrganizationByAuth0Id("auth0|12345")).thenReturn(organization);
    //     when(projectService.getProjectById(request.getProjectId())).thenReturn(project);
    //     when(commentRepository.save(any(Comment.class))).thenReturn(comment);

    //     CommentResponse result = commentService.createComment(request);

    //     assertNotNull(result);
    //     verify(commentRepository, times(2)).save(any(Comment.class)); // Saved twice: once for initial save, once for path update
    // }

    // @Test
    // void testDeleteComment_Success() {
    //     UUID commentId = UUID.randomUUID();
    //     Comment comment = new Comment();
    //     comment.setId(commentId);

    //     when(commentRepository.findByIdAndOrganization_Auth0Id(eq(commentId), anyString())).thenReturn(Optional.of(comment));

    //     CommentResponse result = commentService.deleteComment(commentId);

    //     assertNotNull(result);
    //     verify(commentRepository, times(1)).delete(comment);
    // }
}
