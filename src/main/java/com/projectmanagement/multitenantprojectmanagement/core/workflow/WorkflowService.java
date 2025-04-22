package com.projectmanagement.multitenantprojectmanagement.core.workflow;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class WorkflowService {

    private final WorkflowRepository workflowRepository;

    @Transactional
    public Workflow createWorkflow(Workflow workflow) {
        return workflowRepository.save(workflow);
    }


    public Workflow getWorkflowById(UUID id) {
        return workflowRepository.findById(id).orElseThrow(() -> new RuntimeException("Workflow not found"));
    }

    public List<Workflow> getAllWorkflows() {
        return workflowRepository.findAll();
    }

    public void deleteWorkflow(UUID id) {
        workflowRepository.deleteById(id);
    }
}
