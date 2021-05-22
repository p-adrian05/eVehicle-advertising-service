package org.example.core.user.persistence.repository;



import org.example.core.user.persistence.entity.UserEntity;
import org.springframework.data.jpa.repository.Query;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.Optional;


public interface UserRepository extends CrudRepository<UserEntity,Integer> {

    Optional<UserEntity> findByUsername(String username);

    @Query("SELECT u from UserEntity u left join fetch u.profileImage left join fetch u.roles where u.username =:username")
    Optional<UserEntity> findUserWithRolesAndImage(@Param("username") String username);

    boolean existsUserEntityByUsername(String username);

    boolean existsUserEntityByEmail(String email);

    Optional<UserEntity> findByActivation(String activation);
}
