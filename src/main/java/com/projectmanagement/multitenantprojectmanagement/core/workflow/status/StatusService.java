package com.projectmanagement.multitenantprojectmanagement.core.workflow.status;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.projectmanagement.multitenantprojectmanagement.core.workflow.Workflow;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StatusService {

    private final StatusRepository statusRepository;

       public Status createStatus(Status status) {
        return statusRepository.save(status);
    }

    public void deleteStatus(UUID id) {
        statusRepository.deleteById(id);
    }

}
