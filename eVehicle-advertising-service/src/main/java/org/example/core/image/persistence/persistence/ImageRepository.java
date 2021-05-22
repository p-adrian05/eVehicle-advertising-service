package org.example.core.image.persistence.persistence;


import org.example.core.image.persistence.entity.ImageEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface ImageRepository extends CrudRepository<ImageEntity,Integer> {

    List<ImageEntity> findImageEntitiesByPathIsIn(List<String> paths);

    boolean existsByPath(String path);

    Optional<ImageEntity> findByPath(String path);

}
