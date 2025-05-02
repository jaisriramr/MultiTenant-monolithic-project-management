package com.projectmanagement.multitenantprojectmanagement.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.projectmanagement.multitenantprojectmanagement.core.attachment.Attachment;
import com.projectmanagement.multitenantprojectmanagement.core.attachment.AttachmentController;
import com.projectmanagement.multitenantprojectmanagement.core.attachment.AttachmentService;
import com.projectmanagement.multitenantprojectmanagement.core.attachment.dto.response.AttachmentResponse;
import com.projectmanagement.multitenantprojectmanagement.core.comment.Comment;
import com.projectmanagement.multitenantprojectmanagement.core.issue.Issue;
import com.projectmanagement.multitenantprojectmanagement.core.project.Projects;
import com.projectmanagement.multitenantprojectmanagement.organizations.Organizations;
import com.projectmanagement.multitenantprojectmanagement.organizations.dto.response.PaginatedResponseDto;
import com.projectmanagement.multitenantprojectmanagement.users.Users;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AttachmentController.class)
@AutoConfigureMockMvc(addFilters = false)
public class AttachmentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AttachmentService attachmentService;

    @Autowired
    private ObjectMapper objectMapper;

    private UUID attachmentId;
    private UUID issueId;
    private UUID commentId;
    private UUID projectId;

    private Attachment mockAttachment;
    private PaginatedResponseDto<AttachmentResponse> paginatedResponseDto;
    private AttachmentResponse mockAttachmentResponse;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        attachmentId = UUID.randomUUID();
        issueId = UUID.randomUUID();
        commentId = UUID.randomUUID();
        projectId = UUID.randomUUID();

        mockAttachment = new Attachment();
        mockAttachment.setId(attachmentId);

        Comment comment = new Comment();
        comment.setId(attachmentId);
        
        Issue issue = new Issue();
        issue.setId(attachmentId);
        
        Users user = new Users();
        user.setId(attachmentId);

        Organizations organization = new Organizations();
        organization.setId(attachmentId);

        Projects project = new Projects();
        project.setId(attachmentId);

        mockAttachment.setComment(comment);
        mockAttachment.setIssue(issue);
        mockAttachment.setUploadedBy(user);
        mockAttachment.setOrganization(organization);
        mockAttachment.setProject(project);

        mockAttachmentResponse = new AttachmentResponse();
        mockAttachmentResponse.setId(attachmentId);
        mockAttachmentResponse.setIssueId(issueId);
        mockAttachmentResponse.setCommentId(commentId);
        mockAttachmentResponse.setName("Test name");
        mockAttachmentResponse.setUrl("test.com");
        mockAttachmentResponse.setUploadedBy(null);

        paginatedResponseDto = PaginatedResponseDto.<AttachmentResponse>builder()
                                .data(List.of(mockAttachmentResponse))
                                .totalElements(10)
                                .build();

    }

    @Test
    void testFindAttachmentById_Success() throws Exception {

        when(attachmentService.getAttachmentById(attachmentId)).thenReturn(mockAttachment);

        mockMvc.perform(get("/api/v1/attachment/{id}", attachmentId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(attachmentId.toString()));

        verify(attachmentService, times(1)).getAttachmentById(attachmentId);
    }

    @Test
    void testFindAllAttachmentsByIssueId_Success() throws Exception {

        when(attachmentService.getAttachmentsByIssueId(eq(issueId), any())).thenReturn(paginatedResponseDto);

        mockMvc.perform(get("/api/v1/attachment/{id}/issue", issueId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray());

        verify(attachmentService, times(1)).getAttachmentsByIssueId(eq(issueId), any());
    }

    @Test
    void testFindAllAttachmentsByCommentId_Success() throws Exception {

        when(attachmentService.getAttachmentByCommentId(eq(commentId), any())).thenReturn(paginatedResponseDto);

        mockMvc.perform(get("/api/v1/attachment/{id}/comment", commentId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray());

        verify(attachmentService, times(1)).getAttachmentByCommentId(eq(commentId), any());
    }

    @Test
    void testCreateAttachmentIssue_Success() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "test.txt", MediaType.TEXT_PLAIN_VALUE, "Test content".getBytes());
        AttachmentResponse response = new AttachmentResponse();
        response.setId(attachmentId);

        when(attachmentService.createAttachment(any(), eq(projectId), eq(issueId), isNull())).thenReturn(response);

        mockMvc.perform(multipart("/api/v1/attachment/issue")
                .file(file)
                .param("projectId", projectId.toString())
                .param("issueId", issueId.toString())
                .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(attachmentId.toString()));

        verify(attachmentService, times(1)).createAttachment(any(), eq(projectId), eq(issueId), isNull());
    }

    @Test
    void testCreateAttachmentComment_Success() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "test.txt", MediaType.TEXT_PLAIN_VALUE, "Test content".getBytes());
        AttachmentResponse response = new AttachmentResponse();
        response.setId(attachmentId);

        when(attachmentService.createAttachment(any(), eq(projectId), isNull(), eq(commentId))).thenReturn(response);

        mockMvc.perform(multipart("/api/v1/attachment/comment")
                .file(file)
                .param("projectId", projectId.toString())
                .param("commentId", commentId.toString())
                .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(attachmentId.toString()));

        verify(attachmentService, times(1)).createAttachment(any(), eq(projectId), isNull(), eq(commentId));
    }

    @Test
    void testDeleteAttachmentById_Success() throws Exception {
        AttachmentResponse response = new AttachmentResponse();
        response.setId(attachmentId);

        when(attachmentService.deleteAttachment(attachmentId)).thenReturn(response);

        mockMvc.perform(delete("/api/v1/attachment/{id}", attachmentId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(attachmentId.toString()));

        verify(attachmentService, times(1)).deleteAttachment(attachmentId);
    }
}
