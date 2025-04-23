package com.projectmanagement.multitenantprojectmanagement.core.workflow.transition;

import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.projectmanagement.multitenantprojectmanagement.core.workflow.Workflow;
import com.projectmanagement.multitenantprojectmanagement.core.workflow.WorkflowService;
import com.projectmanagement.multitenantprojectmanagement.core.workflow.status.Status;
import com.projectmanagement.multitenantprojectmanagement.core.workflow.status.StatusService;
import com.projectmanagement.multitenantprojectmanagement.core.workflow.transition.dto.request.CreateTransitionRequest;
import com.projectmanagement.multitenantprojectmanagement.core.workflow.transition.dto.request.UpdateTransitionRequest;
import com.projectmanagement.multitenantprojectmanagement.core.workflow.transition.dto.response.TransitionResponse;
import com.projectmanagement.multitenantprojectmanagement.core.workflow.transition.mapper.TransitionMapper;
import com.projectmanagement.multitenantprojectmanagement.exception.NotFoundException;
import com.projectmanagement.multitenantprojectmanagement.helper.MaskingString;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TransitionService {

    private final TransitionRepository transitionRepository;
    private static final Logger logger = LoggerFactory.getLogger(TransitionService.class);
    private final MaskingString maskingString;
    private final WorkflowService workflowService;
    private final StatusService statusService;

    public Transition getTransitionById(UUID id) {
        logger.info("Getting transition for the given ID: {}", maskingString.maskSensitive(id.toString()));

        Transition transition = transitionRepository.findById(id).orElseThrow(() -> new NotFoundException("Transition not found for the given ID: " + id));

        logger.debug("Fetched transition ID: ", maskingString.maskSensitive(transition.getId().toString()));

        return transition;
    }

    public List<Transition> getAllTransitionsByWorkflowId(UUID workflowId) {
        logger.info("Getting all transitions for the given workflow ID: {}", maskingString.maskSensitive(workflowId.toString()));

        List<Transition> transitions = transitionRepository.findAllByWorkflowId(workflowId);

        logger.debug("Fetched {} transitions", transitions.size());

        return transitions;
    }

    public TransitionResponse createTransition(CreateTransitionRequest createTransitionRequest) {
        logger.info("Creating transition");

        Status fromStatus = statusService.getFullStatusDetails(createTransitionRequest.getFromId());

        Status toStatus = statusService.getFullStatusDetails(createTransitionRequest.getToId());

        Workflow workflow = workflowService.getWorkflowById(createTransitionRequest.getWorkflowId());

        Transition transitionEntity = TransitionMapper.toTransitionEntity(createTransitionRequest, fromStatus, toStatus, workflow);

        Transition transition = transitionRepository.save(transitionEntity);


        logger.debug("Saved transition ID: {}", maskingString.maskSensitive(transition.getId().toString()));

        return TransitionMapper.toTransitionResponse(transition);
    }

    public TransitionResponse updateTransition(UpdateTransitionRequest updateTransitionRequest) {
        logger.info("Updating transition");

        Transition transition = getTransitionById(updateTransitionRequest.getId());

        if(updateTransitionRequest.getFromId() != null) {
            Status fromStatus = statusService.getFullStatusDetails(updateTransitionRequest.getFromId());
            transition.setFrom(fromStatus);
        }

        if(updateTransitionRequest.getToId() != null) {
            Status toStatus = statusService.getFullStatusDetails(updateTransitionRequest.getToId());
            transition.setTo(toStatus);
        }

        if(updateTransitionRequest.getName() != null) {
            transition.setName(updateTransitionRequest.getName());
        }

        Transition updatedTransition = transitionRepository.save(transition);

        logger.debug("Updated transition ID: {}", maskingString.maskSensitive(updatedTransition.getId().toString()));

        return TransitionMapper.toTransitionResponse(updatedTransition);
    }

    public TransitionResponse deleteTransitionById(UUID id) {
        logger.info("Deleting transition for the given ID: {}", maskingString.maskSensitive(id.toString()));

        Transition transition = getTransitionById(id);

        transitionRepository.delete(transition);

        logger.debug("Deleted transition for the given ID: {}", maskingString.maskSensitive(id.toString()));

        return TransitionMapper.toTransitionResponse(transition);
    }


}
