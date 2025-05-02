package com.projectmanagement.multitenantprojectmanagement.core.attachment;

import java.io.IOException;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.projectmanagement.multitenantprojectmanagement.auth0.utils.JWTUtils;
import com.projectmanagement.multitenantprojectmanagement.core.activity.ActivityService;
import com.projectmanagement.multitenantprojectmanagement.core.activity.dto.response.ActivityResponse;
import com.projectmanagement.multitenantprojectmanagement.core.activity.mapper.ActivityMapper;
import com.projectmanagement.multitenantprojectmanagement.core.attachment.dto.response.AttachmentResponse;
import com.projectmanagement.multitenantprojectmanagement.core.attachment.mapper.AttachmentMapper;
import com.projectmanagement.multitenantprojectmanagement.core.comment.Comment;
import com.projectmanagement.multitenantprojectmanagement.core.issue.Issue;
import com.projectmanagement.multitenantprojectmanagement.core.issue.IssueService;
import com.projectmanagement.multitenantprojectmanagement.core.notification.RedisSubscriber;
import com.projectmanagement.multitenantprojectmanagement.core.project.ProjectService;
import com.projectmanagement.multitenantprojectmanagement.core.project.Projects;
import com.projectmanagement.multitenantprojectmanagement.exception.NotFoundException;
import com.projectmanagement.multitenantprojectmanagement.helper.MaskingString;
import com.projectmanagement.multitenantprojectmanagement.organizations.Organizations;
import com.projectmanagement.multitenantprojectmanagement.organizations.OrganizationsService;
import com.projectmanagement.multitenantprojectmanagement.organizations.dto.response.PaginatedResponseDto;
import com.projectmanagement.multitenantprojectmanagement.s3.s3Service;
import com.projectmanagement.multitenantprojectmanagement.users.UserService;
import com.projectmanagement.multitenantprojectmanagement.users.Users;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AttachmentService {

    private final AttachmentRepository attachmentRepository;
    private final MaskingString maskingString;
    private static final Logger logger = LoggerFactory.getLogger(AttachmentService.class);
    private final IssueService issueService;
    private final UserService userService;
    private final ProjectService projectService;
    private final OrganizationsService organizationsService;
    private final s3Service s3Service;
    private final JWTUtils jwtUtils;

    private final ActivityService activityService;
    private final RedisSubscriber redisSubscriber;

    public Attachment getAttachmentById(UUID id) {
        logger.info("Getting attachment by ID: {}", maskingString.maskSensitive(id.toString()));

        String auth0OrgId = jwtUtils.getAuth0OrgId();

        Attachment attachment = attachmentRepository.findByIdAndOrganization_Auth0Id(id, auth0OrgId).orElseThrow(() -> new NotFoundException("Attachment is not found for the given ID: {}" + id));

        logger.debug("Fetched attachment ID: {}", maskingString.maskSensitive(attachment.getId().toString()));

        return attachment;
    }

    public PaginatedResponseDto<AttachmentResponse> getAttachmentsByIssueId(UUID issueId, Pageable pageable) {
        logger.info("Getting attachments by issue ID: {}", maskingString.maskSensitive(issueId.toString()));

        String auth0OrgId = jwtUtils.getAuth0OrgId();

        Page<Attachment> attachments = attachmentRepository.findAllAttachmentsByIssueIdAndOrganization_Auth0Id(issueId, auth0OrgId, pageable);

        logger.debug("Fetched {} attachments", attachments.getTotalElements());

        return AttachmentMapper.toPaginatedResponseDto(attachments);
    }

    public PaginatedResponseDto<AttachmentResponse> getAttachmentByCommentId(UUID commentId, Pageable pageable) {
        logger.info("Getting attachments by comment ID: {}", maskingString.maskSensitive(commentId.toString()));

        String auth0OrgId = jwtUtils.getAuth0OrgId();

        Page<Attachment> attachments = attachmentRepository.findAllAttachmentsByCommentIdAndOrganization_Auth0Id(commentId, auth0OrgId,pageable);

        logger.debug("Fetched {} attachments", attachments.getTotalElements());

        return AttachmentMapper.toPaginatedResponseDto(attachments);
    }

    @Transactional
    public AttachmentResponse createAttachment(MultipartFile file,UUID projectId, UUID issueId, UUID commentId) {
        logger.info("Creating attachment file: {}, userId: {}, issueId: {}, commentId: {}", file, issueId, commentId);
        try {
            Attachment attachment = new Attachment();
            
            String s3Url = s3Service.uploadFile(file, issueId != null ? issueId : commentId , "attachment");

            logger.debug("File uploaded To S3");
            
            String auth0OrgId = jwtUtils.getAuth0OrgId();
            String auth0UserId = jwtUtils.getCurrentUserId();

            Users user = userService.getUserByAuth0Id(auth0UserId);

            Organizations organization = organizationsService.getOrganizationByAuth0Id(auth0OrgId);

            Projects project = projectService.getProjectById(projectId);
            
            attachment.setProject(project);
            attachment.setOrganization(organization);

            attachment.setName(file.getOriginalFilename());
            attachment.setType(file.getContentType());
            attachment.setUrl(s3Url);
            attachment.setUploadedBy(user);
            Issue issue = null;
            if(issueId != null) {
                issue = issueService.getIssueById(issueId);
                attachment.setIssue(issue);
            }
            Comment comment = null;
            if(commentId != null) {
                attachment.setComment(comment);
            }

            Attachment uploadedAttachment = attachmentRepository.save(attachment);

            logger.debug("Saved attachment ID: {}", maskingString.maskSensitive(uploadedAttachment.getId().toString()));

            ActivityResponse activityResponse = activityService.createActivity(ActivityMapper.
                                                toCreateActivityRequest(uploadedAttachment.getId(), 
                                                                            uploadedAttachment.getProject().getId(), 
                                                                            auth0OrgId, 
                                                                            "Created comment", 
                                                                            "add an", 
                                                                            "Attachment",
                                                                            uploadedAttachment.getName(), 
                                                                            "", 
                                                                            "Attachment", 
                                                                            null));

        redisSubscriber.sendNotification(activityResponse.getId().toString());

            return AttachmentMapper.toAttachmentResponse(uploadedAttachment);
        }
        catch (IOException e) {
            logger.error("IO error while processing attachment", e);
            throw new RuntimeException("File processing failed");
        }
        catch(NotFoundException e){
            throw e;    
        }catch(Exception e){
            logger.error("Unexpected error during attachment creation", e);
            throw new RuntimeException("Internal Server while trying to upload an attachment");
        }
    }

    @Transactional
    public AttachmentResponse deleteAttachment(UUID id) {
        logger.info("Deleting attachment for the given ID: {}",maskingString.maskSensitive(id.toString()));

        String auth0OrgId = jwtUtils.getAuth0OrgId();

        Attachment attachment = getAttachmentById(id);

        attachmentRepository.delete(attachment);

        logger.debug("Deleted attachment for the given ID: {}", maskingString.maskSensitive(id.toString()));

        ActivityResponse activityResponse = activityService.createActivity(ActivityMapper.
                                                toCreateActivityRequest(attachment.getId(), 
                                                                            attachment.getProject().getId(), 
                                                                            auth0OrgId, 
                                                                            "Created comment", 
                                                                            "deleted an", 
                                                                            "Attachment",
                                                                            attachment.getName(), 
                                                                            "", 
                                                                            "Attachment", 
                                                                            null));

        redisSubscriber.sendNotification(activityResponse.getId().toString());

        return AttachmentMapper.toAttachmentResponse(attachment);
    }

}
