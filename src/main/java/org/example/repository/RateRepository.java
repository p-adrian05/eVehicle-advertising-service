package org.example.repository;


import org.example.repository.entity.RateEntity;
import org.springframework.data.repository.CrudRepository;

public interface RateRepository extends CrudRepository<RateEntity,Integer> {
}
