package com.projectmanagement.multitenantprojectmanagement.core.workflow.transition.mapper;

import java.util.ArrayList;

import com.projectmanagement.multitenantprojectmanagement.core.workflow.Workflow;
import com.projectmanagement.multitenantprojectmanagement.core.workflow.status.Status;
import com.projectmanagement.multitenantprojectmanagement.core.workflow.status.dto.response.StatusResponse;
import com.projectmanagement.multitenantprojectmanagement.core.workflow.status.mapper.StatusMapper;
import com.projectmanagement.multitenantprojectmanagement.core.workflow.transition.Transition;
import com.projectmanagement.multitenantprojectmanagement.core.workflow.transition.dto.request.CreateTransitionRequest;
import com.projectmanagement.multitenantprojectmanagement.core.workflow.transition.dto.response.TransitionResponse;

public class TransitionMapper {

    public static Transition toTransitionEntity(CreateTransitionRequest createTransitionRequest, Status from, Status to, Workflow workflow) {

        Transition transition = new Transition();

        transition.setName(createTransitionRequest.getName());
        transition.setFrom(from);
        transition.setTo(to);
        if(workflow.getTransitions() == null) {
            workflow.setTransitions(new ArrayList<>());
        }

        workflow.getTransitions().add(transition);
        transition.setWorkflow(workflow);

        return transition;

    }

    public static TransitionResponse toTransitionResponse(Transition transition) {

        StatusResponse from = StatusMapper.toStatusResponse(transition.getFrom());

        StatusResponse to = StatusMapper.toStatusResponse(transition.getTo());

        return TransitionResponse.builder()
                .id(transition.getId())
                .name(transition.getName())
                .from(from)
                .to(to)
                .build();
    }

}
