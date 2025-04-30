package com.projectmanagement.multitenantprojectmanagement.service;

import com.projectmanagement.multitenantprojectmanagement.core.issue.Issue;
import com.projectmanagement.multitenantprojectmanagement.core.issue.dto.response.ListIssuesResponse;
import com.projectmanagement.multitenantprojectmanagement.core.issuerelation.*;
import com.projectmanagement.multitenantprojectmanagement.core.issuerelation.enums.IssueRelationType;
import com.projectmanagement.multitenantprojectmanagement.exception.NotFoundException;
import com.projectmanagement.multitenantprojectmanagement.helper.MaskingString;
import com.projectmanagement.multitenantprojectmanagement.organizations.Organizations;
import com.projectmanagement.multitenantprojectmanagement.organizations.OrganizationsService;
import com.projectmanagement.multitenantprojectmanagement.organizations.dto.response.PaginatedResponseDto;
import com.projectmanagement.multitenantprojectmanagement.auth0.utils.JWTUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class IssueRelationTest {

    @Mock
    private IssueRelationRepository issueRelationRepository;

    @Mock
    private MaskingString maskingString;

    @Mock
    private JWTUtils jwtUtils;

    @Mock
    private OrganizationsService organizationsService;

    @InjectMocks
    private IssueRelationService issueRelationService;

    private UUID issueRelationId;
    private IssueRelation mockIssueRelation;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        issueRelationId = UUID.randomUUID();
        mockIssueRelation = new IssueRelation();
        mockIssueRelation.setId(issueRelationId);
    }

    @Test
    void testGetIssueRelationById_Success() {
        when(issueRelationRepository.findById(issueRelationId)).thenReturn(Optional.of(mockIssueRelation));
        when(maskingString.maskSensitive(anyString())).thenReturn("masked");

        IssueRelation result = issueRelationService.getIssueRelationById(issueRelationId);

        assertNotNull(result);
        assertEquals(issueRelationId, result.getId());
        verify(issueRelationRepository, times(1)).findById(issueRelationId);
    }

    @Test
    void testGetIssueRelationById_NotFound() {
        when(issueRelationRepository.findById(issueRelationId)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            issueRelationService.getIssueRelationById(issueRelationId);
        });

        assertEquals("Issue relation not found for the given ID: " + issueRelationId, exception.getMessage());
        verify(issueRelationRepository, times(1)).findById(issueRelationId);
    }

    @Test
    void testCreateIssueRelation_Success() {
        Issue parentIssue = new Issue();
        Issue childIssue = new Issue();
        Organizations organization = new Organizations();

        when(jwtUtils.getAuth0OrgId()).thenReturn("auth0|12345");
        when(organizationsService.getOrganizationByAuth0Id("auth0|12345")).thenReturn(organization);
        when(issueRelationRepository.save(any(IssueRelation.class))).thenReturn(mockIssueRelation);

        issueRelationService.CreateIssueRelation(parentIssue, childIssue, IssueRelationType.SUB_TASK);

        verify(issueRelationRepository, times(1)).save(any(IssueRelation.class));
    }

    @Test
    void testDeleteIssueRelationById_Success() {
        String auth0OrgId = "auth0|12345";

        when(jwtUtils.getAuth0OrgId()).thenReturn(auth0OrgId);
        when(issueRelationRepository.findByChildIssueIdAndOrganization_Auth0Id(issueRelationId, auth0OrgId))
                .thenReturn(Optional.of(mockIssueRelation));

        issueRelationService.deleteIssueRelationById(issueRelationId);

        verify(issueRelationRepository, times(1)).delete(mockIssueRelation);
    }

    @Test
    void testDeleteIssueRelationById_NotFound() {
        String auth0OrgId = "auth0|12345";

        when(jwtUtils.getAuth0OrgId()).thenReturn(auth0OrgId);
        when(issueRelationRepository.findByChildIssueIdAndOrganization_Auth0Id(issueRelationId, auth0OrgId))
                .thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            issueRelationService.deleteIssueRelationById(issueRelationId);
        });

        assertEquals("Issue relation not found for the given Child ID: " + issueRelationId, exception.getMessage());
        verify(issueRelationRepository, times(1)).findByChildIssueIdAndOrganization_Auth0Id(issueRelationId, auth0OrgId);
    }

    @Test
    void testFindChildWorksByParentId_Success() {
        UUID parentId = UUID.randomUUID();
        String auth0OrgId = "auth0|12345";
        Pageable pageable = Pageable.unpaged();
        Page<IssueRelation> issueRelations = new PageImpl<>(Collections.emptyList());

        when(jwtUtils.getAuth0OrgId()).thenReturn(auth0OrgId);
        when(issueRelationRepository.findAllByParentIssueIdAndTypeEqualsAndOrganization_Auth0Id(
                parentId, IssueRelationType.SUB_TASK, auth0OrgId, pageable)).thenReturn(issueRelations);

        PaginatedResponseDto<ListIssuesResponse> result = issueRelationService.findChildWorksByParentId(parentId, pageable);

        assertNotNull(result);
        assertEquals(0, result.getTotalElements());
        verify(issueRelationRepository, times(1))
                .findAllByParentIssueIdAndTypeEqualsAndOrganization_Auth0Id(parentId, IssueRelationType.SUB_TASK, auth0OrgId, pageable);
    }
}
