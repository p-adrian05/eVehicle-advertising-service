package org.example.core.advertising.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.core.advertising.exception.UnknownCategoryException;
import org.example.core.advertising.model.AdDetailsDto;
import org.example.core.advertising.model.AdLabelDto;
import org.example.core.advertising.model.AdvertisementDto;
import org.example.core.advertising.model.BasicAdDetails;
import org.example.core.advertising.persistence.entity.AdDetailsEntity;
import org.example.core.advertising.persistence.entity.AdvertisementEntity;
import org.example.core.advertising.persistence.entity.BasicAdDetailsEntity;
import org.example.core.advertising.persistence.entity.BrandEntity;
import org.example.core.advertising.persistence.entity.CategoryEntity;
import org.example.core.advertising.persistence.entity.TypeEntity;
import org.example.core.advertising.persistence.repository.BrandRepository;
import org.example.core.advertising.persistence.repository.CategoryRepository;
import org.example.core.advertising.persistence.repository.TypeRepository;
import org.example.core.image.model.ImageDto;
import org.example.core.image.persistence.entity.ImageEntity;
import org.example.core.user.exception.UnknownUserException;
import org.example.core.user.persistence.entity.UserEntity;
import org.example.core.user.persistence.repository.UserRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Component
@Slf4j
class AdUtil {

    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final TypeRepository typeRepository;
    private final BrandRepository brandRepository;

    UserEntity queryUserEntity(String username) throws UnknownUserException {
        Optional<UserEntity> userEntity = userRepository.findByUsername(username);
        if (userEntity.isEmpty()) {
            throw new UnknownUserException(String.format("User not found: %s", username));
        }
        log.info("Queried user : {}", userEntity.get());
        return userEntity.get();
    }

    CategoryEntity queryCategory(String categoryName) throws UnknownCategoryException {
        Optional<CategoryEntity> categoryEntity = categoryRepository.findByName(categoryName);
        if (categoryEntity.isEmpty()) {
            throw new UnknownCategoryException(String.format("Category not found: %s", categoryName));
        }
        log.info("Queried category : {}", categoryEntity.get());
        return categoryEntity.get();
    }

    TypeEntity getTypeEntity(String type) {
        Optional<TypeEntity> typeEntity = typeRepository.findByName(type);
        if (typeEntity.isPresent()) {
            log.info("Created typeEntity by name : {},{}", type, typeEntity.get());
            return typeEntity.get();
        }
        TypeEntity newTypeEntity = TypeEntity.builder().name(type).build();
        newTypeEntity = typeRepository.save(newTypeEntity);
        log.info("Created typeEntity by name : {},{}", type, newTypeEntity);
        return newTypeEntity;
    }

    BrandEntity getBrandEntity(String brand) {
        Optional<BrandEntity> brandEntity = brandRepository.findById(brand);
        if (brandEntity.isPresent()) {
            log.info("brandEntity by name : {},{}", brand, brandEntity.get());
            return brandEntity.get();
        }
        BrandEntity newBrandEntity = BrandEntity.builder().brandName(brand).build();
        newBrandEntity = brandRepository.save(newBrandEntity);
        log.info("Created brandEntity by name : {},{}", brand, newBrandEntity);
        return newBrandEntity;
    }

    AdDetailsEntity convertAdDetailsDtoToEntity(AdDetailsDto adDetailsDto) {
        return AdDetailsEntity.builder()
            .adId(adDetailsDto.getAdId())
            .maxSpeed(adDetailsDto.getMaxSpeed())
            .productRange(adDetailsDto.getRange())
            .weight(adDetailsDto.getWeight())
            .accelaration(adDetailsDto.getAccelaration())
            .color(adDetailsDto.getColor())
            .description(adDetailsDto.getDescription())
            .build();
    }

    BasicAdDetailsEntity convertAdDetailsDtoToBasicEntity(AdDetailsDto adDetailsDto) {
        return BasicAdDetailsEntity.builder()
            .adId(adDetailsDto.getAdId())
            .performance(adDetailsDto.getPerformance())
            .batterySize(adDetailsDto.getBatterySize())
            .km(adDetailsDto.getKm())
            .chargeSpeed(adDetailsDto.getChargeSpeed())
            .drive(adDetailsDto.getDrive())
            .seatNumber(adDetailsDto.getSeatNumber())
            .year(adDetailsDto.getYear())
            .build();
    }

