package com.projectmanagement.multitenantprojectmanagement.core.epic;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.projectmanagement.multitenantprojectmanagement.core.epic.dto.request.CreateEpicRequest;
import com.projectmanagement.multitenantprojectmanagement.core.epic.mapper.EpicMapper;
import com.projectmanagement.multitenantprojectmanagement.core.issue.IssueService;
import com.projectmanagement.multitenantprojectmanagement.core.project.ProjectService;
import com.projectmanagement.multitenantprojectmanagement.core.project.Projects;
import com.projectmanagement.multitenantprojectmanagement.exception.NotFoundException;
import com.projectmanagement.multitenantprojectmanagement.helper.MaskingString;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EpicService {

    private final EpicRepository epicRepository;
    private final ProjectService projectService;
    private final MaskingString maskingString;
    private static final Logger logger = LoggerFactory.getLogger(EpicService.class);

    public Epic getEpicEntity(UUID id) {
        logger.info("Getting epic for the given ID: {}", maskingString.maskSensitive(id.toString()));

        Epic epic = epicRepository.findById(id).orElseThrow(() -> new NotFoundException("Epic not found for the given ID: " + id));

        logger.debug("Fetched epic ID: {}", maskingString.maskSensitive(epic.getId().toString()));

        return epic;
    }

    @Transactional
    public Epic createEpicEntity(@Valid CreateEpicRequest createEpicRequest) {
        logger.info("Creating epic");

        Projects project = projectService.getProjectById(createEpicRequest.getProjectId());

        Epic epic = EpicMapper.toEpicEntity(createEpicRequest, project);

        Epic savedEpic = epicRepository.save(epic);

        logger.debug("Saved epic ID: {}" + maskingString.maskSensitive(savedEpic.getId().toString()));

        return savedEpic;
    }

}
