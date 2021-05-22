package org.example.core.role.persistence.repository;


import org.example.core.role.persistence.entity.RoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<RoleEntity,Integer> {

    Optional<RoleEntity> findRoleEntityByRoleName(String roleName);

    boolean existsByRoleName(String name);
}
