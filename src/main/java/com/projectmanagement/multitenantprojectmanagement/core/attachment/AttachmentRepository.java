package com.projectmanagement.multitenantprojectmanagement.core.attachment;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AttachmentRepository extends JpaRepository<Attachment, UUID> {

    Page<Attachment> findAllAttachmentsByIssueId(UUID issueId, Pageable pageable);

    Page<Attachment> findAllAttachmentsByCommentId(UUID commentId, Pageable pageable);

}
