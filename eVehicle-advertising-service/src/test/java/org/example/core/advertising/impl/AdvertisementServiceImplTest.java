package org.example.core.advertising.impl;

import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.example.core.advertising.AdDetailsService;
import org.example.core.advertising.exception.UnknownAdvertisementException;
import org.example.core.advertising.exception.UnknownCategoryException;
import org.example.core.advertising.model.AdDetailsDto;
import org.example.core.advertising.model.AdLabelDto;
import org.example.core.advertising.model.AdvertisementDto;
import org.example.core.advertising.model.CreateAdDto;
import org.example.core.advertising.model.UpdateAdvertisementDto;
import org.example.core.advertising.persistence.AdState;
import org.example.core.advertising.persistence.Drive;
import org.example.core.advertising.persistence.ProductState;
import org.example.core.advertising.persistence.entity.AdvertisementEntity;
import org.example.core.advertising.persistence.entity.BrandEntity;
import org.example.core.advertising.persistence.entity.CategoryEntity;
import org.example.core.advertising.persistence.entity.TypeEntity;
import org.example.core.advertising.persistence.repository.AdvertisementQueryParams;
import org.example.core.advertising.persistence.repository.AdvertisementRepository;
import org.example.core.image.AdImageService;
import org.example.core.image.persistence.entity.ImageEntity;
import org.example.core.user.exception.UnknownUserException;
import org.example.core.user.persistence.entity.UserEntity;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.web.multipart.MultipartFile;

