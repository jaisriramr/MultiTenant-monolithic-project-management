package com.projectmanagement.multitenantprojectmanagement.core.label;

import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.projectmanagement.multitenantprojectmanagement.core.label.dto.request.CreateLabelRequest;
import com.projectmanagement.multitenantprojectmanagement.core.label.dto.response.LabelResponse;
import com.projectmanagement.multitenantprojectmanagement.core.label.mapper.LabelMapper;
import com.projectmanagement.multitenantprojectmanagement.organizations.dto.response.PaginatedResponseDto;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class LabelController {

    private final LabelService labelService;

    @PreAuthorize("@organizationMembersService.hasPermission(#jwt.subject, {\"view:task\"}, #jwt.claims[\"org_id\"])")
    @GetMapping("/v1/label/{id}")
    public ResponseEntity<LabelResponse> getLabelById(@PathVariable UUID id, @AuthenticationPrincipal Jwt jwt) {
        LabelResponse labelResponse = labelService.getLabelById(id);

        return ResponseEntity.ok(labelResponse);
    }

    @PreAuthorize("@organizationMembersService.hasPermission(#jwt.subject, {\"view:task\"}, #jwt.claims[\"org_id\"])")
    @GetMapping("/v1/label/{id}/project")
    public ResponseEntity<PaginatedResponseDto<LabelResponse>> getLabelsByProjectId(@PathVariable UUID id, Pageable pageable,@AuthenticationPrincipal Jwt jwt) {
        PaginatedResponseDto<LabelResponse> response = labelService.getLabelsByProjectId(id, pageable);

        return ResponseEntity.ok(response);
    }

    @PreAuthorize("@organizationMembersService.hasPermission(#jwt.subject, {\"create:task\"}, #jwt.claims[\"org_id\"])")
    @PostMapping("/v1/label")
    public ResponseEntity<LabelResponse> createLabel(@RequestBody CreateLabelRequest createLabelRequest,@AuthenticationPrincipal Jwt jwt) {
        Label label = labelService.createLabel(createLabelRequest);

        return ResponseEntity.ok(LabelMapper.toLabelResponse(label));
    }

    @PreAuthorize("@organizationMembersService.hasPermission(#jwt.subject, {\"delete:task\"}, #jwt.claims[\"org_id\"])")
    @DeleteMapping("/v1/label/{id}")
    public ResponseEntity<LabelResponse> deleteLabelById(@PathVariable UUID id,@AuthenticationPrincipal Jwt jwt) {
        LabelResponse label = labelService.deleteLabelById(id);

        return ResponseEntity.ok(label);
    }

}
