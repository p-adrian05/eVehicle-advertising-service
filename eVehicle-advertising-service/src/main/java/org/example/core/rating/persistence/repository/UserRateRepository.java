
package org.example.core.rating.persistence.repository;


import org.example.core.rating.persistence.entity.RateState;
import org.example.core.rating.persistence.entity.UserRateEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRateRepository extends CrudRepository<UserRateEntity,Integer> {

    @Query(value = "SELECT r FROM UserRateEntity r join fetch r.advertisement JOIN FETCH r.rate join fetch r.ratingUser join fetch r.ratedUser " +
            "where (:#{#params.advertisementId} is null or r.advertisement.id=:#{#params.advertisementId})  " +
            "and (:#{#params.ratedState} is null or r.state=:#{#params.ratedState})  " +
            "and (:#{#params.rateStatus} is null or r.status=:#{#params.rateStatus})  " +
            "and (:#{#params.ratedUsername} is null or r.ratedUser.username=:#{#params.ratedUsername})  " +
            "and (:#{#params.ratingUsername} is null or r.ratingUser.username=:#{#params.ratingUsername})  " +
            "order by r.rate.created desc",
            countQuery = "SELECT count(r) FROM UserRateEntity r  " +
                    "where (:#{#params.advertisementId} is null or r.advertisement.id=:#{#params.advertisementId})  " +
                    "and (:#{#params.ratedState} is null or r.state=:#{#params.ratedState})  " +
                    "and (:#{#params.rateStatus} is null or r.status=:#{#params.rateStatus})  " +
                    "and (:#{#params.ratedUsername} is null or r.ratedUser.username=:#{#params.ratedUsername})  " +
                    "and (:#{#params.ratingUsername} is null or r.ratingUser.username=:#{#params.ratingUsername})  ")
    Page<UserRateEntity> findByRatedUser_UsernameAndStateOrderByRate(@Param("params") RateQueryParams params, Pageable pageable);

    boolean existsByRatingUser_IdAndAdvertisement_Id(int ratingUserId, int advertisementId);

    int countByRatedUser_UsernameAndRate_State(String username, RateState rateState);

    @Query(value = "SELECT r FROM UserRateEntity r join fetch r.ratedUser join fetch r.ratingUser join fetch r.rate where r.activationCode =:code  and r.status='OPEN'")
    Optional<UserRateEntity> findUserRateEntityByActivationCode(@Param("code") String code);
}