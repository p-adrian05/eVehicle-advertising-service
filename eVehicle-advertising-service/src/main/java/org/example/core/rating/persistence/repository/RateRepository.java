package org.example.core.rating.persistence.repository;


import org.example.core.rating.persistence.entity.RateEntity;
import org.springframework.data.repository.CrudRepository;

public interface RateRepository extends CrudRepository<RateEntity,Integer> {
}
