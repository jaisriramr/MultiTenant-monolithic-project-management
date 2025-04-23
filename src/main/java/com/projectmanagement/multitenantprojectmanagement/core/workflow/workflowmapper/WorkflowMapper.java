package com.projectmanagement.multitenantprojectmanagement.core.workflow.workflowmapper;

import java.util.ArrayList;
import java.util.List;

import com.projectmanagement.multitenantprojectmanagement.core.workflow.Workflow;
import com.projectmanagement.multitenantprojectmanagement.core.workflow.transition.Transition;
import com.projectmanagement.multitenantprojectmanagement.core.workflow.transition.dto.response.TransitionResponse;
import com.projectmanagement.multitenantprojectmanagement.core.workflow.transition.mapper.TransitionMapper;
import com.projectmanagement.multitenantprojectmanagement.core.workflow.workflowdto.request.CreateWorkflowRequest;
import com.projectmanagement.multitenantprojectmanagement.core.workflow.workflowdto.response.WorkflowResponse;

public class WorkflowMapper {

    public static Workflow toWorkflowEntity(CreateWorkflowRequest createWorkflowRequest) {

        Workflow workflow = new Workflow();

        workflow.setName(createWorkflowRequest.getName());
        workflow.setDescription(createWorkflowRequest.getDescription());
        workflow.setIsDefault(createWorkflowRequest.getIsDefault());
        workflow.setTransitions(new ArrayList<>());

        return workflow;
    }

    public static WorkflowResponse toWorkflowResponse(Workflow workflow) {

        List<TransitionResponse> transitionResponses = new ArrayList<>();

        for(Transition transition: workflow.getTransitions()) {
            TransitionResponse t = TransitionMapper.toTransitionResponse(transition);
            transitionResponses.add(t);
        }

        return WorkflowResponse.builder()
                .id(workflow.getId())
                .name(workflow.getName())
                .isDefault(workflow.getIsDefault())
                .transactions(transitionResponses)
                .build();

    }

}
