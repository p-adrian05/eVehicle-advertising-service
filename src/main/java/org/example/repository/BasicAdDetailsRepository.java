package org.example.repository;

import org.example.repository.entity.AdDetailsEntity;
import org.example.repository.entity.BasicAdDetailsEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface BasicAdDetailsRepository extends CrudRepository<BasicAdDetailsEntity,Integer> {

}
