package org.example.repository;

import org.example.repository.util.AdState;
import org.example.repository.util.AdvertisementQueryParams;
import org.example.repository.entity.AdvertisementEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface AdvertisementRepository extends CrudRepository<AdvertisementEntity,Integer> {

    @Query("SELECT ad FROM AdvertisementEntity ad JOIN FETCH ad.category left JOIN FETCH ad.images JOIN FETCH ad.type where ad.id=:id")
    Optional<AdvertisementEntity> findByIdWithCategoryAndType(@Param("id") int id);

    @Query("SELECT ad FROM AdvertisementEntity ad left join fetch ad.basicAdDetails adDetails left JOIN FETCH ad.type left join fetch ad.images " +
            "where (:#{#params.category} is null or ad.category.name =:#{#params.category}) " +
            "and ad.state in ('ACTIVE','FROZEN')" +
            "and (:#{#params.brand} is null or ad.type.brandEntity.brandName =:#{#params.brand})" +
            "and (:#{#params.type} is null or ad.type.name =:#{#params.type})"+
            "and (:#{#params.drive} is null or adDetails.drive =:#{#params.drive})"+
            "and (:#{#params.condition} is null or ad.condition =:#{#params.condition})"+
            "and (:#{#params.minYear} is null or adDetails.year>=:#{#params.minYear})"+
            "and (:#{#params.maxYear} is null or adDetails.year<=:#{#params.maxYear})"+
            "and (:#{#params.minKm} is null or adDetails.km>=:#{#params.minKm})"+
            "and (:#{#params.maxKm} is null or adDetails.km<=:#{#params.maxKm})"+
            "and (:#{#params.minPrice} is null or ad.price>=:#{#params.minPrice})"+
            "and (:#{#params.maxPrice} is null or ad.price<=:#{#params.maxPrice})"+
            "and (:#{#params.seatNumber} is null or adDetails.seatNumber=:#{#params.seatNumber})"+
            "and (:#{#params.minBatterySize} is null or adDetails.batterySize>=:#{#params.minBatterySize})"+
            "and (:#{#params.maxBatterySize} is null or adDetails.batterySize<=:#{#params.maxBatterySize})"+
            "and (:#{#params.minPerformance} is null or adDetails.performance>=:#{#params.minPerformance})"+
            "and (:#{#params.maxPerformance} is null or adDetails.performance<=:#{#params.maxPerformance})"+
            "and (:#{#params.minChargeSpeed} is null or adDetails.chargeSpeed>=:#{#params.minChargeSpeed})"+
            "and (:#{#params.maxChargeSpeed} is null or adDetails.chargeSpeed<=:#{#params.maxChargeSpeed})"
    )
    Slice<AdvertisementEntity> findByCategory(@Param("params") AdvertisementQueryParams params, Pageable pageable);

    @Query(value = "SELECT ad FROM AdvertisementEntity ad  JOIN FETCH ad.type left join fetch ad.images where ad.creator.id =:userId and ad.state=:state",
            countQuery="SELECT count(ad) from AdvertisementEntity ad where ad.creator.id =:userId and ad.state=:state")
    Page<AdvertisementEntity> findByCreator(@Param("userId") int userId, Pageable pageable, @Param("state")AdState state);

    @Query("SELECT ad FROM AdvertisementEntity ad JOIN FETCH ad.images where ad.id=:id")
    Optional<AdvertisementEntity> findByIdWithImages(@Param("id") int id);

    @Query("SELECT ad.type.brandEntity.brandName FROM AdvertisementEntity ad join ad.type t join ad.category c " +
            "where c.name =:cat group by ad.type.brandEntity.brandName")
    List<String> findBrandNamesByCategory(@Param("cat") String category);

    @Query("SELECT ad.type.name FROM AdvertisementEntity ad join ad.type t join ad.category c " +
            "where (:cat is null or c.name =:cat) and t.brandEntity.brandName =:brandName group by ad.type.name")
    List<String> findCarTypesByCategoryAndBrand(@Param("cat") String category,@Param("brandName") String brandName);

    boolean existsByIdAndAndCreator_Username(int id, String username);
}
