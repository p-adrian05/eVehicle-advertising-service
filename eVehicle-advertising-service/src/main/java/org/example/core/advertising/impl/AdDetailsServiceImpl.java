package org.example.core.advertising.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.core.advertising.AdDetailsService;
import org.example.core.advertising.exception.UnknownAdvertisementException;
import org.example.core.advertising.model.AdDetailsDto;
import org.example.core.advertising.persistence.entity.AdDetailsEntity;
import org.example.core.advertising.persistence.entity.AdvertisementEntity;
import org.example.core.advertising.persistence.entity.BasicAdDetailsEntity;
import org.example.core.advertising.persistence.repository.AdDetailsRepository;
import org.example.core.advertising.persistence.repository.BasicAdDetailsRepository;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdDetailsServiceImpl implements AdDetailsService {

    private final AdDetailsRepository adDetailsRepository;
    private final BasicAdDetailsRepository basicAdDetailsRepository;

    @Override
    public Optional<AdDetailsDto> getAdDetailsById(int id) {
        Optional<BasicAdDetailsEntity> basicAdDetailsEntity = basicAdDetailsRepository.findById(id);
        Optional<AdDetailsEntity> adDetailsEntity = adDetailsRepository.findById(id);
        if (basicAdDetailsEntity.isPresent() && adDetailsEntity.isPresent()) {
            return Optional.of(AdDetailsDto.builder()
                .weight(adDetailsEntity.get().getWeight())
                .maxSpeed(adDetailsEntity.get().getMaxSpeed())
                .range(adDetailsEntity.get().getProductRange())
                .accelaration(adDetailsEntity.get().getAccelaration())
                .color(adDetailsEntity.get().getColor())
                .description(adDetailsEntity.get().getDescription())
                .adId(adDetailsEntity.get().getAdId())
                .batterySize(basicAdDetailsEntity.get().getBatterySize())
                .chargeSpeed(basicAdDetailsEntity.get().getChargeSpeed())
                .km(basicAdDetailsEntity.get().getKm())
                .drive(basicAdDetailsEntity.get().getDrive())
                .performance(basicAdDetailsEntity.get().getPerformance())
                .seatNumber(basicAdDetailsEntity.get().getSeatNumber())
                .year(basicAdDetailsEntity.get().getYear())
                .build());
        }
        return Optional.empty();
    }
    public void updateAdDetails(AdDetailsDto adDetailsDto) throws UnknownAdvertisementException {
        Objects.requireNonNull(adDetailsDto, "AdDetailsDto cannot be null during update process");
        if (!adDetailsRepository.existsById(adDetailsDto.getAdId())) {
            throw new UnknownAdvertisementException(
                String.format("Advertisement not found by id: %s", adDetailsDto.getAdId()));
        }
        adDetailsRepository.save(convertAdDetailsToAdDetailsEntity(adDetailsDto));
        basicAdDetailsRepository.save(convertAdDetailsToBasicAdDetailsEntity(adDetailsDto));
        log.info("Updated AdDetails: {}", adDetailsDto);
    }

    public void createAdDetails(AdDetailsDto adDetailsDto, AdvertisementEntity advertisementEntity){
        Objects.requireNonNull(adDetailsDto, "AdDetailsDto cannot be null during update process");
        Objects.requireNonNull(advertisementEntity, "AdvertisementEntity cannot be null during creating ad details entity");
        BasicAdDetailsEntity basicAdDetailsEntity = convertAdDetailsToBasicAdDetailsEntity(adDetailsDto);
        basicAdDetailsEntity.setAdvertisement(advertisementEntity);
        basicAdDetailsRepository.save(basicAdDetailsEntity);

        AdDetailsEntity adDetailsEntity = convertAdDetailsToAdDetailsEntity(adDetailsDto);
        adDetailsEntity.setAdvertisement(advertisementEntity);
        adDetailsRepository.save(adDetailsEntity);
        log.info("Created AdDetails: {}", adDetailsDto);
    }

    private AdDetailsEntity convertAdDetailsToAdDetailsEntity(AdDetailsDto adDetails) {
        Objects.requireNonNull(adDetails, "AdDetailsDto cannot be null during converting");
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

    private BasicAdDetailsEntity convertAdDetailsToBasicAdDetailsEntity(AdDetailsDto adDetails) {
        Objects.requireNonNull(adDetails, "AdDetailsDto cannot be null during converting");
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
