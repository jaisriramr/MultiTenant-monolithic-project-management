package com.projectmanagement.multitenantprojectmanagement.organizations;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrganizationsRepository extends JpaRepository<Organizations, UUID> {

    Optional<Organizations> findByAuth0Id(String auth0Id);

    Optional<Organizations> findByDomain(String domain);

    Optional<Organizations> findByName(String name);
}
