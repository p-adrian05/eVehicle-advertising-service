package org.example.core.user.persistence.repository;

import org.example.core.user.persistence.entity.UserDataEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface UserDataRepository extends CrudRepository<UserDataEntity,Integer> {

    Optional<UserDataEntity> findUserDataEntityByUserEntityUsername(String username);
}
