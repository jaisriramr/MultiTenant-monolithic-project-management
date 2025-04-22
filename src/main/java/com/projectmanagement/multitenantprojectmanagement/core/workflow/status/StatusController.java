package com.projectmanagement.multitenantprojectmanagement.core.workflow.status;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.projectmanagement.multitenantprojectmanagement.core.workflow.status.dto.request.CreateStatusRequest;
import com.projectmanagement.multitenantprojectmanagement.core.workflow.status.dto.request.UpdateStatusRequest;
import com.projectmanagement.multitenantprojectmanagement.core.workflow.status.dto.response.StatusResponse;
import com.projectmanagement.multitenantprojectmanagement.core.workflow.status.dto.response.StatusesResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class StatusController {

    private final StatusService statusService;

    @GetMapping("/v1/status/{id}")
    public ResponseEntity<StatusResponse> getStatusById(@PathVariable UUID id) {
        StatusResponse status = statusService.getStatusById(id);

        return ResponseEntity.ok(status);
    }

    @GetMapping("/v1/status/{id}/project")
    public ResponseEntity<List<StatusesResponse>> getAllStatusByProjectId(@PathVariable UUID id) {
        List<StatusesResponse> statuses = statusService.getAllStatusByProjectId(id);
        
        return ResponseEntity.ok(statuses);
    }

    @PostMapping("/v1/status")
    public ResponseEntity<StatusResponse> createStatus(@Valid @RequestBody CreateStatusRequest createStatusRequest) { 
        StatusResponse status = statusService.createStatus(createStatusRequest);
        
        return ResponseEntity.ok(status);
    }

    @PutMapping("/v1/status")
    public ResponseEntity<StatusResponse> updateStatus(@Valid @RequestBody UpdateStatusRequest updateStatusRequest) {
        StatusResponse status = statusService.updateStatus(updateStatusRequest);

        return ResponseEntity.ok(status);
    }

    @DeleteMapping("/v1/status/{id}")
    public ResponseEntity<StatusResponse> deleteStatusById(@PathVariable UUID id) {
        StatusResponse status = statusService.deletedStatusById(id);

        return ResponseEntity.ok(status);
    }

}
