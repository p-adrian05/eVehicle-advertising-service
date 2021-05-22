package org.example.core.advertising.persistence.repository;


import org.example.core.advertising.persistence.entity.CategoryEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface CategoryRepository extends CrudRepository<CategoryEntity,Integer> {

    Optional<CategoryEntity> findByName(String name);
}
