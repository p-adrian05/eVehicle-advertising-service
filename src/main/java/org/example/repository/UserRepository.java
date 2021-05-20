package org.example.repository;


import org.example.repository.entity.AdvertisementEntity;
import org.example.repository.entity.UserEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.Optional;


public interface UserRepository extends CrudRepository<UserEntity,Integer> {

    Optional<UserEntity> findByUsername(String username);
    @Query("SELECT u from UserEntity u left join fetch u.savedAds where u.username =:username")
    Optional<UserEntity> findUserEntityByUsernameWithSavedAds(@Param("username") String username);
    @Query("SELECT u from UserEntity u left join fetch u.roles where u.username =:username")
    Optional<UserEntity> findUserEntityByUsernameWithRoles(@Param("username") String username);
    @Query("SELECT u from UserEntity u left join fetch u.profileImage where u.username =:username")
    Optional<UserEntity> findUserEntityByUsernameWithImage(@Param("username") String username);
    boolean existsUserEntityByUsername(String username);
    boolean existsUserEntityByEmail(String email);

    Optional<UserEntity> findByActivation(String activation);

    @Query("Select u.id from UserEntity u where u.username =:username")
    Optional<Integer> getIdByUsername(@Param("username")String username);
    @Query("Select u.username from UserEntity u where u.id =:id")
    Optional<String> getUsernameById(@Param("id")int id);
}
