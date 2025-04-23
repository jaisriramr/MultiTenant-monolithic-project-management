package com.projectmanagement.multitenantprojectmanagement.core.workflow;

import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.projectmanagement.multitenantprojectmanagement.core.workflow.transition.TransitionService;
import com.projectmanagement.multitenantprojectmanagement.core.workflow.workflowdto.request.CreateWorkflowRequest;
import com.projectmanagement.multitenantprojectmanagement.core.workflow.workflowdto.request.UpdateWorkflowRequest;
import com.projectmanagement.multitenantprojectmanagement.core.workflow.workflowmapper.WorkflowMapper;
import com.projectmanagement.multitenantprojectmanagement.exception.NotFoundException;
import com.projectmanagement.multitenantprojectmanagement.helper.MaskingString;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class WorkflowService {

    private final WorkflowRepository workflowRepository;
    private final MaskingString maskingString;
    private static final Logger logger = LoggerFactory.getLogger(WorkflowService.class);

    public Workflow getWorkflowById(UUID id) {
        logger.info("Getting workflow for the given ID: {}", maskingString.maskSensitive(id.toString()));

        Workflow workflow = workflowRepository.findById(id).orElseThrow(() -> new NotFoundException("Workflow not found for the given ID: " + id));

        logger.debug("Fetched workflow ID: {}", maskingString.maskSensitive(workflow.getId().toString()));

        return workflow;
    }

    @Transactional
    public Workflow createWorkflow(CreateWorkflowRequest createWorkflowRequest) {
        logger.info("Creating workflow");

        Workflow workflow = WorkflowMapper.toWorkflowEntity(createWorkflowRequest);

        Workflow savedWorkflow = workflowRepository.save(workflow);

        logger.debug("Saved workflow ID: {}", maskingString.maskSensitive(savedWorkflow.getId().toString()));

        return savedWorkflow;
    }

    @Transactional
    public Workflow updateWorkflow(UpdateWorkflowRequest workflowRequest) {
        logger.info("Updating workflow for the given ID: {}", maskingString.maskSensitive(workflowRequest.getId().toString()));

        Workflow workflow = getWorkflowById(workflowRequest.getId());

        logger.debug("Fetched workflow ID: {}", maskingString.maskSensitive(workflow.getId().toString()));

        if(workflowRequest.getName() != null) {
            workflow.setName(workflowRequest.getName());
        }

        if(workflowRequest.getDescription() != null) {
            workflow.setDescription(workflowRequest.getDescription());
        }

        if(workflowRequest.getIsDefault() != null) {
            workflow.setIsDefault(workflowRequest.getIsDefault());
        }

        Workflow updatedWorkflow = workflowRepository.save(workflow);

        logger.debug("Updated workflow ID: {}", maskingString.maskSensitive(updatedWorkflow.getId().toString()));

        return updatedWorkflow;
    }

    @Transactional
    public Workflow deleteWorkflow(UUID id) {
        logger.info("Deleting workflow for the given ID: {}", maskingString.maskSensitive(id.toString()));

        Workflow workflow = getWorkflowById(id);

        logger.debug("Fetched workflow ID: ", maskingString.maskSensitive(workflow.getId().toString()));

        workflowRepository.delete(workflow);

        logger.debug("Workflow with ID: {} is deleted", maskingString.maskSensitive(id.toString()));

        return workflow;
    }


}
