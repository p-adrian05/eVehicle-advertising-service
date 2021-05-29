package org.example.core.image;

import org.example.core.image.persistence.entity.ImageEntity;

import java.nio.file.Path;
import java.util.List;
import java.util.Set;

public interface ImageService {

    void createImage(String path);

    void deleteImage(String path);

    Set<ImageEntity> createImageEntities(Set<Path> paths);

}
