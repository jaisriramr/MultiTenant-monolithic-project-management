package com.projectmanagement.multitenantprojectmanagement.core.workflow;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.projectmanagement.multitenantprojectmanagement.core.workflow.workflowdto.request.CreateWorkflowRequest;
import com.projectmanagement.multitenantprojectmanagement.core.workflow.workflowdto.request.UpdateWorkflowRequest;
import com.projectmanagement.multitenantprojectmanagement.core.workflow.workflowdto.response.WorkflowResponse;
import com.projectmanagement.multitenantprojectmanagement.core.workflow.workflowmapper.WorkflowMapper;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;




@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class WorkflowController {

    private final WorkflowService workflowService;

    @GetMapping("/v1/workflow/{id}")
    public ResponseEntity<WorkflowResponse> getWorkflowById(@PathVariable UUID id) {
        Workflow workflow = workflowService.getWorkflowById(id);

        WorkflowResponse response = WorkflowMapper.toWorkflowResponse(workflow);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/v1/workflow")
    public ResponseEntity<Workflow> createWorkflow(@Valid @RequestBody CreateWorkflowRequest createWorkflowRequest) {
        Workflow workflow = workflowService.createWorkflow(createWorkflowRequest);

        return ResponseEntity.ok(workflow);
    }

    @PutMapping("/v1/workflow")
    public ResponseEntity<Workflow> updateWorkflow(@RequestBody UpdateWorkflowRequest updateWorkflowRequest) {
        Workflow workflow = workflowService.updateWorkflow(updateWorkflowRequest);

        return ResponseEntity.ok(workflow);
    }

    @DeleteMapping("/v1/workflow/{id}")
    public ResponseEntity<Workflow> deleteWorkflow(@PathVariable UUID id) {
        Workflow workflow = workflowService.deleteWorkflow(id);

        return ResponseEntity.ok(workflow);
    }
    
    

}
