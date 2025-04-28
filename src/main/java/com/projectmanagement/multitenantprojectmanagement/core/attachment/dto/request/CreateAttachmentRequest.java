package com.projectmanagement.multitenantprojectmanagement.core.attachment.dto.request;

import java.util.UUID;

import org.springframework.web.multipart.MultipartFile;

import lombok.Data;

@Data
public class CreateAttachmentRequest {
    private UUID issueId;
    private UUID projectId;
    private UUID userId;
    private UUID commentId;
    private MultipartFile file;
}
