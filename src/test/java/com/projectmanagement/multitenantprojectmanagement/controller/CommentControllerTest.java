package com.projectmanagement.multitenantprojectmanagement.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.projectmanagement.multitenantprojectmanagement.auth0.utils.JWTUtils;
import com.projectmanagement.multitenantprojectmanagement.core.comment.Comment;
import com.projectmanagement.multitenantprojectmanagement.core.comment.CommentController;
import com.projectmanagement.multitenantprojectmanagement.core.comment.CommentService;
import com.projectmanagement.multitenantprojectmanagement.core.comment.dto.request.CreateCommentReplyRequest;
import com.projectmanagement.multitenantprojectmanagement.core.comment.dto.request.CreateCommentRequest;
import com.projectmanagement.multitenantprojectmanagement.core.comment.dto.request.UpdateCommentRequest;
import com.projectmanagement.multitenantprojectmanagement.core.comment.dto.response.CommentResponse;
import com.projectmanagement.multitenantprojectmanagement.core.issue.Issue;
import com.projectmanagement.multitenantprojectmanagement.core.project.Projects;
import com.projectmanagement.multitenantprojectmanagement.organizations.Organizations;
import com.projectmanagement.multitenantprojectmanagement.organizations.dto.response.PaginatedResponseDto;
import com.projectmanagement.multitenantprojectmanagement.users.Users;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.Collections;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CommentController.class)
@AutoConfigureMockMvc(addFilters = false)
public class CommentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CommentService commentService;

    @MockBean
    private JWTUtils jwtUtils;

    @Autowired
    private ObjectMapper objectMapper;

    private UUID commentId;
    private UUID issueId;

    private Comment comment;
    private PaginatedResponseDto<CommentResponse> paginatedResponseDto;
    private CommentResponse commentResponse;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        commentId = UUID.fromString("c6e447db-1920-41a8-b728-bf831bf5f096");
        issueId = UUID.randomUUID();

        Users users = new Users();
        users.setId(commentId);
        
        Comment comment = new Comment();
        comment.setId(commentId);
        comment.setAuthor(users);
        comment.setCreatedAt(Instant.now());
        comment.setUpdatedAt(Instant.now());
        
        Projects projects = new Projects();
        projects.setId(commentId);
        projects.setCreatedAt(Instant.now());
        projects.setUpdatedAt(Instant.now());

        comment.setProject(projects);
        
        Organizations organization = new Organizations();
        organization.setId(commentId);

        comment.setOrganization(organization);

        Issue issue = new Issue();
        issue.setId(issueId);

        comment.setIssue(issue);
        commentResponse = new CommentResponse();
        commentResponse.setId(commentId);

        paginatedResponseDto = PaginatedResponseDto.<CommentResponse>builder()
                                .data(Collections.singletonList(commentResponse))
                                .page(0)
                                .size(0)
                                .totalElements(0)   
                                .totalPages(0)
                                .build();

    }

    @Test
    void testGetCommentById_Success() throws Exception {

        Comment mockComment = new Comment();
        mockComment.setId(commentId);
        mockComment.setAuthor(new Users());
        mockComment.getAuthor().setId(commentId);

        when(commentService.getCommentById(commentId)).thenReturn(mockComment);

        mockMvc.perform(get("/api/v1/comment/{id}", commentId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(commentId.toString()));

        verify(commentService, times(1)).getCommentById(commentId);
    }

    @Test
    void testGetCommentsByIssueId_Success() throws Exception {

        when(commentService.getCommentsByIssueId(eq(issueId), any())).thenReturn(paginatedResponseDto);

        mockMvc.perform(get("/api/v1/comment/{id}/issue", issueId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray());

        verify(commentService, times(1)).getCommentsByIssueId(eq(issueId), any());
    }

    @Test
    void testGetCommentReplies_Success() throws Exception {

        when(commentService.getRepliesByCommentId(eq(commentId), any())).thenReturn(paginatedResponseDto);

        mockMvc.perform(get("/api/v1/comment/{id}/replies", commentId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray());

        verify(commentService, times(1)).getRepliesByCommentId(eq(commentId), any());
    }

    @Test
    void testCreateComment_Success() throws Exception {
        CreateCommentRequest request = new CreateCommentRequest();
        CommentResponse response = new CommentResponse();
        response.setId(commentId);

        when(commentService.createComment(any(CreateCommentRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/v1/comment")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(commentId.toString()));

        verify(commentService, times(1)).createComment(any(CreateCommentRequest.class));
    }

    @Test
    void testCreateCommentReply_Success() throws Exception {
        CreateCommentReplyRequest request = new CreateCommentReplyRequest();
        CommentResponse response = new CommentResponse();
        response.setId(commentId);

        when(commentService.createCommentReply(any(CreateCommentReplyRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/v1/comment/reply")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(commentId.toString()));

        verify(commentService, times(1)).createCommentReply(any(CreateCommentReplyRequest.class));
    }

    @Test
    void testUpdateComment_Success() throws Exception {
        UpdateCommentRequest request = new UpdateCommentRequest();
        CommentResponse response = new CommentResponse();
        response.setId(commentId);

        when(commentService.updateComment(any(UpdateCommentRequest.class))).thenReturn(response);

        mockMvc.perform(put("/api/v1/comment")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(commentId.toString()));

        verify(commentService, times(1)).updateComment(any(UpdateCommentRequest.class));
    }

    @Test
    void testDeleteCommentById_Success() throws Exception {
        CommentResponse response = new CommentResponse();
        response.setId(commentId);

        when(commentService.deleteComment(commentId)).thenReturn(response);

        mockMvc.perform(delete("/api/v1/comment/{id}", commentId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(commentId.toString()));

        verify(commentService, times(1)).deleteComment(commentId);
    }
}
