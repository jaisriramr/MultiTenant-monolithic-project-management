package com.projectmanagement.multitenantprojectmanagement.core.workflow.transition;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.projectmanagement.multitenantprojectmanagement.core.workflow.Workflow;


@Repository
public interface TransitionRepository extends JpaRepository<Transition, UUID> {
    List<Transition> findAllByWorkflowId(UUID workflowId);
}
