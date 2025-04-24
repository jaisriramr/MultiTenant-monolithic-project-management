package com.projectmanagement.multitenantprojectmanagement.core.issuerelation;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.projectmanagement.multitenantprojectmanagement.core.issuerelation.enums.IssueRelationType;

@Repository
public interface IssueRelationRepository extends JpaRepository<IssueRelation, UUID> {
    Optional<IssueRelation> findByChildIssueId(UUID childId);

    Page<IssueRelation> findAllByParentIssueIdAndTypeEquals(UUID parentId, IssueRelationType type, Pageable pageable);

}
