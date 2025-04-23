package com.projectmanagement.multitenantprojectmanagement.core.projectMember;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProjectMemberRepository extends JpaRepository<ProjectMember, UUID> {

    Optional<ProjectMember> findByUser_Auth0Id(String auth0Id);

    Optional<ProjectMember> findByUserId(UUID id);

    Page<ProjectMember> findAllByProjectId(UUID projectId, Pageable pageable);

}
