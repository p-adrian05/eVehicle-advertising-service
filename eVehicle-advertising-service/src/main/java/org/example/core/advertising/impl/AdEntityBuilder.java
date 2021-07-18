package org.example.core.advertising.impl;

import lombok.RequiredArgsConstructor;
import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.example.core.advertising.exception.UnknownAdvertisementException;
import org.example.core.advertising.exception.UnknownCategoryException;
import org.example.core.advertising.model.AdDetailsDto;
import org.example.core.advertising.model.CreateAdDto;
import org.example.core.advertising.model.UpdateAdvertisementDto;
import org.example.core.advertising.persistence.AdState;
import org.example.core.advertising.persistence.entity.AdvertisementEntity;
import org.example.core.advertising.persistence.entity.CategoryEntity;
import org.example.core.advertising.persistence.entity.TypeEntity;
import org.example.core.advertising.persistence.repository.AdvertisementRepository;
import org.example.core.image.AdImageService;
import org.example.core.user.exception.UnknownUserException;
import org.example.core.user.persistence.entity.UserEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.sql.Timestamp;
import java.util.Currency;
import java.util.Date;
import java.util.Objects;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class AdEntityBuilder {

    private final AdUtil adUtil;
    private final AdvertisementRepository advertisementRepository;
    private final AdImageService adImageService;

    public AdvertisementEntity createNewAdvertisement(CreateAdDto advertisementDto,MultipartFile[] imageFiles)
        throws UnknownCategoryException, UnknownUserException, FileUploadException {
        Objects.requireNonNull(advertisementDto, "CreateAdDto cannot be null during creation");
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
            .price(advertisementDto.getPrice())
            .images(adImageService.store(imageFiles))
            .currency(Currency.getInstance(advertisementDto.getCurrency()).getCurrencyCode())
            .build();
    }

    public AdvertisementEntity createUpdatedAdvertisement(UpdateAdvertisementDto advertisementDto,
                                               MultipartFile[] imageFiles)
        throws UnknownAdvertisementException, UnknownCategoryException, FileUploadException {
        Objects.requireNonNull(advertisementDto, "AdvertisementDto cannot be null during update");
        Objects.requireNonNull(imageFiles, "ImageFile array cannot be null");
        AdvertisementEntity advertisementEntity = queryAdvertisementEntity(advertisementDto.getId());
        CategoryEntity categoryEntity = adUtil.queryCategory(advertisementDto.getCategory());
        TypeEntity typeEntity = adUtil.getTypeEntity(advertisementDto.getType());
        typeEntity.setBrandEntity(adUtil.getBrandEntity(advertisementDto.getBrand()));
        advertisementEntity.setCategory(categoryEntity);
        advertisementEntity.setType(typeEntity);
        advertisementEntity.setProductCondition(advertisementDto.getCondition());
        advertisementEntity.setTitle(advertisementDto.getTitle());
        advertisementEntity.setPrice(advertisementDto.getPrice());
        advertisementEntity.setCurrency(Currency.getInstance(advertisementDto.getCurrency()).getCurrencyCode());
        advertisementEntity.setImages(adImageService.updateAndStore(advertisementEntity.getImages(), imageFiles));
       return advertisementEntity;
    }

    public AdvertisementEntity queryAdvertisementEntity(int id) throws UnknownAdvertisementException {
        Optional<AdvertisementEntity> advertisementEntity = advertisementRepository.findById(id);
        if (advertisementEntity.isEmpty()) {
            throw new UnknownAdvertisementException(String.format("Advertisement not found by id: %s", id));
        }
        return advertisementEntity.get();
    }
}
