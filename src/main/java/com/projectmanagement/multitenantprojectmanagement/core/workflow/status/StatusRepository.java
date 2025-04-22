package com.projectmanagement.multitenantprojectmanagement.core.workflow.status;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.projectmanagement.multitenantprojectmanagement.core.workflow.Workflow;

@Repository
public interface StatusRepository extends JpaRepository<Status, UUID> {
    
}
