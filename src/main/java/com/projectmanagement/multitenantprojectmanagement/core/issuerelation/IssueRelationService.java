package com.projectmanagement.multitenantprojectmanagement.core.issuerelation;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.projectmanagement.multitenantprojectmanagement.auth0.utils.JWTUtils;
import com.projectmanagement.multitenantprojectmanagement.core.issue.Issue;
import com.projectmanagement.multitenantprojectmanagement.core.issue.dto.response.ListIssuesResponse;
import com.projectmanagement.multitenantprojectmanagement.core.issuerelation.enums.IssueRelationType;
import com.projectmanagement.multitenantprojectmanagement.core.issuerelation.mapper.IssueRelationMapper;
import com.projectmanagement.multitenantprojectmanagement.exception.NotFoundException;
import com.projectmanagement.multitenantprojectmanagement.helper.MaskingString;
import com.projectmanagement.multitenantprojectmanagement.organizations.Organizations;
import com.projectmanagement.multitenantprojectmanagement.organizations.OrganizationsService;
import com.projectmanagement.multitenantprojectmanagement.organizations.dto.response.PaginatedResponseDto;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class IssueRelationService {
    
    private final IssueRelationRepository issueRelationRepository;
    private final MaskingString maskingString;
    private static final Logger logger = LoggerFactory.getLogger(IssueRelationService.class);
    private final JWTUtils jwtUtils;
    private final OrganizationsService organizationsService;
    
    public IssueRelation getIssueRelationById(UUID id) {
        logger.info("Getting issue relation by ID: {}", maskingString.maskSensitive(id.toString()));

        IssueRelation issueRelation = issueRelationRepository.findById(id).orElseThrow(() -> new NotFoundException("Issue relation not found for the given ID: " + id));

        logger.debug("Fetched issue relation ID: {}" , maskingString.maskSensitive(issueRelation.getId().toString()));

        return issueRelation;
    }

    @Transactional
    public void CreateIssueRelation(Issue parent,Issue child, IssueRelationType type) {
        logger.info("Creating issue relation");

        IssueRelation issueRelation = new IssueRelation();
        issueRelation.setParentIssue(parent);
        issueRelation.setChildIssue(child);
        
        issueRelation.setType(type);

        String auth0OrgId = jwtUtils.getAuth0OrgId();

        Organizations organization = organizationsService.getOrganizationByAuth0Id(auth0OrgId);

        issueRelation.setOrganization(organization);

        // if("SUB_TASK".equals(IssueRelationType.SUB_TASK.toString())) {
        //     issueRelation.setType(IssueRelationType.SUB_TASK);
        // }else if("LINKED".equals(IssueRelationType.LINKED.toString())) {
        //     issueRelation.setType(IssueRelationType.LINKED);
        // }else if("BLOCKS".equals(IssueRelationType.BLOCKS.toString())) {
        //     issueRelation.setType(IssueRelationType.BLOCKS);
        // }else if("DUPLICATES".equals(IssueRelationType.DUPLICATES.toString())) {
        //     issueRelation.setType(IssueRelationType.DUPLICATES);
        // }else if("DEPENDS_ON".equals(IssueRelationType.DEPENDS_ON.toString())) {
        //     issueRelation.setType(IssueRelationType.DEPENDS_ON);
        // }else {
        //     throw new IllegalArgumentException("The provided issue relation type is not allowed!");
        // }

        IssueRelation savedIssueRelation = issueRelationRepository.save(issueRelation);

        logger.debug("Saved issue relation ID: {}", maskingString.maskSensitive(savedIssueRelation.getId().toString()));
    }

    @Transactional
    public void deleteIssueRelationById(UUID id) {
        logger.info("Deleting issue relation");

        String auth0OrgId = jwtUtils.getAuth0OrgId();

        IssueRelation issueRelation = issueRelationRepository.findByChildIssueIdAndOrganization_Auth0Id(id, auth0OrgId).orElseThrow(() -> new NotFoundException("Issue relation not found for the given Child ID: " + id));

        issueRelationRepository.delete(issueRelation);

        logger.debug("Deleted issue relation for the given ID: {}", maskingString.maskSensitive(id.toString()));
    }

    public PaginatedResponseDto<ListIssuesResponse> findChildWorksByParentId(UUID id, Pageable pageable) {
        logger.info("Getting all child works for the given parent ID: {}", maskingString.maskSensitive(id.toString()));

        String auth0OrgId = jwtUtils.getAuth0OrgId();

        Page<IssueRelation> issueRelations = issueRelationRepository.findAllByParentIssueIdAndTypeEqualsAndOrganization_Auth0Id(id, IssueRelationType.SUB_TASK, auth0OrgId,pageable);

        logger.debug("Fetched {} issue relations", issueRelations.getTotalElements());

        return IssueRelationMapper.toPaginatedResponseDto(issueRelations);

    }

}
