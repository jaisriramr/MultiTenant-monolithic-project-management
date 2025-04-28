package com.projectmanagement.multitenantprojectmanagement.core.issue;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.projectmanagement.multitenantprojectmanagement.core.issue.enums.IssueType;

@Repository
public interface IssueRepository extends JpaRepository<Issue, UUID> {

    Optional<Issue> findByIdAndOrganization_Auth0Id(UUID id, String auth0Id);

    Optional<Issue> findByKeyAndOrganization_Auth0Id(String key, String auth0Id);

    Page<Issue> findAllBySprintIdAndIsSubTaskFalseAndTypeNotAndOrganization_Auth0Id(UUID sprintId, IssueType type, String auth0Id,Pageable pageable);

    // Page<Issue> findAllBySprintIdAndParentIssueIsNullAndTypeNot(UUID sprintId, IssueType type, Pageable pageable);

    Page<Issue> findAllByProjectIdAndSprintIsNullAndIsSubTaskFalseAndTypeNotAndOrganization_Auth0Id(UUID projectId, IssueType type, String auth0Id,Pageable pageable);

    // Page<Issue> findAllByProjectIdAndSprintIsNullAndParentIssueIsNullAndTypeNot(UUID projectId, IssueType type, Pageable pageable);

    // Page<Issue> findAllByParentIssueId(UUID parentId, Pageable pageable);

}
