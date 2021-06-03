package org.example.core.image.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.example.core.image.AdImageService;
import org.example.core.image.ImageService;
import org.example.core.image.persistence.entity.ImageEntity;
import org.example.core.storage.AdImageStorageService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class AdImageServiceImpl implements AdImageService {

    private final ImageService imageService;
    private final AdImageStorageService adImageStorageService;

    @Override
    public Set<ImageEntity> store(MultipartFile[] imageFiles) throws FileUploadException {
        Set<Path> imagePaths = adImageStorageService.store(imageFiles);
        return imageService.createImageEntities(imagePaths);
    }

    @Override
    public Set<ImageEntity> updateAndStore(Set<ImageEntity> currentImages, MultipartFile[] imageFiles)
        throws FileUploadException {
        Set<String> imageFileNames =
            Arrays.stream(imageFiles).map(MultipartFile::getOriginalFilename).collect(Collectors.toSet());
        Set<String> originalImagesPaths = currentImages.stream().map(
            ImageEntity::getPath).collect(Collectors.toSet());

        Set<ImageEntity> toDeleteImages =
            currentImages.stream().filter(imageEntity -> !imageFileNames.contains(imageEntity.getPath()))
                .collect(Collectors.toSet());

        MultipartFile[] newFiles = Arrays.stream(imageFiles)
            .filter(imageFile -> !originalImagesPaths.contains(imageFile.getOriginalFilename()))
            .toArray(MultipartFile[]::new);

        Set<ImageEntity> notToRemoveImageEntities =
            currentImages.stream().filter(imageEntity -> imageFileNames.contains(imageEntity.getPath()))
                .collect(Collectors.toSet());
        notToRemoveImageEntities.addAll(store(newFiles));
        deleteImageFiles(toDeleteImages.stream().map(ImageEntity::getPath).collect(Collectors.toList()));
        return notToRemoveImageEntities;
    }

    private void deleteImageFiles(Collection<String> toDeletedImageModels) {
        toDeletedImageModels.forEach(adImageStorageService::deleteImageByPath);
    }
}
