package com.projectmanagement.multitenantprojectmanagement.permissions;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PermissionsRepository extends JpaRepository<Permissions, UUID> {
    List<Permissions> findByNameIn(List<String> names);

    List<Permissions> findByModule(String module);
}
