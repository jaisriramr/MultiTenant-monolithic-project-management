package com.projectmanagement.multitenantprojectmanagement.organizations;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.projectmanagement.multitenantprojectmanagement.organizations.dto.request.CreateOrganizationRequest;
import com.projectmanagement.multitenantprojectmanagement.organizations.dto.request.UpdateOrganizationRequest;
import com.projectmanagement.multitenantprojectmanagement.organizations.dto.response.OrganizationResponse;
import com.projectmanagement.multitenantprojectmanagement.organizations.dto.response.OrganizationsResponse;
import com.projectmanagement.multitenantprojectmanagement.organizations.mapper.OrganizationMapper;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class OrganizationsController {

    private final OrganizationsService organizationsService;

    @PreAuthorize("@organizationMembersService.hasPermission(#jwt.subject, {\"super:admin\"}, #jwt.claims[\"org_id\"])")
    @GetMapping("/v1/organizations")
    public ResponseEntity<List<OrganizationsResponse>> getAllOrganizations() {
        List<OrganizationsResponse> organizationList = organizationsService.getOrganizations();
        return ResponseEntity.ok(organizationList);
    }

    @PreAuthorize("@organizationMembersService.hasPermission(#jwt.subject, {\"view:organization\"}, #jwt.claims[\"org_id\"])")
    @GetMapping("/v1/organization/{id}")
    public ResponseEntity<OrganizationResponse> getOrganizationById(@PathVariable UUID id) {
        OrganizationResponse organization = organizationsService.getOrganizationById(id);
        return ResponseEntity.ok(organization);
    }

    @PostMapping("/v1/organization")
    public ResponseEntity<OrganizationResponse> createOrganization(@RequestBody CreateOrganizationRequest createOrganizationRequest) {
        Organizations organization = organizationsService.createAnOrganization(createOrganizationRequest);

        OrganizationResponse orgDto = OrganizationMapper.toOrganizationResponse(organization);

        return ResponseEntity.status(HttpStatus.CREATED).body(orgDto);
    }

    @PreAuthorize("@organizationMembersService.hasPermission(#jwt.subject, {\"update:organization\"}, #jwt.claims[\"org_id\"])")
    @PutMapping("/v1/organization")
    public ResponseEntity<OrganizationResponse> updateOrganization(@RequestBody UpdateOrganizationRequest updateOrganizationRequest) {
        OrganizationResponse updatedOrganization = organizationsService.updateAnOrganiation(updateOrganizationRequest);

        return ResponseEntity.ok(updatedOrganization);
    }

    @PreAuthorize("@organizationMembersService.hasPermission(#jwt.subject, {\"delete:organization\"}, #jwt.claims[\"org_id\"])")
    @DeleteMapping("/v1/organization/{id}/by/{userId}")
    public ResponseEntity<String> deleteOrganizationById(@PathVariable UUID id, @PathVariable UUID userId) {
        String response = organizationsService.deleteOrganizationById(id, userId);
        return ResponseEntity.ok(response);
    }

}
