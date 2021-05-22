package org.example.core.image.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.core.image.ImageService;
import org.example.core.image.persistence.entity.ImageEntity;
import org.example.core.image.persistence.persistence.ImageRepository;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.sql.Timestamp;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Slf4j
@Service
public class ImageServiceImpl implements ImageService {

    private final ImageRepository imageRepository;

    @Override
    @Transactional
    public void createImage(String path) {
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
    public List<ImageEntity> createImageEntities(List<String> paths) {
        List<ImageEntity> imageEntities = new LinkedList<>();
        Optional<ImageEntity> imageEntity;
        for (String path : paths) {
            imageEntity = imageRepository.findByPath(path);
            if (imageEntity.isPresent()) {
                imageEntities.add(imageEntity.get());
            } else {
                imageEntities.add(imageRepository.save(ImageEntity.builder()
                    .path(path)
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
        Optional<ImageEntity> imageEntity = imageRepository.findByPath(path);
        imageRepository.delete(imageEntity.orElseThrow(() -> new IllegalArgumentException("Image path not exits")));
    }
}
