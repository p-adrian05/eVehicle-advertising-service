package org.example.core.advertising.persistence.repository;

import org.example.core.advertising.persistence.entity.BrandEntity;
import org.springframework.data.repository.CrudRepository;

public interface BrandRepository extends CrudRepository<BrandEntity,String> {
}
