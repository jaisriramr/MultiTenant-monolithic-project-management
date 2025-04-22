package com.projectmanagement.multitenantprojectmanagement.core.workflow;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WorkflowRepository extends JpaRepository<Workflow, UUID> {
    Optional<Workflow> findByName(String name);
}
