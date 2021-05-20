package org.example.repository;

import org.example.repository.entity.BrandEntity;
import org.springframework.data.repository.CrudRepository;

public interface BrandRepository extends CrudRepository<BrandEntity,String> {
}
