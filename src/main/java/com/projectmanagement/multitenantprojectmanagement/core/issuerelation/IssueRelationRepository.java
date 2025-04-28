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
    Optional<IssueRelation> findByChildIssueIdAndOrganization_Auth0Id(UUID childId, String auth0Id);

    Page<IssueRelation> findAllByParentIssueIdAndTypeEqualsAndOrganization_Auth0Id(UUID parentId, IssueRelationType type, String auth0Id,Pageable pageable);

}
