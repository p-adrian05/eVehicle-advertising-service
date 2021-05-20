package org.example.repository;

import org.example.repository.entity.RoleEntity;
import org.example.repository.entity.UserEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface RoleRepository extends CrudRepository<RoleEntity,Integer> {

    List<RoleEntity> findRoleEntitiesByUsersContains(UserEntity userEntity);

    Collection<RoleEntity> findRoleEntitiesByRoleNameIsIn(Collection<String> roleNames);

    Optional<RoleEntity> findRoleEntityByRoleName(String roleName);
}
