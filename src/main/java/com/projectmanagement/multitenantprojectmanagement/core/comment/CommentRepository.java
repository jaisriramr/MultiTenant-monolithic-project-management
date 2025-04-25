package com.projectmanagement.multitenantprojectmanagement.core.comment;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentRepository extends JpaRepository<Comment, UUID> {

    Page<Comment> findByIssueIdAndDepth(UUID issueId, int depth, Pageable pageable);
    
    @Query("SELECT c FROM Comment c WHERE c.issue.id = :issueId AND c.path LIKE CONCAT(:pathPrefix, '/%') AND c.depth = :depth ORDER BY c.createdAt")
    Page<Comment> findRepliesByPathPrefix(UUID issueId,String pathPrefix, int depth, Pageable pageable);

}
