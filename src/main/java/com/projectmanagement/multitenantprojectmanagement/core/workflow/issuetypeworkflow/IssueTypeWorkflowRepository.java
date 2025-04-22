package com.projectmanagement.multitenantprojectmanagement.core.workflow.issuetypeworkflow;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.projectmanagement.multitenantprojectmanagement.core.workflow.workflowscheme.WorkflowScheme;


@Repository
public interface IssueTypeWorkflowRepository extends JpaRepository<IssueTypeWorkflow, UUID> {
    List<IssueTypeWorkflow> findByWorkflowScheme(WorkflowScheme workflowScheme);
}
