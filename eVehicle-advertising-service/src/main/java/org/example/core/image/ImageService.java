package org.example.core.image;

import org.example.core.image.persistence.entity.ImageEntity;

import java.util.List;

public interface ImageService {

    void createImage(String path);

    void deleteImage(String path);

    List<ImageEntity> createImageEntities(List<String> paths);
}
