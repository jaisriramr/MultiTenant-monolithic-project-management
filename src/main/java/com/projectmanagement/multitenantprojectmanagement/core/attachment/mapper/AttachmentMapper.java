package com.projectmanagement.multitenantprojectmanagement.core.attachment.mapper;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;

import com.projectmanagement.multitenantprojectmanagement.core.attachment.Attachment;
import com.projectmanagement.multitenantprojectmanagement.core.attachment.dto.response.AttachmentResponse;
import com.projectmanagement.multitenantprojectmanagement.organizations.dto.response.PaginatedResponseDto;
import com.projectmanagement.multitenantprojectmanagement.users.dto.mapper.UserMapper;

public class AttachmentMapper {

    public static AttachmentResponse toAttachmentResponse(Attachment attachment) {
        return AttachmentResponse.builder()
                .id(attachment.getId())
                .name(attachment.getName())
                .type(attachment.getType())
                .url(attachment.getUrl())
                .issueId(attachment.getIssue().getId())
                .uploadedBy(UserMapper.toUserSingleListResponse(attachment.getUploadedBy()))
                .createdAt(attachment.getCreatedAt())
                .updatedAt(attachment.getUpdatedAt())
                .build();
    }

    public static List<AttachmentResponse> toListAttachmentResponse(List<Attachment> attachments) {
        List<AttachmentResponse> response = new ArrayList<>();

        for(Attachment attachment: attachments) {
            response.add(toAttachmentResponse(attachment));
        }

        return response;
    }

    public static PaginatedResponseDto<AttachmentResponse> toPaginatedResponseDto(Page<Attachment> attachments) {

        return PaginatedResponseDto.<AttachmentResponse>builder()
                .data(toListAttachmentResponse(attachments.getContent()))
                .size(attachments.getSize())
                .totalElements(attachments.getTotalElements())
                .totalPages(attachments.getTotalPages())
                .page(attachments.getNumber())
                .build();

    }

    // public static Attachment toAttachmentEntity() {

    // }

}
