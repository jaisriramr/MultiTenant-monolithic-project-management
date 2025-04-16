package com.projectmanagement.multitenantprojectmanagement.organizations;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.projectmanagement.multitenantprojectmanagement.auth0.Auth0Service;
import com.projectmanagement.multitenantprojectmanagement.organizations.dto.request.CreateOrganizationRequest;
import com.projectmanagement.multitenantprojectmanagement.organizations.dto.request.UpdateOrganizationRequest;
import com.projectmanagement.multitenantprojectmanagement.organizations.dto.response.OrganizationResponse;
import com.projectmanagement.multitenantprojectmanagement.organizations.dto.response.OrganizationsResponse;
import com.projectmanagement.multitenantprojectmanagement.organizations.mapper.OrganizationMapper;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrganizationsService {

    private final OrganizationsRepository organizationsRepository;
    private final Auth0Service auth0Service;
    
    public OrganizationResponse getOrganizationById(UUID id) {
            Organizations organization = organizationsRepository.findById(id).orElseThrow(() -> new RuntimeException("Organization Not Found"));

            return OrganizationMapper.toOrganizationResponse(organization);
    }

    public List<OrganizationsResponse> getOrganizations() {
        try {
            List<Organizations> organizations = organizationsRepository.findAll();

            return OrganizationMapper.toOrganizationsResponse(organizations);
        }catch(Exception e) {
            throw new RuntimeException("Error while trying to fetch all organizations");
        }
    }

    @Transactional
    public Organizations createAnOrganization(CreateOrganizationRequest createOrganizationRequest) {
        try {
            ResponseEntity<Map<String, Object>> auth0Response =  auth0Service.createAnOrganization(createOrganizationRequest.getName(), createOrganizationRequest.getDisplayName());

            Map<String, Object> body = auth0Response.getBody();

            if(body != null) {
                String id = (String) body.get("id");

                Organizations organization = OrganizationMapper.toEntityOrganization(createOrganizationRequest, id);

                Organizations savedOrganization = organizationsRepository.save(organization);

                return savedOrganization;
            }else {
                throw new RuntimeException("Error while trying to create an organization");    
            }

        }catch(Exception e) {
            throw new RuntimeException("Error while trying to create an organization", e);
        }
    }

    @Transactional
    public OrganizationResponse updateAnOrganiation(UpdateOrganizationRequest updateOrganizationRequest) {
        try {
            Organizations organizations = organizationsRepository.findById(updateOrganizationRequest.getId()).orElseThrow(() -> new NotFoundException());
            if(updateOrganizationRequest.getName() != null) {
                String displayName = updateOrganizationRequest.getDisplayName() != null ? updateOrganizationRequest.getDisplayName() : updateOrganizationRequest.getName();
                auth0Service.updateAnOrganization(organizations.getAuth0Id(), updateOrganizationRequest.getName(),  displayName);
            }
            
            if(updateOrganizationRequest.getName() != null) {
                organizations.setName(updateOrganizationRequest.getName());
            }
            if(updateOrganizationRequest.getDisplayName() != null) {
                organizations.setDisplayName(updateOrganizationRequest.getDisplayName());
            }

            Organizations updatedOrganization = organizationsRepository.save(organizations);

            return OrganizationMapper.toOrganizationResponse(updatedOrganization);
        }catch(Exception e) {
            throw new RuntimeException("Error while trying to update an organization", e);
        }
    }

    @Transactional
    public String deleteOrganizationById(UUID id) {
        try {
            Organizations org = organizationsRepository.findById(id).orElseThrow(() -> new NotFoundException());
            auth0Service.deleteAnOrganization(org.getAuth0Id());

            organizationsRepository.deleteById(id);
            
            return "Organization with Id " + id + " is removed successfully!";
        }catch(Exception e) {
            throw new RuntimeException("Error while trying to delete an organization", e);
        }
    }



}
