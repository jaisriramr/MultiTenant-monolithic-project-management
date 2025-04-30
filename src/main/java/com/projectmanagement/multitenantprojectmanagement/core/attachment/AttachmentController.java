package com.projectmanagement.multitenantprojectmanagement.core.attachment;

import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.projectmanagement.multitenantprojectmanagement.core.attachment.dto.request.CreateAttachmentRequest;
import com.projectmanagement.multitenantprojectmanagement.core.attachment.dto.response.AttachmentResponse;
import com.projectmanagement.multitenantprojectmanagement.core.attachment.mapper.AttachmentMapper;
import com.projectmanagement.multitenantprojectmanagement.organizations.dto.response.PaginatedResponseDto;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AttachmentController {

    private final AttachmentService attachmentService;

    @GetMapping("/v1/attachment/{id}")
    public ResponseEntity<AttachmentResponse> findAttachmentById(@PathVariable UUID id,@AuthenticationPrincipal Jwt jwt) {
        Attachment attachment = attachmentService.getAttachmentById(id);

        AttachmentResponse attachmentResponse = AttachmentMapper.toAttachmentResponse(attachment);

        return ResponseEntity.ok(attachmentResponse);
    }

    @GetMapping("/v1/attachment/{id}/issue")
    public ResponseEntity<PaginatedResponseDto<AttachmentResponse>> findAllAttachmentsByIssueId(@PathVariable UUID id, Pageable pageable,@AuthenticationPrincipal Jwt jwt) {
        PaginatedResponseDto<AttachmentResponse> response = attachmentService.getAttachmentsByIssueId(id, pageable);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/v1/attachment/{id}/comment")
    public ResponseEntity<PaginatedResponseDto<AttachmentResponse>> findAllAttachmentsByCommentId(@PathVariable UUID id, Pageable pageable,@AuthenticationPrincipal Jwt jwt) {
        PaginatedResponseDto<AttachmentResponse> response = attachmentService.getAttachmentByCommentId(id, pageable);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/v1/attachment/issue")
    public ResponseEntity<AttachmentResponse> createAttachmentIssue(@Valid @RequestPart("file") MultipartFile file, @RequestParam("projectId") UUID projectId,@RequestParam("issueId") UUID issueId,@AuthenticationPrincipal Jwt jwt) {
        AttachmentResponse response = attachmentService.createAttachment(file,projectId, issueId, null);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/v1/attachment/comment")
    public ResponseEntity<AttachmentResponse> createAttachmentComment(@Valid @RequestPart("file") MultipartFile file, @RequestParam("projectId") UUID projectId,@RequestParam("commentId") UUID commentId,@AuthenticationPrincipal Jwt jwt) {
        AttachmentResponse response = attachmentService.createAttachment(file, projectId,null, commentId);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/v1/attachment/{id}")
    public ResponseEntity<AttachmentResponse> deleteAttachmentById(@PathVariable UUID id,@AuthenticationPrincipal Jwt jwt) {
        AttachmentResponse attachmentResponse = attachmentService.deleteAttachment(id);

        return ResponseEntity.ok(attachmentResponse);
    }

}