import java.sql.Timestamp;
import java.util.Currency;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class AdvertisementServiceImplTest {

    private AdvertisementRepository advertisementRepository;
    private AdImageService adImageService;
    private AdUtil adUtil;
    private AdDetailsService adDetailsService;


    private AdvertisementServiceImpl underTest;

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


    private CreateAdDto createAdDto = CreateAdDto.builder()
        .creator("Username")
        .category("Car")
        .brand("Tesla")
        .condition(ProductState.NEW)
        .price(10000)
        .title("Test")
        .type("model 3")
        .currency("EUR")
        .build();
    private UserEntity userEntity = UserEntity.builder()
        .username(createAdDto.getCreator())
        .password("pass")
        .created(new Timestamp(new Date().getTime()))
        .activation(null)
        .email("email")
        .enabled(true)
        .lastLogin(null)
        .build();

    private BrandEntity brandEntity = BrandEntity.builder()
        .brandName(createAdDto.getBrand())
        .build();
    private TypeEntity typeEntity = TypeEntity.builder()
        .name(createAdDto.getType())
        .build();
    CategoryEntity categoryEntity = CategoryEntity.builder()
        .name(createAdDto.getCategory())
        .build();
    private AdvertisementEntity advertisementEntity = AdvertisementEntity.builder()
        .creator(userEntity)
        .category(categoryEntity)
        .type(typeEntity)
        .title(createAdDto.getTitle())
        .productCondition(createAdDto.getCondition())
        .state(AdState.ACTIVE)
        .images(Set.of(new ImageEntity()))
        .created(new Timestamp(1))
        .price(createAdDto.getPrice())
        .currency(Currency.getInstance(createAdDto.getCurrency()).getCurrencyCode())
        .build();
    private AdvertisementDto advertisementDto = AdvertisementDto.builder()
        .id(advertisementEntity.getId())
        .price((advertisementEntity.getPrice()))
        .currency(advertisementEntity.getCurrency())
        .creator(advertisementEntity.getCreator().getUsername())
        .category(advertisementEntity.getCategory().getName())
        .brand(brandEntity.getBrandName())
        .condition(advertisementEntity.getProductCondition())
        .created(advertisementEntity.getCreated())
        .title(advertisementEntity.getTitle())
        .state(advertisementEntity.getState())
        .type(advertisementEntity.getType().getName())
        .imagePaths(advertisementEntity.getImages().stream()
            .map(ImageEntity::getPath).collect(Collectors.toSet()))
        .build();
    private UpdateAdvertisementDto updateAdvertisementDto = UpdateAdvertisementDto.builder()
        .id(1)
        .category("Car")
        .brand("Tesla")
        .condition(ProductState.NEW)
        .price(10000)
        .title("Test")
        .type("model 3")
        .currency("EUR")
        .build();
    private AdLabelDto adLabelDto = AdLabelDto.builder()
        .id(advertisementEntity.getId())
        .price(advertisementEntity.getPrice())
        .brand(brandEntity.getBrandName())
        .condition(advertisementEntity.getProductCondition())
        .created(advertisementEntity.getCreated())
        .title(advertisementEntity.getTitle())
        .state(advertisementEntity.getState())
        .type(advertisementEntity.getType().getName())
        .currency(advertisementEntity.getCurrency())
        .imagePaths(List.of())
        .basicAdDetails(null)
        .build();

    @BeforeEach
    public void init() {
        advertisementRepository = Mockito.mock(AdvertisementRepository.class);
        adImageService = Mockito.mock(AdImageService.class);
        adDetailsService = Mockito.mock(AdDetailsService.class);
        adUtil = Mockito.mock(AdUtil.class);
        underTest = new AdvertisementServiceImpl(advertisementRepository, adImageService, adUtil, adDetailsService);
    }

    @Test
    public void testCreateAdvertisementShouldCallAdvertisementRepository()
        throws FileUploadException, UnknownCategoryException, UnknownUserException {
        // Given
        Mockito.when(adImageService.store(new MultipartFile[] {})).thenReturn(Set.of(new ImageEntity()));
        Mockito.when(adUtil.queryUserEntity(createAdDto.getCreator())).thenReturn(userEntity);
        Mockito.when(adUtil.queryCategory(createAdDto.getCategory())).thenReturn(categoryEntity);
        Mockito.when(adUtil.getTypeEntity(createAdDto.getType())).thenReturn(typeEntity);
        Mockito.when(advertisementRepository.save(advertisementEntity)).thenReturn(advertisementEntity);
        Mockito.when(adUtil.getBrandEntity(createAdDto.getBrand())).thenReturn(brandEntity);
        // When
        underTest.createAdvertisement(createAdDto, adDetailsDto, new MultipartFile[] {});

        // Then
        Assertions.assertEquals(typeEntity.getBrandEntity(), brandEntity);
        Mockito.verify(adImageService).store(new MultipartFile[] {});
        Mockito.verify(adUtil).queryUserEntity(createAdDto.getCreator());
        Mockito.verify(adUtil).queryCategory(createAdDto.getCategory());
        Mockito.verify(adUtil).getTypeEntity(createAdDto.getType());
        Mockito.verify(adUtil).getBrandEntity(createAdDto.getBrand());
        Mockito.verify(adDetailsService).createAdDetails(adDetailsDto, advertisementEntity);
        Mockito.verify(advertisementRepository).save(advertisementEntity);
        Mockito.verifyNoMoreInteractions(adImageService, adUtil, adDetailsService, advertisementRepository);
    }

    @Test
    public void testCreateAdvertisementShouldThrowUnknownCategoryExceptionWhenCategoryNotExists()
        throws UnknownCategoryException, UnknownUserException {
        // Given

        Mockito.when(adUtil.queryUserEntity(createAdDto.getCreator())).thenReturn(userEntity);
        Mockito.when(adUtil.queryCategory(createAdDto.getCategory())).thenThrow(UnknownCategoryException.class);
        // When
        Assertions.assertThrows(UnknownCategoryException.class,
            () -> underTest.createAdvertisement(createAdDto, adDetailsDto, new MultipartFile[] {}));
        // Then
        Mockito.verify(adUtil).queryUserEntity(createAdDto.getCreator());
        Mockito.verify(adUtil).queryCategory(createAdDto.getCategory());
        Mockito.verifyNoMoreInteractions(adImageService, adUtil, adDetailsService, advertisementRepository);
    }

    @Test
    public void testCreateAdvertisementShouldThrowUnknownUserExceptionWhenUserNotExists()
        throws UnknownUserException {
        // Given
        Mockito.when(adUtil.queryUserEntity(createAdDto.getCreator())).thenThrow(UnknownUserException.class);
        // When
        Assertions.assertThrows(UnknownUserException.class,
            () -> underTest.createAdvertisement(createAdDto, adDetailsDto, new MultipartFile[] {}));
        // Then
        Mockito.verify(adUtil).queryUserEntity(createAdDto.getCreator());
        Mockito.verifyNoMoreInteractions(adImageService, adUtil, adDetailsService, advertisementRepository);
    }

    @Test
    public void testCreateAdvertisementShouldThrowFileUploadExceptionWhenFileCannotBeUploaded()
        throws UnknownUserException, FileUploadException, UnknownCategoryException {
        // Given
        Mockito.when(adImageService.store(new MultipartFile[] {})).thenThrow(FileUploadException.class);
        Mockito.when(adUtil.queryUserEntity(createAdDto.getCreator())).thenReturn(userEntity);
        Mockito.when(adUtil.queryCategory(createAdDto.getCategory())).thenReturn(categoryEntity);
        Mockito.when(adUtil.getTypeEntity(createAdDto.getType())).thenReturn(typeEntity);
        Mockito.when(advertisementRepository.save(advertisementEntity)).thenReturn(advertisementEntity);
        Mockito.when(adUtil.getBrandEntity(createAdDto.getBrand())).thenReturn(brandEntity);
        // When
        Assertions.assertThrows(FileUploadException.class,
            () -> underTest.createAdvertisement(createAdDto, adDetailsDto, new MultipartFile[] {}));
        // Then
        Assertions.assertEquals(typeEntity.getBrandEntity(), brandEntity);
        Mockito.verify(adImageService).store(new MultipartFile[] {});
        Mockito.verify(adUtil).queryUserEntity(createAdDto.getCreator());
        Mockito.verify(adUtil).queryCategory(createAdDto.getCategory());
        Mockito.verify(adUtil).getTypeEntity(createAdDto.getType());
        Mockito.verify(adUtil).getBrandEntity(createAdDto.getBrand());
        Mockito.verifyNoMoreInteractions(adImageService, adUtil, adDetailsService, advertisementRepository);
    }

    @Test
    public void testUpdateAdvertisementWithDetailsShouldCallAdvertisementRepository()
        throws UnknownCategoryException, FileUploadException, UnknownAdvertisementException {
        // Given
        Mockito.when(advertisementRepository.findById(updateAdvertisementDto.getId()))
            .thenReturn(Optional.of(advertisementEntity));
        Mockito.when(adUtil.queryCategory(createAdDto.getCategory())).thenReturn(categoryEntity);
        Mockito.when(adUtil.getTypeEntity(createAdDto.getType())).thenReturn(typeEntity);
        Mockito.when(adUtil.getBrandEntity(createAdDto.getBrand())).thenReturn(brandEntity);
        Mockito.when(
            adImageService.updateAndStore(advertisementEntity.getImages(), new MultipartFile[] {}))
            .thenReturn(Set.of(new ImageEntity()));

        // When
        underTest.updateAdvertisementWithDetails(updateAdvertisementDto, adDetailsDto, new MultipartFile[] {});
        // Then
        Mockito.verify(adImageService).updateAndStore(advertisementEntity.getImages(), new MultipartFile[] {});
        Mockito.verify(advertisementRepository).findById(updateAdvertisementDto.getId());
        Mockito.verify(adUtil).queryCategory(createAdDto.getCategory());
        Mockito.verify(adUtil).getTypeEntity(createAdDto.getType());
        Mockito.verify(adUtil).getBrandEntity(createAdDto.getBrand());
        Mockito.verify(adDetailsService).updateAdDetails(adDetailsDto);
        Mockito.verify(advertisementRepository).save(advertisementEntity);
        Mockito.verifyNoMoreInteractions(adImageService, adUtil, adDetailsService, advertisementRepository);
    }

    @Test
    public void testUpdateAdvertisementWithDetailsShouldThrowUnknownAdvertisementExceptionWhenAdNotExists() {
        // Given
        Mockito.when(advertisementRepository.findById(updateAdvertisementDto.getId()))
            .thenReturn(Optional.empty());
        // When
        Assertions.assertThrows(UnknownAdvertisementException.class,
            () -> underTest
                .updateAdvertisementWithDetails(updateAdvertisementDto, adDetailsDto, new MultipartFile[] {}));
        // Then
        Mockito.verify(advertisementRepository).findById(updateAdvertisementDto.getId());
        Mockito.verifyNoMoreInteractions(adImageService, adUtil, adDetailsService, advertisementRepository);
    }

    @Test
    public void testUpdateAdvertisementWithDetailsShouldThrowUnknownCategoryExceptionExceptionWhenCategoryNotExists()
        throws UnknownCategoryException {
        // Given
        Mockito.when(advertisementRepository.findById(updateAdvertisementDto.getId()))
            .thenReturn(Optional.of(advertisementEntity));
        Mockito.when(adUtil.queryCategory(createAdDto.getCategory())).thenThrow(UnknownCategoryException.class);
        // When
        Assertions.assertThrows(UnknownCategoryException.class,
            () -> underTest
                .updateAdvertisementWithDetails(updateAdvertisementDto, adDetailsDto, new MultipartFile[] {}));
        // Then
        Mockito.verify(advertisementRepository).findById(updateAdvertisementDto.getId());
        Mockito.verify(adUtil).queryCategory(createAdDto.getCategory());
        Mockito.verifyNoMoreInteractions(adImageService, adUtil, adDetailsService, advertisementRepository);
    }

    @Test
    public void testUpdateAdvertisementWithDetailsShouldThrowFileUploadExceptionExceptionExceptionWhenFileCannotBeUploaded()
        throws FileUploadException, UnknownCategoryException {
        // Given
        Mockito.when(advertisementRepository.findById(updateAdvertisementDto.getId()))
            .thenReturn(Optional.of(advertisementEntity));
        Mockito.when(adUtil.queryCategory(createAdDto.getCategory())).thenReturn(categoryEntity);
        Mockito.when(adUtil.getTypeEntity(createAdDto.getType())).thenReturn(typeEntity);
        Mockito.when(adUtil.getBrandEntity(createAdDto.getBrand())).thenReturn(brandEntity);
        Mockito.when(
            adImageService.updateAndStore(advertisementEntity.getImages(), new MultipartFile[] {}))
            .thenThrow(FileUploadException.class);
        // When
        Assertions.assertThrows(FileUploadException.class,
            () -> underTest
                .updateAdvertisementWithDetails(updateAdvertisementDto, adDetailsDto, new MultipartFile[] {}));
        // Then
        Mockito.verify(adImageService).updateAndStore(advertisementEntity.getImages(), new MultipartFile[] {});
        Mockito.verify(advertisementRepository).findById(updateAdvertisementDto.getId());
        Mockito.verify(adUtil).queryCategory(createAdDto.getCategory());
        Mockito.verify(adUtil).getTypeEntity(createAdDto.getType());
        Mockito.verify(adUtil).getBrandEntity(createAdDto.getBrand());
        Mockito.verifyNoMoreInteractions(adImageService, adUtil, adDetailsService, advertisementRepository);
    }

    @Test
    public void testGetAdvertisementByIdShouldCallAdvertisementRepositoryAndReturnOptionalIfExistsAdById() {
        // Given
        Mockito.when(advertisementRepository.findByIdWithCategoryAndType(advertisementEntity.getId()))
            .thenReturn(Optional.of(advertisementEntity));
        Mockito.when(adUtil.convertAdvertisementEntityToDto(advertisementEntity,
            Currency.getInstance(advertisementEntity.getCurrency())))
            .thenReturn(advertisementDto);
        // When
        Optional<AdvertisementDto> actual = underTest
            .getAdvertisementById(advertisementEntity.getId(), Currency.getInstance(advertisementEntity.getCurrency()));
        // Then
        Assertions.assertEquals(Optional.of(advertisementDto), actual);
        Mockito.verify(advertisementRepository).findByIdWithCategoryAndType(advertisementEntity.getId());
        Mockito.verify(adUtil).convertAdvertisementEntityToDto(advertisementEntity,
            Currency.getInstance(advertisementEntity.getCurrency()));
        Mockito.verifyNoMoreInteractions(advertisementRepository, adUtil);
    }

    @Test
    public void testGetAdvertisementByIdShouldCallAdvertisementRepositoryAndReturnEmptyIfNotExistsAdById() {
        // Given
        Mockito.when(advertisementRepository.findByIdWithCategoryAndType(advertisementEntity.getId()))
            .thenReturn(Optional.empty());
        // When
        Optional<AdvertisementDto> actual = underTest
            .getAdvertisementById(advertisementEntity.getId(), Currency.getInstance(advertisementEntity.getCurrency()));
        // Then
        Assertions.assertEquals(Optional.empty(), actual);
        Mockito.verify(advertisementRepository).findByIdWithCategoryAndType(advertisementEntity.getId());
        Mockito.verifyNoMoreInteractions(advertisementRepository);
    }

    @Test
    public void testGetAdDetailsByIdShouldCallAdDetailsServiceAndReturnOptionalAdDetailsDto() {
        // Given
        Mockito.when(adDetailsService.getAdDetailsById(adDetailsDto.getAdId()))
            .thenReturn(Optional.of(adDetailsDto));
        // When
        Optional<AdDetailsDto> actual = underTest
            .getAdDetailsById(adDetailsDto.getAdId());
        // Then
        Assertions.assertEquals(Optional.of(adDetailsDto), actual);
        Mockito.verify(adDetailsService).getAdDetailsById(adDetailsDto.getAdId());
        Mockito.verifyNoMoreInteractions(adDetailsService);
    }

    @Test
    public void testGetAdvertisementsShouldCallAdvertisementRepositoryAndReturnSliceOfAdLabelDto() {
        // Given
        Slice<AdLabelDto> adLabelDtos = new SliceImpl<>(List.of(adLabelDto));
        Currency currency = Currency.getInstance("HUF");
        Slice<AdvertisementEntity> advertisementEntities = new SliceImpl<>(List.of(advertisementEntity));
        AdvertisementQueryParams advertisementQueryParams = AdvertisementQueryParams.builder().build();
        Pageable pageable = PageRequest.of(1, 1);

        Mockito.when(advertisementRepository.findByParams(advertisementQueryParams, pageable))
            .thenReturn(advertisementEntities);
        Mockito.when(adUtil.convertAdvertisementEntityToLabelDto(advertisementEntity, currency))
            .thenReturn(adLabelDto);
        // When
        Slice<AdLabelDto> actual = underTest
            .getAdvertisements(advertisementQueryParams, pageable, currency);
        // Then
        Assertions.assertEquals(adLabelDtos, actual);
        Mockito.verify(advertisementRepository).findByParams(advertisementQueryParams, pageable);
        Mockito.verify(adUtil).convertAdvertisementEntityToLabelDto(advertisementEntity, currency);
        Mockito.verifyNoMoreInteractions(advertisementRepository, adUtil);
    }

    @Test
    public void testGetAdvertisementsByUsernameShouldCallAdvertisementRepositoryAndReturnPageOfAdLabelDto() {
        // Given
        Page<AdLabelDto> adLabelDtos = new PageImpl<>(List.of(adLabelDto));
        Currency currency = Currency.getInstance("HUF");
        Page<AdvertisementEntity> advertisementEntities = new PageImpl<>(List.of(advertisementEntity));
        Pageable pageable = PageRequest.of(1, 1);

        Mockito.when(
            advertisementRepository.findByCreator(userEntity.getUsername(), pageable, advertisementEntity.getState()))
            .thenReturn(advertisementEntities);
        Mockito.when(adUtil.convertAdvertisementEntityToLabelDto(advertisementEntity, currency))
            .thenReturn(adLabelDto);
        // When
        Page<AdLabelDto> actual = underTest
            .getAdvertisementsByUsername(userEntity.getUsername(), pageable, advertisementEntity.getState(), currency);
        // Then
        Assertions.assertEquals(adLabelDtos, actual);
        Mockito.verify(advertisementRepository)
            .findByCreator(userEntity.getUsername(), pageable, advertisementEntity.getState());
        Mockito.verify(adUtil).convertAdvertisementEntityToLabelDto(advertisementEntity, currency);
        Mockito.verifyNoMoreInteractions(advertisementRepository, adUtil);
    }

    @Test
    public void testChangeStateShouldCallAdvertisementRepositoryIfInputIsValid()
        throws UnknownAdvertisementException {
        // Given
        Mockito.when(advertisementRepository
            .existsByIdAndAndCreator_Username(advertisementEntity.getId(), userEntity.getUsername()))
            .thenReturn(true);
        Mockito.when(advertisementRepository.findById(advertisementEntity.getId())).thenReturn(
            Optional.ofNullable(advertisementEntity));
        // When
        underTest.changeState(advertisementEntity.getId(), AdState.ACTIVE, userEntity.getUsername());
        // Then
        Assertions.assertEquals(advertisementEntity.getState(), AdState.ACTIVE);
        Mockito.verify(advertisementRepository)
            .existsByIdAndAndCreator_Username(advertisementEntity.getId(), userEntity.getUsername());
        Mockito.verify(advertisementRepository).findById(advertisementEntity.getId());
        Mockito.verify(advertisementRepository).save(advertisementEntity);
        Mockito.verifyNoMoreInteractions(advertisementRepository);
    }

    @Test
    public void testChangeStateShouldThrowUnknownAdvertisementExceptionIfAdNotExistsById() {
        // Given
        Mockito.when(advertisementRepository
            .existsByIdAndAndCreator_Username(advertisementEntity.getId(), userEntity.getUsername()))
            .thenReturn(true);
        Mockito.when(advertisementRepository.findById(advertisementEntity.getId())).thenReturn(
            Optional.empty());
        // When
        Assertions.assertThrows(UnknownAdvertisementException.class,
            () -> underTest.changeState(advertisementEntity.getId(), AdState.ACTIVE, userEntity.getUsername()));
        // Then
        Mockito.verify(advertisementRepository)
            .existsByIdAndAndCreator_Username(advertisementEntity.getId(), userEntity.getUsername());
        Mockito.verify(advertisementRepository).findById(advertisementEntity.getId());
        Mockito.verifyNoMoreInteractions(advertisementRepository);
    }

}
