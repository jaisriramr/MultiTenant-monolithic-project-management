package com.projectmanagement.multitenantprojectmanagement.core.workflow.workflowscheme;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WorkflowSchemeRepository extends JpaRepository<WorkflowScheme, UUID> {
    Optional<WorkflowScheme> findByName(String name);
}
