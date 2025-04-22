package com.projectmanagement.multitenantprojectmanagement.core.workflow.transition;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.projectmanagement.multitenantprojectmanagement.core.workflow.Workflow;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TransitionService {

    private final TransitionRepository transitionRepository;

        public Transition createTransition(Transition transition) {
        return transitionRepository.save(transition);
    }

    public List<Transition> getTransitionsByWorkflow(Workflow workflow) {
        return transitionRepository.findByWorkflow(workflow);
    }

    public void deleteTransition(UUID id) {
        transitionRepository.deleteById(id);
    }
}
