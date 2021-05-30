package org.example.core.advertising.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.example.core.advertising.AdDetailsService;
import org.example.core.advertising.AdvertisementService;
import org.example.core.advertising.exception.UnknownAdvertisementException;
import org.example.core.advertising.exception.UnknownCategoryException;
import org.example.core.advertising.model.AdDetailsDto;
import org.example.core.advertising.model.AdLabelDto;
import org.example.core.advertising.model.AdvertisementDto;
import org.example.core.advertising.model.CreateAdDto;
import org.example.core.advertising.model.UpdateAdvertisementDto;
import org.example.core.advertising.persistence.AdState;
import org.example.core.advertising.persistence.entity.AdvertisementEntity;
import org.example.core.advertising.persistence.entity.CategoryEntity;
import org.example.core.advertising.persistence.entity.TypeEntity;
import org.example.core.advertising.persistence.repository.AdvertisementQueryParams;
import org.example.core.advertising.persistence.repository.AdvertisementRepository;
import org.example.core.image.ImageService;
import org.example.core.image.persistence.entity.ImageEntity;
import org.example.core.security.AuthException;
import org.example.core.storage.AdImageStorageService;
import org.example.core.user.exception.UnknownUserException;
import org.example.core.user.persistence.entity.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.nio.file.Path;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Slf4j
public class AdvertisementServiceImpl implements AdvertisementService {


    private final AdvertisementRepository advertisementRepository;
    private final ImageService imageService;
    private final AdImageStorageService adImageStorageService;
    private final AdUtil adUtil;
    private final AdDetailsService adDetailsService;

    @Override
    @Transactional
    public void createAdvertisement(CreateAdDto advertisementDto, AdDetailsDto adDetailsDto,
                                    MultipartFile[] imageFiles)
        throws UnknownUserException, UnknownCategoryException, FileUploadException {

        AdvertisementEntity advertisementEntity = createNewAdvertisement(advertisementDto);
        Set<Path> imagePaths = adImageStorageService.store(imageFiles);
        advertisementEntity.setImages(
            imageService.createImageEntities(imagePaths));
        AdvertisementEntity newAdvertisementEntity = advertisementRepository.save(advertisementEntity);
        log.info("New advertisementEntity: {}", newAdvertisementEntity);

        adDetailsService.createAdDetails(adDetailsDto, newAdvertisementEntity);
    }

    private Set<ImageEntity> getUpdatedImages(Set<ImageEntity> currentImages, MultipartFile[] imageFiles)
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
        notToRemoveImageEntities.addAll(imageService.createImageEntities(adImageStorageService.store(newFiles)));
        deleteImageFiles(toDeleteImages.stream().map(ImageEntity::getPath).collect(Collectors.toList()));
        return notToRemoveImageEntities;
    }

    @Override
    @Transactional
    public void updateAdvertisementWithDetails(UpdateAdvertisementDto advertisementDto, AdDetailsDto adDetails,
                                                MultipartFile[] imageFiles)
        throws UnknownAdvertisementException, UnknownCategoryException, FileUploadException {

        if (!advertisementRepository.existsByIdAndAndCreator_Username(advertisementDto.getId(),
            SecurityContextHolder.getContext().getAuthentication().getName())) {
            throw new AuthException("Access denied");
        }

        AdvertisementEntity advertisementEntity = queryAdvertisementEntity(advertisementDto.getId());
        CategoryEntity categoryEntity = adUtil.queryCategory(advertisementDto.getCategory());
        TypeEntity typeEntity = adUtil.getTypeEntity(advertisementDto.getType());
        typeEntity.setBrandEntity(adUtil.getBrandEntity(advertisementDto.getBrand()));
        log.info("Type entity for updated advertisement: {}", typeEntity);
        advertisementEntity.setCategory(categoryEntity);
        advertisementEntity.setType(typeEntity);
        advertisementEntity.setProductCondition(advertisementDto.getCondition());
        advertisementEntity.setTitle(advertisementDto.getTitle());
        advertisementEntity.setPrice(advertisementDto.getPrice());
        adDetailsService.updateAdDetails(adDetails);
        advertisementEntity.setImages(getUpdatedImages(advertisementEntity.getImages(),imageFiles));
        advertisementRepository.save(advertisementEntity);
    }


    private void deleteImageFiles(Collection<String> toDeletedImageModels) {
        toDeletedImageModels.forEach(adImageStorageService::deleteImageByPath);
    }

    @Override
    public Optional<AdvertisementDto> getAdvertisementById(int id) {
        Optional<AdvertisementEntity> advertisementEntity = advertisementRepository.findByIdWithCategoryAndType(id);
        log.info("AdvertisementEntity entity by id: {},{}", id, advertisementEntity);
        if (advertisementEntity.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(adUtil.convertAdvertisementEntityToDto(advertisementEntity.get()));
    }

    @Override
    public Optional<AdDetailsDto> getAdDetailsById(int id) {
        return adDetailsService.getAdDetailsById(id);
    }


    @Override
    public Slice<AdLabelDto> getAdvertisements(AdvertisementQueryParams params, Pageable pageable) {
        return advertisementRepository.findByParams(params, pageable)
            .map(adUtil::convertAdvertisementEntityToLabelDto);
    }



    @Override
    public Page<AdLabelDto> getAdvertisementsByUsername(String username, Pageable pageable, AdState state) {
        return advertisementRepository.findByCreator(username, pageable, state)
            .map(adUtil::convertAdvertisementEntityToLabelDto);
    }

    @Override
    public void changeState(int adId, AdState stateToChange, String creatorName) throws UnknownAdvertisementException {
        if (!advertisementRepository.existsByIdAndAndCreator_Username(adId, creatorName)) {
            throw new AuthException("Access denied");
        }
        AdvertisementEntity advertisementEntity = queryAdvertisementEntity(adId);
        log.info(String
            .format("Changing advertisement id: %s  state from %s to %s", adId, advertisementEntity.getState(),
                stateToChange));
        advertisementEntity.setState(stateToChange);
        advertisementRepository.save(advertisementEntity);
    }


    private AdvertisementEntity queryAdvertisementEntity(int id) throws UnknownAdvertisementException {
        Optional<AdvertisementEntity> advertisementEntity = advertisementRepository.findById(id);
        if (advertisementEntity.isEmpty()) {
            throw new UnknownAdvertisementException(String.format("Advertisement not found by id: %s", id));
        }
        log.info("Queried advertisement : {}", advertisementEntity);
        return advertisementEntity.get();
    }

    private AdvertisementEntity createNewAdvertisement(CreateAdDto advertisementDto)
        throws UnknownCategoryException, UnknownUserException {

        UserEntity userEntity = adUtil.queryUserEntity(advertisementDto.getCreator());
        CategoryEntity categoryEntity = adUtil.queryCategory(advertisementDto.getCategory());
        TypeEntity typeEntity = adUtil.getTypeEntity(advertisementDto.getType());
        typeEntity.setBrandEntity(adUtil.getBrandEntity(advertisementDto.getBrand()));

        return AdvertisementEntity.builder()
            .creator(userEntity)
            .category(categoryEntity)
            .type(typeEntity)
            .title(advertisementDto.getTitle())
            .productCondition(advertisementDto.getCondition())
            .state(AdState.ACTIVE)
            .created(new Timestamp(new Date().getTime()))
            .price(advertisementDto.getPrice()).build();
    }
}
