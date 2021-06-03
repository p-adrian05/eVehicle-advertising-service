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
import org.example.core.image.AdImageService;
import org.example.core.security.AuthException;
import org.example.core.user.exception.UnknownUserException;
import org.example.core.user.persistence.entity.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.sql.Timestamp;
import java.util.Currency;
import java.util.Date;
import java.util.Objects;
import java.util.Optional;

@RequiredArgsConstructor
@Service
@Slf4j
public class AdvertisementServiceImpl implements AdvertisementService {


    private final AdvertisementRepository advertisementRepository;
    private final AdImageService adImageService;
    private final AdUtil adUtil;
    private final AdDetailsService adDetailsService;

    @Override
    @Transactional
    public void createAdvertisement(CreateAdDto advertisementDto, AdDetailsDto adDetailsDto,
                                    MultipartFile[] imageFiles)
        throws UnknownUserException, UnknownCategoryException, FileUploadException {
        Objects.requireNonNull(advertisementDto, "AdvertisementDto cannot be null during creation");
        Objects.requireNonNull(adDetailsDto, "AdDetailsDto cannot be null during creation");
        Objects.requireNonNull(imageFiles, "ImageFile array cannot be null");
        AdvertisementEntity advertisementEntity = createNewAdvertisement(advertisementDto);
        advertisementEntity.setImages(adImageService.store(imageFiles));
        AdvertisementEntity newAdvertisementEntity = advertisementRepository.save(advertisementEntity);
        log.info("New advertisementEntity: {}", newAdvertisementEntity);

        adDetailsService.createAdDetails(adDetailsDto, newAdvertisementEntity);
    }

    @Override
    @Transactional
    public void updateAdvertisementWithDetails(UpdateAdvertisementDto advertisementDto, AdDetailsDto adDetails,
                                               MultipartFile[] imageFiles)
        throws UnknownAdvertisementException, UnknownCategoryException, FileUploadException {
        Objects.requireNonNull(advertisementDto, "AdvertisementDto cannot be null during update");
        Objects.requireNonNull(adDetails, "AdDetailsDto cannot be null during update");
        Objects.requireNonNull(imageFiles, "ImageFile array cannot be null");
        if (!advertisementRepository.existsByIdAndAndCreator_Username(advertisementDto.getId(),
            SecurityContextHolder.getContext().getAuthentication().getName())) {
            throw new AuthException("Access denied");
        }

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
        adDetailsService.updateAdDetails(adDetails);
        advertisementEntity.setImages(adImageService.updateAndStore(advertisementEntity.getImages(), imageFiles));

        advertisementRepository.save(advertisementEntity);
        log.info("Updated advertisementEntity: {}", advertisementEntity);
    }

    @Override
    public Optional<AdvertisementDto> getAdvertisementById(int id,Currency currency) {
        Optional<AdvertisementEntity> advertisementEntity = advertisementRepository.findByIdWithCategoryAndType(id);
        log.info("AdvertisementEntity entity by id: {},{}", id, advertisementEntity);
        if (advertisementEntity.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(adUtil.convertAdvertisementEntityToDto(advertisementEntity.get(),currency));
    }

    @Override
    public Optional<AdDetailsDto> getAdDetailsById(int id) {
        return adDetailsService.getAdDetailsById(id);
    }


    @Override
    public Slice<AdLabelDto> getAdvertisements(AdvertisementQueryParams params, Pageable pageable, Currency currency) {
        Objects.requireNonNull(params, "AdvertisementQueryParams cannot be null");
        Objects.requireNonNull(pageable, "Pageable cannot be null");
        return advertisementRepository.findByParams(params, pageable)
            .map(advertisementEntity -> adUtil.convertAdvertisementEntityToLabelDto(advertisementEntity, currency));
    }

    @Override
    public Page<AdLabelDto> getAdvertisementsByUsername(String username, Pageable pageable, AdState state,
                                                        Currency currency) {
        Objects.requireNonNull(username, "Username cannot be null");
        Objects.requireNonNull(pageable, "Pageable cannot be null");
        Objects.requireNonNull(state, "AdState cannot be null");
        return advertisementRepository.findByCreator(username, pageable, state)
            .map(advertisementEntity -> adUtil
                .convertAdvertisementEntityToLabelDto(advertisementEntity, currency));
    }

    @Override
    public void changeState(int adId, AdState stateToChange, String creatorName) throws UnknownAdvertisementException {
        Objects.requireNonNull(creatorName, "Creator username cannot be null");
        Objects.requireNonNull(stateToChange, "AdState cannot be null");
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
            .currency(Currency.getInstance(advertisementDto.getCurrency()).getCurrencyCode())
            .build();
    }
}
