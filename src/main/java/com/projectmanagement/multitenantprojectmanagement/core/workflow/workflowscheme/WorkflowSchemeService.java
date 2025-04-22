package com.projectmanagement.multitenantprojectmanagement.core.workflow.workflowscheme;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class WorkflowSchemeService {

    private final WorkflowSchemeRepository workflowSchemeRepository;

    public WorkflowScheme createWorkflowScheme(WorkflowScheme workflowScheme) {
        return workflowSchemeRepository.save(workflowScheme);
    }

    public WorkflowScheme getWorkflowSchemeById(UUID id) {
        return workflowSchemeRepository.findById(id).orElseThrow(() -> new RuntimeException("Workflow Scheme not found"));
    }

    public List<WorkflowScheme> getAllWorkflowSchemes() {
        return workflowSchemeRepository.findAll();
    }

    public void deleteWorkflowScheme(UUID id) {
        workflowSchemeRepository.deleteById(id);
    }

}
