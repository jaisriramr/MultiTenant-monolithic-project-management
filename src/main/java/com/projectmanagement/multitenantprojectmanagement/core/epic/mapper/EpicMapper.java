package com.projectmanagement.multitenantprojectmanagement.core.epic.mapper;

import com.projectmanagement.multitenantprojectmanagement.core.epic.Epic;
import com.projectmanagement.multitenantprojectmanagement.core.epic.dto.request.CreateEpicRequest;
import com.projectmanagement.multitenantprojectmanagement.core.epic.dto.response.EpicResponse;
import com.projectmanagement.multitenantprojectmanagement.core.project.Projects;
import com.projectmanagement.multitenantprojectmanagement.organizations.Organizations;

public class EpicMapper {

    public static Epic toEpicEntity(CreateEpicRequest createEpicRequest, Projects project, Organizations organization) {

        Epic epic = new Epic();
        epic.setName(createEpicRequest.getName());
        epic.setDescription(createEpicRequest.getDescription());
        epic.setColor(createEpicRequest.getColor());
        epic.setProject(project);
        epic.setOrganization(organization);

        return epic;

    }

    public static EpicResponse toEpicResponse(Epic epic) {
        return EpicResponse.builder().build();
    }

}
