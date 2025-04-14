package com.projectmanagement.multitenantprojectmanagement.organizations.mapper;

import java.util.ArrayList;
import java.util.List;

import com.projectmanagement.multitenantprojectmanagement.organizations.Organizations;
import com.projectmanagement.multitenantprojectmanagement.organizations.dto.request.CreateOrganizationRequest;
import com.projectmanagement.multitenantprojectmanagement.organizations.dto.response.OrganizationResponse;
import com.projectmanagement.multitenantprojectmanagement.organizations.dto.response.OrganizationsResponse;

public class OrganizationMapper {

    public static OrganizationResponse toOrganizationResponse(Organizations organizations) {
        return OrganizationResponse.builder()
                .id(organizations.getId())
                .name(organizations.getName())
                .displayName(organizations.getDisplayName())
                .domain(organizations.getDomain())
                .auth0Id(organizations.getAuth0Id())
                .build();
    }

    public static List<OrganizationsResponse> toOrganizationsResponse(List<Organizations> organizations) {
        List<OrganizationsResponse> response = new ArrayList<>();

        for(Organizations organization: organizations){
            OrganizationsResponse organizationResponse = OrganizationsResponse.builder()
                                                        .id(organization.getId())
                                                        .name(organization.getName())
                                                        .auth0Id(organization.getAuth0Id())
                                                        .build();

            response.add(organizationResponse);
        }

        return response;
    }

    public static Organizations toEntityOrganization(CreateOrganizationRequest createOrganizationRequest, String auth0Id) {
        Organizations organization = new Organizations();
        organization.setAuth0Id(auth0Id);
        organization.setName(createOrganizationRequest.getName());
        organization.setDisplayName(createOrganizationRequest.getDisplayName());
        organization.setDomain(createOrganizationRequest.getDomain());

        return organization;
    }

}
