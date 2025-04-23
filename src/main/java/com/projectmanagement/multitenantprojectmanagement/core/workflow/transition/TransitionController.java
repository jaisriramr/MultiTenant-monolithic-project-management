package com.projectmanagement.multitenantprojectmanagement.core.workflow.transition;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.projectmanagement.multitenantprojectmanagement.core.workflow.transition.dto.request.CreateTransitionRequest;
import com.projectmanagement.multitenantprojectmanagement.core.workflow.transition.dto.request.UpdateTransitionRequest;
import com.projectmanagement.multitenantprojectmanagement.core.workflow.transition.dto.response.TransitionResponse;
import com.projectmanagement.multitenantprojectmanagement.core.workflow.transition.mapper.TransitionMapper;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PutMapping;


@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class TransitionController {

    private final TransitionService transitionService;

    @GetMapping("/v1/transition/{id}")
    public ResponseEntity<TransitionResponse> getTransitionById(@PathVariable UUID id) {
        Transition transition = transitionService.getTransitionById(id);

        return ResponseEntity.ok(TransitionMapper.toTransitionResponse(transition));
    }

    @GetMapping("/v1/transition/{id}/workflow")
    public ResponseEntity<List<Transition>> getAllTransitionsByWorkflowId(@PathVariable UUID id) {
        List<Transition> transitions = transitionService.getAllTransitionsByWorkflowId(id);

        return ResponseEntity.ok(transitions);
    }

    @PostMapping("/v1/transition")
    public ResponseEntity<TransitionResponse> createTransition(@RequestBody CreateTransitionRequest createTransitionRequest) {
        TransitionResponse transition = transitionService.createTransition(createTransitionRequest);

        return ResponseEntity.status(HttpStatus.CREATED).body(transition);
    }

    @PutMapping("/v1/transition")
    public ResponseEntity<TransitionResponse> updateTransition(@RequestBody UpdateTransitionRequest updateTransitionRequest) {
        TransitionResponse transition = transitionService.updateTransition(updateTransitionRequest);

        return ResponseEntity.ok(transition);
    }

    @DeleteMapping("/v1/transition/{id}")
    public ResponseEntity<TransitionResponse> deleteTransition(@PathVariable UUID id) {
        TransitionResponse transition = transitionService.deleteTransitionById(id);

        return ResponseEntity.ok(transition);
    }

}
