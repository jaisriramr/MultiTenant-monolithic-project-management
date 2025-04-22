package com.projectmanagement.multitenantprojectmanagement.core.workflow.issuetypeworkflow;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.projectmanagement.multitenantprojectmanagement.core.workflow.workflowscheme.WorkflowScheme;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class IssueTypeWorkflowService {

    private final IssueTypeWorkflowRepository issueTypeWorkflowRepository;


    public IssueTypeWorkflow createIssueTypeWorkflow(IssueTypeWorkflow issueTypeWorkflow) {
        return issueTypeWorkflowRepository.save(issueTypeWorkflow);
    }

    public List<IssueTypeWorkflow> getIssueTypeWorkflowsByScheme(WorkflowScheme workflowScheme) {
        return issueTypeWorkflowRepository.findByWorkflowScheme(workflowScheme);
    }

    public void deleteIssueTypeWorkflow(UUID id) {
        issueTypeWorkflowRepository.deleteById(id);
    }

}
