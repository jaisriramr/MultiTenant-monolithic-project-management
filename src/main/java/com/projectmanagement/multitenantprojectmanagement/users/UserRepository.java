package com.projectmanagement.multitenantprojectmanagement.users;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<Users, UUID> {
    Optional<List<Users>> findAllByNameContainingIgnoreCase(String name);
}
