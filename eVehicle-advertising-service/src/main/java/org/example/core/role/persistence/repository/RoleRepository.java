package org.example.core.role.persistence.repository;


import org.example.core.role.model.Role;
import org.example.core.role.persistence.entity.RoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RoleRepository extends JpaRepository<RoleEntity,Integer> {

    Optional<RoleEntity> findRoleEntityByRoleName(Role roleName);

    boolean existsByRoleName(Role name);

}
