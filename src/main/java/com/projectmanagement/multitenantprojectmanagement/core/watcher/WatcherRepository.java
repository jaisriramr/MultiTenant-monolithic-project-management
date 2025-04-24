package com.projectmanagement.multitenantprojectmanagement.core.watcher;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WatcherRepository extends JpaRepository<Watcher, UUID> {
    List<Watcher> findAllByIssueId(UUID issueId);
}
