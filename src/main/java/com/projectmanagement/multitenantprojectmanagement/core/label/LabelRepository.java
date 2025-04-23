package com.projectmanagement.multitenantprojectmanagement.core.label;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LabelRepository extends JpaRepository<Label, UUID> {
    Page<Label> findAllByProjectId(UUID projectId, Pageable pageable);
}
