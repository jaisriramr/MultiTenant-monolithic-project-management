package com.projectmanagement.multitenantprojectmanagement.service;

import com.projectmanagement.multitenantprojectmanagement.auth0.utils.JWTUtils;
import com.projectmanagement.multitenantprojectmanagement.core.activity.ActivityService;
import com.projectmanagement.multitenantprojectmanagement.core.activity.dto.request.CreateActivityRequest;
import com.projectmanagement.multitenantprojectmanagement.core.activity.dto.response.ActivityResponse;
import com.projectmanagement.multitenantprojectmanagement.core.attachment.*;
import com.projectmanagement.multitenantprojectmanagement.core.attachment.dto.response.AttachmentResponse;
import com.projectmanagement.multitenantprojectmanagement.core.issue.Issue;
import com.projectmanagement.multitenantprojectmanagement.core.issue.IssueService;
import com.projectmanagement.multitenantprojectmanagement.core.notification.RedisSubscriber;
import com.projectmanagement.multitenantprojectmanagement.core.project.ProjectService;
import com.projectmanagement.multitenantprojectmanagement.core.project.Projects;
import com.projectmanagement.multitenantprojectmanagement.exception.NotFoundException;
import com.projectmanagement.multitenantprojectmanagement.helper.MaskingString;
import com.projectmanagement.multitenantprojectmanagement.organizations.OrganizationsService;
import com.projectmanagement.multitenantprojectmanagement.organizations.dto.response.PaginatedResponseDto;
import com.projectmanagement.multitenantprojectmanagement.s3.s3Service;
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
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class AttachmentTest {

    @Mock
    private AttachmentRepository attachmentRepository;

    @Mock
    private MaskingString maskingString;

    @Mock
    private IssueService issueService;

    @Mock
    private UserService userService;

    @Mock
    private ProjectService projectService;

    @Mock
    private OrganizationsService organizationsService;

    @Mock
    private s3Service s3Service;

    @Mock
    private JWTUtils jwtUtils;

    @InjectMocks
    private AttachmentService attachmentService;

    @Mock
    private RedisSubscriber redisSubscriber;

    @Mock
    private ActivityService activityService;

    private UUID attachmentId;
    private Attachment mockAttachment;
    private ActivityResponse activityResponse;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        attachmentId = UUID.fromString("c6e447db-1920-41a8-b728-bf831bf5f096");
        mockAttachment = new Attachment();
        mockAttachment.setId(attachmentId);
        mockAttachment.setName("Test Attachment.jpg");
        mockAttachment.setType("text/plain");
        mockAttachment.setUrl("https://s3.amazonaws.com/test-file");

        Issue issue = new Issue();
        issue.setId(attachmentId);

        Users user = new Users();
        user.setId(attachmentId);

        Projects project = new Projects();
        project.setId(attachmentId);

        mockAttachment.setProject(project);
        mockAttachment.setUploadedBy(user);
        mockAttachment.setIssue(issue);

        activityResponse = new ActivityResponse();
        activityResponse.setId(attachmentId);

    

        when(jwtUtils.getAuth0OrgId()).thenReturn("orgId");
        when(maskingString.maskSensitive(anyString())).thenReturn("masked-string");
    }

    @Test
    void testGetAttachmentById_Success() {

        when(attachmentRepository.findByIdAndOrganization_Auth0Id(attachmentId, "orgId")).thenReturn(Optional.of(mockAttachment));
        when(maskingString.maskSensitive(anyString())).thenReturn("masked");

        Attachment result = attachmentService.getAttachmentById(attachmentId);

        assertNotNull(result);
        assertEquals(attachmentId, result.getId());
        verify(attachmentRepository, times(1)).findByIdAndOrganization_Auth0Id(attachmentId, "orgId");
    }

    @Test
    void testGetAttachmentById_NotFound() {

        when(attachmentRepository.findByIdAndOrganization_Auth0Id(attachmentId, "orgId")).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            attachmentService.getAttachmentById(attachmentId);
        });

        assertEquals("Attachment is not found for the given ID: {}" + attachmentId, exception.getMessage());
        verify(attachmentRepository, times(1)).findByIdAndOrganization_Auth0Id(attachmentId, "orgId");
    }

    @Test
    void testGetAttachmentsByIssueId_Success() {
        Pageable pageable = Pageable.unpaged();
        Page<Attachment> attachments = new PageImpl<>(Collections.emptyList());

        when(attachmentRepository.findAllAttachmentsByIssueIdAndOrganization_Auth0Id(mockAttachment.getIssue().getId(), "orgId", pageable)).thenReturn(attachments);

        PaginatedResponseDto<AttachmentResponse> result = attachmentService.getAttachmentsByIssueId(mockAttachment.getIssue().getId(), pageable);

        assertNotNull(result);
        assertEquals(0, result.getTotalElements());
        verify(attachmentRepository, times(1)).findAllAttachmentsByIssueIdAndOrganization_Auth0Id(mockAttachment.getIssue().getId(), "orgId", pageable);
    }

    @Test
    void testCreateAttachmentWithIssueId() throws IOException {
        // Given
        UUID commentId = null;
        String s3Url = "https://s3.amazonaws.com/bucket/file.txt";

        MultipartFile file = mock(MultipartFile.class);
        // Mock behaviors
        when(file.getOriginalFilename()).thenReturn("file.txt");
        when(file.getContentType()).thenReturn("text/plain");

        when(s3Service.uploadFile(file, mockAttachment.getIssue().getId(), "attachment")).thenReturn(s3Url);

        when(jwtUtils.getAuth0OrgId()).thenReturn("orgId");
        when(jwtUtils.getCurrentUserId()).thenReturn("userId");

        when(userService.getUserByAuth0Id(eq("userId"))).thenReturn(mockAttachment.getUploadedBy());
        when(organizationsService.getOrganizationByAuth0Id(eq("orgId"))).thenReturn(mockAttachment.getOrganization());
        when(projectService.getProjectById(eq(mockAttachment.getProject().getId()))).thenReturn(mockAttachment.getProject());
        when(issueService.getIssueById(eq(mockAttachment.getIssue().getId()))).thenReturn(mockAttachment.getIssue());

        when(attachmentRepository.save(any(Attachment.class))).thenReturn(mockAttachment);
        when(activityService.createActivity(any(CreateActivityRequest.class))).thenReturn(activityResponse);
            
        AttachmentResponse response = attachmentService.createAttachment(file, mockAttachment.getProject().getId(), mockAttachment.getIssue().getId(), commentId);

            
        assertNotNull(response);
        assertEquals(mockAttachment.getId(), response.getId());
        
    }

    @Test
    void testDeleteAttachment_Success() {

        when(attachmentRepository.findByIdAndOrganization_Auth0Id(eq(attachmentId), anyString())).thenReturn(Optional.of(mockAttachment));
        when(activityService.createActivity(any(CreateActivityRequest.class))).thenReturn(activityResponse);
        
        AttachmentResponse result = attachmentService.deleteAttachment(attachmentId);

        assertNotNull(result);
        verify(attachmentRepository, times(1)).delete(mockAttachment);
    }
}
