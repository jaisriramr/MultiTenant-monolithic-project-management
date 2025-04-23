package com.projectmanagement.multitenantprojectmanagement.core.issue;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IssueRepository extends JpaRepository<Issue, UUID> {

    Optional<Issue> findByKey(String key);

    Page<Issue> findAllBySprintId(UUID sprintId, Pageable pageable);

    Page<Issue> findAllByProjectIdAndSprintIsNull(UUID projectId, Pageable pageable);

}