    AdLabelDto convertAdvertisementEntityToLabelDto(AdvertisementEntity advertisementEntity) {
        return AdLabelDto.builder()
            .id(advertisementEntity.getId())
            .price(advertisementEntity.getPrice())
            .brand(advertisementEntity.getType().getBrandEntity().getBrandName())
            .condition(advertisementEntity.getProductCondition())
            .created(advertisementEntity.getCreated())
            .title(advertisementEntity.getTitle())
            .state(advertisementEntity.getState())
            .type(advertisementEntity.getType().getName())
            .imagePaths(advertisementEntity.getImages().stream()
                .map(ImageEntity::getPath).collect(Collectors.toList()))
            .basicAdDetails(convertBasicAdDetailsEntityToModel(advertisementEntity.getBasicAdDetails()))
            .build();
    }

    BasicAdDetails convertBasicAdDetailsEntityToModel(BasicAdDetailsEntity basicAdDetailsEntity) {
        return BasicAdDetails.builder()
            .adId(basicAdDetailsEntity.getAdId())
            .year(basicAdDetailsEntity.getYear())
            .batterySize(basicAdDetailsEntity.getBatterySize())
            .chargeSpeed(basicAdDetailsEntity.getChargeSpeed())
            .seatNumber(basicAdDetailsEntity.getSeatNumber())
            .performance(basicAdDetailsEntity.getPerformance())
            .km(basicAdDetailsEntity.getKm())
            .drive(basicAdDetailsEntity.getDrive())
            .build();
    }

    AdvertisementDto convertAdvertisementEntityToDto(AdvertisementEntity advertisementEntity) {
        return AdvertisementDto.builder()
            .id(advertisementEntity.getId())
            .price(advertisementEntity.getPrice())
            .creator(advertisementEntity.getCreator().getUsername())
            .category(advertisementEntity.getCategory().getName())
            .brand(advertisementEntity.getType().getBrandEntity().getBrandName())
            .condition(advertisementEntity.getProductCondition())
            .created(advertisementEntity.getCreated())
            .title(advertisementEntity.getTitle())
            .state(advertisementEntity.getState())
            .type(advertisementEntity.getType().getName())
            .imagePaths(advertisementEntity.getImages().stream()
                .map(ImageEntity::getPath).collect(Collectors.toSet()))
            .build();
    }

    ImageDto convertImageEntityToModel(ImageEntity imageEntity) {
        return ImageDto.builder()
            .id(imageEntity.getId())
            .path(imageEntity.getPath())
            .uploadedTime(imageEntity.getUploadedTime())
            .build();
    }

    AdDetailsDto convertAdDetailsEntityToModel(AdDetailsEntity adDetailsEntity) {
        return AdDetailsDto.builder()
            .adId(adDetailsEntity.getAdId())
            .description(adDetailsEntity.getDescription())
            .color(adDetailsEntity.getColor())
            .accelaration(adDetailsEntity.getAccelaration())
            .range(adDetailsEntity.getProductRange())
            .maxSpeed(adDetailsEntity.getMaxSpeed())
            .weight(adDetailsEntity.getWeight())
            .build();
    }

    AdDetailsEntity convertAdDetailsToAdDetailsEntity(AdDetailsDto adDetails) {
        return AdDetailsEntity.builder()
            .adId(adDetails.getAdId())
            .maxSpeed(adDetails.getMaxSpeed())
            .productRange(adDetails.getRange())
            .weight(adDetails.getWeight())
            .accelaration(adDetails.getAccelaration())
            .color(adDetails.getColor())
            .description(adDetails.getDescription())
            .build();
    }

    BasicAdDetailsEntity convertAdDetailsToBasicAdDetailsEntity(AdDetailsDto adDetails) {
        return BasicAdDetailsEntity.builder()
            .adId(adDetails.getAdId())
            .performance(adDetails.getPerformance())
            .batterySize(adDetails.getBatterySize())
            .km(adDetails.getKm())
            .chargeSpeed(adDetails.getChargeSpeed())
            .drive(adDetails.getDrive())
            .seatNumber(adDetails.getSeatNumber())
            .year(adDetails.getYear())
            .build();
    }

}
