package com.projectmanagement.multitenantprojectmanagement.core.project;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.projectmanagement.multitenantprojectmanagement.core.workflow.workflowscheme.WorkflowSchemeService;
import com.projectmanagement.multitenantprojectmanagement.organizationmembers.OrganizationMembersService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final OrganizationMembersService organizationMembersService;

    
    
}
