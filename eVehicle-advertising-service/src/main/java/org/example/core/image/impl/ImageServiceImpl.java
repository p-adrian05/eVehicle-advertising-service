package org.example.core.image.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.core.image.ImageService;
import org.example.core.image.persistence.entity.ImageEntity;
import org.example.core.image.persistence.persistence.ImageRepository;
import org.example.core.user.persistence.entity.UserEntity;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.nio.file.Path;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

@RequiredArgsConstructor
@Slf4j
@Service
public class ImageServiceImpl implements ImageService {

    private final ImageRepository imageRepository;
    @Value("${user.default-image_id:1}")
    private String DEFAULT_IMAGE_ID;

    @Override
    @Transactional
    public void createImage(String path) {
        Objects.requireNonNull(path, "Path cannot be null for creating Image process");
        if (!imageRepository.existsByPath(path)) {
            imageRepository.save(ImageEntity.builder()
                .path(path)
                .uploadedTime(new Timestamp(new Date().getTime()))
                .build());
        }
        throw new IllegalArgumentException("Image path already exits");
    }

    @Override
    @Transactional
    public Set<ImageEntity> createImageEntities(Set<Path> paths) {
        Objects.requireNonNull(paths, "Paths cannot be null for creating Image entities process");
        Set<ImageEntity> imageEntities = new HashSet<>();
        Optional<ImageEntity> imageEntity;

        for (Path path : paths) {
            path = path.subpath(1,path.getNameCount());
            imageEntity = imageRepository.findByPath(path.toString());
            if (imageEntity.isPresent()) {
                imageEntities.add(imageEntity.get());
            } else {
                imageEntities.add(imageRepository.save(ImageEntity.builder()
                    .path(path.toString())
                    .uploadedTime(new Timestamp(new Date().getTime()))
                    .build()
                ));
            }
        }
        return imageEntities;
    }

    @Override
    @Transactional
    public void deleteImage(String path) {
        Objects.requireNonNull(path, "Path cannot be null for deleting Image process");
        Optional<ImageEntity> imageEntity = imageRepository.findByPath(path);
        imageRepository.delete(imageEntity.orElseThrow(() -> new IllegalArgumentException("Image path not exits")));
    }
    @Override
    public ImageEntity queryDefaultProfileImageEntity() {
        Optional<ImageEntity> imageEntity = imageRepository.findById(Integer.valueOf(DEFAULT_IMAGE_ID));
        return imageEntity.orElse(null);
    }
}
