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
import org.example.core.advertising.model.BasicAdDetails;
import org.example.core.advertising.model.CreateAdDto;
import org.example.core.advertising.model.UpdateAdvertisementDto;
import org.example.core.advertising.persistence.AdState;
import org.example.core.advertising.persistence.entity.AdvertisementEntity;
import org.example.core.advertising.persistence.entity.BasicAdDetailsEntity;
import org.example.core.advertising.persistence.repository.AdvertisementQueryParams;
import org.example.core.advertising.persistence.repository.AdvertisementRepository;
import org.example.core.finance.bank.Bank;
import org.example.core.finance.money.Money;
import org.example.core.image.persistence.entity.ImageEntity;
import org.example.core.security.AuthException;
import org.example.core.user.exception.UnknownUserException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.util.Currency;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Slf4j
public class AdvertisementServiceImpl implements AdvertisementService {


    private final AdvertisementRepository advertisementRepository;
    private final AdEntityBuilder adEntityBuilder;
    private final AdDetailsService adDetailsService;
    private final Bank bank;

    @Override
    @Transactional
    public void createAdvertisement(CreateAdDto advertisementDto, AdDetailsDto adDetailsDto,
                                    MultipartFile[] imageFiles)
        throws UnknownUserException, UnknownCategoryException, FileUploadException {
        Objects.requireNonNull(advertisementDto, "AdvertisementDto cannot be null during creation");
        Objects.requireNonNull(adDetailsDto, "AdDetailsDto cannot be null during creation");
        Objects.requireNonNull(imageFiles, "ImageFile array cannot be null");
        AdvertisementEntity advertisementEntity = adEntityBuilder.createNewAdvertisement(advertisementDto,imageFiles);
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

        AdvertisementEntity updatedAdvertisementEntity = adEntityBuilder.createUpdatedAdvertisement(advertisementDto,imageFiles);
        adDetailsService.updateAdDetails(adDetails);
        advertisementRepository.save(updatedAdvertisementEntity);
        log.info("Updated advertisementEntity: {}", updatedAdvertisementEntity);
    }

    @Override
    public Optional<AdvertisementDto> getAdvertisementById(int id,Currency currency) {
        Optional<AdvertisementEntity> advertisementEntity = advertisementRepository.findByIdWithCategoryAndType(id);
        log.info("AdvertisementEntity entity by id: {},{}", id, advertisementEntity);
        if (advertisementEntity.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(convertAdvertisementEntityToDto(advertisementEntity.get(),currency));
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
            .map(advertisementEntity -> convertAdvertisementEntityToLabelDto(advertisementEntity, currency));
    }

    @Override
    public Page<AdLabelDto> getAdvertisementsByUsername(String username, Pageable pageable, AdState state,
                                                        Currency currency) {
        Objects.requireNonNull(username, "Username cannot be null");
        Objects.requireNonNull(pageable, "Pageable cannot be null");
        Objects.requireNonNull(state, "AdState cannot be null");
        return advertisementRepository.findByCreator(username, pageable, state)
            .map(advertisementEntity -> convertAdvertisementEntityToLabelDto(advertisementEntity, currency));
    }

    @Override
    public void changeState(int adId, AdState stateToChange, String creatorName) throws UnknownAdvertisementException {
        Objects.requireNonNull(creatorName, "Creator username cannot be null");
        Objects.requireNonNull(stateToChange, "AdState cannot be null");
        if (!advertisementRepository.existsByIdAndAndCreator_Username(adId, creatorName)) {
            throw new AuthException("Access denied");
        }
        AdvertisementEntity advertisementEntity = adEntityBuilder.queryAdvertisementEntity(adId);
        log.info(String
            .format("Changing advertisement id: %s  state from %s to %s", adId, advertisementEntity.getState(),
                stateToChange));
        advertisementEntity.setState(stateToChange);
        advertisementRepository.save(advertisementEntity);
    }




    AdLabelDto convertAdvertisementEntityToLabelDto(AdvertisementEntity advertisementEntity, Currency currency) {
        Objects.requireNonNull(advertisementEntity, "AdvertisementEntity cannot be null during converting");
        Objects.requireNonNull(advertisementEntity.getCurrency(), "Currency cannot be null during converting");
        Money money = new Money(advertisementEntity.getPrice(), Currency.getInstance(advertisementEntity.getCurrency()))
            .to(currency, bank);
        return AdLabelDto.builder()
            .id(advertisementEntity.getId())
            .price(money.getAmount())
            .brand(advertisementEntity.getType().getBrandEntity().getBrandName())
            .condition(advertisementEntity.getProductCondition())
            .created(advertisementEntity.getCreated())
            .title(advertisementEntity.getTitle())
            .state(advertisementEntity.getState())
            .type(advertisementEntity.getType().getName())
            .currency(money.getCurrency().getCurrencyCode())
            .imagePaths(advertisementEntity.getImages().stream()
                .map(ImageEntity::getPath).collect(Collectors.toList()))
            .basicAdDetails(convertBasicAdDetailsEntityToModel(advertisementEntity.getBasicAdDetails()))
            .build();
    }

    BasicAdDetails convertBasicAdDetailsEntityToModel(BasicAdDetailsEntity basicAdDetailsEntity) {
        Objects.requireNonNull(basicAdDetailsEntity, "BasicAdDetailsEntity cannot be null during converting");
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

    AdvertisementDto convertAdvertisementEntityToDto(AdvertisementEntity advertisementEntity,Currency currency) {
        Objects.requireNonNull(advertisementEntity, "AdvertisementEntity cannot be null during converting");
        Objects.requireNonNull(advertisementEntity.getCurrency(), "Currency cannot be null during converting");
        Money money = new Money(advertisementEntity.getPrice(), Currency.getInstance(advertisementEntity.getCurrency()))
            .to(currency, bank);
        return AdvertisementDto.builder()
            .id(advertisementEntity.getId())
            .price(money.getAmount())
            .currency(money.getCurrency().getCurrencyCode())
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
}
