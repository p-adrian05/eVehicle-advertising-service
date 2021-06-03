package org.example.core.advertising.impl;

import org.example.core.advertising.exception.UnknownAdvertisementException;
import org.example.core.advertising.model.AdDetailsDto;
import org.example.core.advertising.persistence.Drive;
import org.example.core.advertising.persistence.entity.AdDetailsEntity;
import org.example.core.advertising.persistence.entity.AdvertisementEntity;
import org.example.core.advertising.persistence.entity.BasicAdDetailsEntity;
import org.example.core.advertising.persistence.repository.AdDetailsRepository;
import org.example.core.advertising.persistence.repository.BasicAdDetailsRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Optional;

public class AdDetailsServiceImplTest {

    private AdDetailsRepository adDetailsRepository;
    private BasicAdDetailsRepository basicAdDetailsRepository;
    private AdUtil adUtil;

    private AdDetailsServiceImpl underTest;

    private AdDetailsDto adDetailsDto = AdDetailsDto.builder()
        .weight(1200)
        .maxSpeed(241)
        .range(1200)
        .accelaration(2)
        .color("Red")
        .description("Desc")
        .adId(1)
        .batterySize(89)
        .chargeSpeed(200)
        .km(34)
        .drive(Drive.AWD)
        .performance(900)
        .seatNumber(2)
        .year(2020)
        .build();
    private AdDetailsEntity adDetailsEntity = AdDetailsEntity.builder()
        .adId(adDetailsDto.getAdId())
        .maxSpeed(adDetailsDto.getMaxSpeed())
        .productRange(adDetailsDto.getRange())
        .weight(adDetailsDto.getWeight())
        .accelaration(adDetailsDto.getAccelaration())
        .color(adDetailsDto.getColor())
        .description(adDetailsDto.getDescription())
        .build();

    private BasicAdDetailsEntity basicAdDetailsEntity = BasicAdDetailsEntity.builder()
        .adId(adDetailsDto.getAdId())
        .performance(adDetailsDto.getPerformance())
        .batterySize(adDetailsDto.getBatterySize())
        .km(adDetailsDto.getKm())
        .chargeSpeed(adDetailsDto.getChargeSpeed())
        .drive(adDetailsDto.getDrive())
        .seatNumber(adDetailsDto.getSeatNumber())
        .year(adDetailsDto.getYear())
        .build();

    private AdvertisementEntity advertisementEntity = new AdvertisementEntity();

    @BeforeEach
    public void init() {
        adDetailsRepository = Mockito.mock(AdDetailsRepository.class);
        basicAdDetailsRepository = Mockito.mock(BasicAdDetailsRepository.class);
        adUtil = Mockito.mock(AdUtil.class);
        underTest = new AdDetailsServiceImpl(adDetailsRepository, basicAdDetailsRepository, adUtil);
    }

    @Test
    public void testUpdateAdDetailsShouldCallAdDetailsRepositoryAndBasicAdDetailsRepository()
        throws UnknownAdvertisementException {
        // Given
        Mockito.when(adDetailsRepository.existsById(adDetailsDto.getAdId())).thenReturn(true);
        Mockito.when(adUtil.convertAdDetailsToAdDetailsEntity(adDetailsDto)).thenReturn(adDetailsEntity);
        Mockito.when(adUtil.convertAdDetailsToBasicAdDetailsEntity(adDetailsDto)).thenReturn(basicAdDetailsEntity);

        // When
        underTest.updateAdDetails(adDetailsDto);

        // Then
        Mockito.verify(adDetailsRepository).save(adDetailsEntity);
        Mockito.verify(adDetailsRepository).existsById(adDetailsDto.getAdId());
        Mockito.verify(basicAdDetailsRepository).save(basicAdDetailsEntity);
        Mockito.verifyNoMoreInteractions(adDetailsRepository, basicAdDetailsRepository);
    }

    @Test
    public void testUpdateAdDetailsShouldThrowUnknownAdvertisementExceptionWhenAdIdNotFound() {
        // Given
        Mockito.when(adDetailsRepository.existsById(adDetailsDto.getAdId())).thenReturn(false);

        // When
        Assertions.assertThrows(UnknownAdvertisementException.class, () -> underTest.updateAdDetails(adDetailsDto));

        // Then
        Mockito.verify(adDetailsRepository).existsById(adDetailsDto.getAdId());
        Mockito.verifyNoMoreInteractions(adDetailsRepository, basicAdDetailsRepository);
    }

    @Test
    public void testCreateAdDetailsShouldCallAdDetailsRepositoryAndBasicAdDetailsRepository() {
        // Given
        Mockito.when(adUtil.convertAdDetailsToAdDetailsEntity(adDetailsDto)).thenReturn(adDetailsEntity);
        Mockito.when(adUtil.convertAdDetailsToBasicAdDetailsEntity(adDetailsDto)).thenReturn(basicAdDetailsEntity);

        // When
        underTest.createAdDetails(adDetailsDto, advertisementEntity);

        // Then
        Assertions.assertEquals(basicAdDetailsEntity.getAdvertisement(), advertisementEntity);
        Assertions.assertEquals(adDetailsEntity.getAdvertisement(), advertisementEntity);
        Mockito.verify(adDetailsRepository).save(adDetailsEntity);
        Mockito.verify(basicAdDetailsRepository).save(basicAdDetailsEntity);
        Mockito.verifyNoMoreInteractions(adDetailsRepository, basicAdDetailsRepository);
    }

    @Test
    public void testGetAdDetailsByIdShouldReturnOptionalAdDetailsDtoIfExistsTheId() {
        // Given
        Mockito.when(adDetailsRepository.findById(adDetailsDto.getAdId())).thenReturn(Optional.of(adDetailsEntity));
        Mockito.when(basicAdDetailsRepository.findById(adDetailsDto.getAdId()))
            .thenReturn(Optional.of(basicAdDetailsEntity));

        // When
        Optional<AdDetailsDto> actual = underTest.getAdDetailsById(adDetailsDto.getAdId());

        // Then
        Assertions.assertEquals(adDetailsDto, actual.get());
        Mockito.verify(adDetailsRepository).findById(adDetailsDto.getAdId());
        Mockito.verify(basicAdDetailsRepository).findById(adDetailsDto.getAdId());
        Mockito.verifyNoMoreInteractions(adDetailsRepository, basicAdDetailsRepository);
    }

    @Test
    public void testGetAdDetailsByIdShouldReturnEmptyOptionalIfNotExistsTheId() {
        // Given
        Mockito.when(adDetailsRepository.findById(adDetailsDto.getAdId())).thenReturn(Optional.empty());
        Mockito.when(basicAdDetailsRepository.findById(adDetailsDto.getAdId()))
            .thenReturn(Optional.empty());

        // When
        Optional<AdDetailsDto> actual = underTest.getAdDetailsById(adDetailsDto.getAdId());

        // Then
        Assertions.assertEquals(Optional.empty(), actual);
        Mockito.verify(adDetailsRepository).findById(adDetailsDto.getAdId());
        Mockito.verify(basicAdDetailsRepository).findById(adDetailsDto.getAdId());
        Mockito.verifyNoMoreInteractions(adDetailsRepository, basicAdDetailsRepository);
    }
}
