package org.example.core.advertising.persistence.repository;


import org.example.core.advertising.persistence.entity.TypeEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface TypeRepository extends CrudRepository<TypeEntity,Integer> {

    Optional<TypeEntity> findByName(String name);

    List<TypeEntity> findDistinctByBrandEntity_BrandName(String brandName);
}
