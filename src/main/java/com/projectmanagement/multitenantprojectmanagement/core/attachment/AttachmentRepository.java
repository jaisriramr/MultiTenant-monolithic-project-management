package com.projectmanagement.multitenantprojectmanagement.core.attachment;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AttachmentRepository extends JpaRepository<Attachment, UUID> {

    Optional<Attachment> findByIdAndOrganization_Auth0Id(UUID id,String auth0Id);

    Page<Attachment> findAllAttachmentsByIssueIdAndOrganization_Auth0Id(UUID issueId, String auth0Id,Pageable pageable);

    Page<Attachment> findAllAttachmentsByCommentIdAndOrganization_Auth0Id(UUID commentId, String auth0Id,Pageable pageable);

}
