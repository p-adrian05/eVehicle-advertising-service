package org.example.repository;

import org.example.repository.entity.CategoryEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface CategoryRepository extends CrudRepository<CategoryEntity,Integer> {

    Optional<CategoryEntity> findByName(String name);
}
