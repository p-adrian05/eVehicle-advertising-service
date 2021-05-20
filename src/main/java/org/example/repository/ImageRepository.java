package org.example.repository;


import org.example.model.Image;
import org.example.repository.entity.ImageEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.Collection;
import java.util.List;

public interface ImageRepository extends CrudRepository<ImageEntity,Integer> {

    List<ImageEntity> findImageEntitiesByPathIsIn(List<String> paths);

    boolean existsByPath(String path);

    ImageEntity findByPath(String path);


}
