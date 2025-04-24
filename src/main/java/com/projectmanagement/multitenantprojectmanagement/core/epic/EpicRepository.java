package com.projectmanagement.multitenantprojectmanagement.core.epic;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EpicRepository extends JpaRepository<Epic, UUID> {

    Page<Epic> findAllByProjectId(UUID projectId, Pageable pageable);

}
