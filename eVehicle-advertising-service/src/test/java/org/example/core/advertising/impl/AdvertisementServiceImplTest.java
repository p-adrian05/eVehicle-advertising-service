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
import org.example.core.advertising.persistence.entity.BasicAdDetailsEntity;
import org.example.core.advertising.persistence.entity.BrandEntity;
import org.example.core.advertising.persistence.entity.CategoryEntity;
import org.example.core.advertising.persistence.entity.TypeEntity;
import org.example.core.advertising.persistence.repository.AdvertisementQueryParams;
import org.example.core.advertising.persistence.repository.AdvertisementRepository;
import org.example.core.finance.bank.Bank;
import org.example.core.finance.bank.staticbank.impl.StaticBank;
import org.example.core.finance.bank.staticbank.model.StaticExchangeRates;
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
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class AdvertisementServiceImplTest {

    private AdvertisementRepository advertisementRepository;
    private AdEntityBuilder adEntityBuilder;
    private AdDetailsService adDetailsService;
    private Bank bank;


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
        .brandEntity(brandEntity)
        .build();
    CategoryEntity categoryEntity = CategoryEntity.builder()
        .name(createAdDto.getCategory())
        .build();
    private AdvertisementEntity advertisementEntity = AdvertisementEntity.builder()
        .creator(userEntity)
        .id(0)
        .category(categoryEntity)
        .type(typeEntity)
        .title(createAdDto.getTitle())
        .productCondition(createAdDto.getCondition())
        .state(AdState.ACTIVE)
        .basicAdDetails(BasicAdDetailsEntity.builder()
            .adId(0)
            .batterySize(12)
            .build())
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
        bank = StaticBank.of(() -> new StaticExchangeRates.Builder()
            .addRate("HUF", "EUR", 0.0029, 346)
            .addRate("HUF", "USD", 0.0035, 283)
            .addRate("EUR", "USD", 1.22, 0.82)
            .build());
        adEntityBuilder = Mockito.mock(AdEntityBuilder.class);
        adDetailsService = Mockito.mock(AdDetailsService.class);
        underTest = new AdvertisementServiceImpl(advertisementRepository, adEntityBuilder, adDetailsService,bank);
    }

    @Test
    public void testCreateAdvertisementShouldCallAdvertisementRepository()
        throws FileUploadException, UnknownCategoryException, UnknownUserException {
        // Given
        Mockito.when(advertisementRepository.save(advertisementEntity)).thenReturn(advertisementEntity);
        Mockito.when(adEntityBuilder.createNewAdvertisement(createAdDto,new MultipartFile[] {})).thenReturn(advertisementEntity);
        // When
        underTest.createAdvertisement(createAdDto, adDetailsDto, new MultipartFile[] {});

        // Then
        Mockito.verify(adEntityBuilder).createNewAdvertisement(createAdDto,new MultipartFile[] {});
        Mockito.verify(adDetailsService).createAdDetails(adDetailsDto, advertisementEntity);
        Mockito.verify(advertisementRepository).save(advertisementEntity);
        Mockito.verifyNoMoreInteractions(adEntityBuilder,adDetailsService, advertisementRepository);
    }

    @Test
    public void testCreateAdvertisementShouldThrowUnknownCategoryExceptionWhenCategoryNotExists()
        throws UnknownCategoryException, UnknownUserException, FileUploadException {
        // Given

        Mockito.when(adEntityBuilder.createNewAdvertisement(createAdDto,new MultipartFile[]{})).thenThrow(UnknownCategoryException.class);
        // When
        Assertions.assertThrows(UnknownCategoryException.class,
            () -> underTest.createAdvertisement(createAdDto, adDetailsDto, new MultipartFile[] {}));
        // Then
        Mockito.verify(adEntityBuilder).createNewAdvertisement(createAdDto,new MultipartFile[]{});
        Mockito.verifyNoMoreInteractions(adEntityBuilder,adDetailsService, advertisementRepository);
    }

    @Test
    public void testCreateAdvertisementShouldThrowUnknownUserExceptionWhenUserNotExists()
        throws UnknownUserException, UnknownCategoryException, FileUploadException {
        // Given
        Mockito.when(adEntityBuilder.createNewAdvertisement(createAdDto,new MultipartFile[]{})).thenThrow(UnknownUserException.class);
        // When
        Assertions.assertThrows(UnknownUserException.class,
            () -> underTest.createAdvertisement(createAdDto, adDetailsDto, new MultipartFile[] {}));
        // Then
        Mockito.verify(adEntityBuilder).createNewAdvertisement(createAdDto,new MultipartFile[]{});
        Mockito.verifyNoMoreInteractions(adEntityBuilder,adDetailsService, advertisementRepository);
    }

    @Test
    public void testCreateAdvertisementShouldThrowFileUploadExceptionWhenFileCannotBeUploaded()
        throws UnknownUserException, FileUploadException, UnknownCategoryException {
        // Given
        Mockito.when(adEntityBuilder.createNewAdvertisement(createAdDto,new MultipartFile[]{})).thenThrow(FileUploadException.class);
        Mockito.when(advertisementRepository.save(advertisementEntity)).thenReturn(advertisementEntity);
        // When
        Assertions.assertThrows(FileUploadException.class,
            () -> underTest.createAdvertisement(createAdDto, adDetailsDto, new MultipartFile[] {}));
        // Then
        Mockito.verify(adEntityBuilder).createNewAdvertisement(createAdDto,new MultipartFile[]{});
        Mockito.verifyNoMoreInteractions(adEntityBuilder,adDetailsService, advertisementRepository);
    }

    @Test
    public void testUpdateAdvertisementWithDetailsShouldCallAdvertisementRepository()
        throws UnknownCategoryException, FileUploadException, UnknownAdvertisementException {
        // Given
        Mockito.when(adEntityBuilder.createUpdatedAdvertisement(updateAdvertisementDto,new MultipartFile[]{}))
            .thenReturn(advertisementEntity);
        // When
        underTest.updateAdvertisementWithDetails(updateAdvertisementDto, adDetailsDto, new MultipartFile[] {});
        // Then
        Mockito.verify(adEntityBuilder).createUpdatedAdvertisement(updateAdvertisementDto,new MultipartFile[]{});
        Mockito.verify(adDetailsService).updateAdDetails(adDetailsDto);
        Mockito.verify(advertisementRepository).save(advertisementEntity);
        Mockito.verifyNoMoreInteractions(adEntityBuilder,adDetailsService, advertisementRepository);
    }

    @Test
    public void testUpdateAdvertisementWithDetailsShouldThrowUnknownAdvertisementExceptionWhenAdNotExists()
        throws UnknownCategoryException, UnknownAdvertisementException, FileUploadException {
        // Given
        Mockito.when(adEntityBuilder.createUpdatedAdvertisement(updateAdvertisementDto,new MultipartFile[]{})).thenThrow(UnknownAdvertisementException.class);

        // When
        Assertions.assertThrows(UnknownAdvertisementException.class,
            () -> underTest
                .updateAdvertisementWithDetails(updateAdvertisementDto, adDetailsDto, new MultipartFile[] {}));
        // Then
        Mockito.verify(adEntityBuilder).createUpdatedAdvertisement(updateAdvertisementDto,new MultipartFile[]{});
        Mockito.verifyNoMoreInteractions(adEntityBuilder,adDetailsService, advertisementRepository);
    }

    @Test
    public void testUpdateAdvertisementWithDetailsShouldThrowUnknownCategoryExceptionExceptionWhenCategoryNotExists()
        throws UnknownCategoryException, UnknownAdvertisementException, FileUploadException {
        // Given
        Mockito.when(adEntityBuilder.createUpdatedAdvertisement(updateAdvertisementDto,new MultipartFile[]{})).thenThrow(UnknownCategoryException.class);
        // When
        Assertions.assertThrows(UnknownCategoryException.class,
            () -> underTest
                .updateAdvertisementWithDetails(updateAdvertisementDto, adDetailsDto, new MultipartFile[] {}));
        // Then
        Mockito.verify(adEntityBuilder).createUpdatedAdvertisement(updateAdvertisementDto,new MultipartFile[]{});
        Mockito.verifyNoMoreInteractions(adEntityBuilder,adDetailsService, advertisementRepository);
    }

    @Test
    public void testUpdateAdvertisementWithDetailsShouldThrowFileUploadExceptionExceptionExceptionWhenFileCannotBeUploaded()
        throws FileUploadException, UnknownCategoryException, UnknownAdvertisementException {
        // Given
        Mockito.when(adEntityBuilder.createUpdatedAdvertisement(updateAdvertisementDto,new MultipartFile[]{})).thenThrow(FileUploadException.class);
        // When
        Assertions.assertThrows(FileUploadException.class,
            () -> underTest
                .updateAdvertisementWithDetails(updateAdvertisementDto, adDetailsDto, new MultipartFile[] {}));
        // Then
        Mockito.verify(adEntityBuilder).createUpdatedAdvertisement(updateAdvertisementDto,new MultipartFile[]{});
        Mockito.verifyNoMoreInteractions(adEntityBuilder,adDetailsService, advertisementRepository);
    }

    @Test
    public void testGetAdvertisementByIdShouldCallAdvertisementRepositoryAndReturnOptionalIfExistsAdById() {
        // Given
        Mockito.when(advertisementRepository.findByIdWithCategoryAndType(advertisementEntity.getId()))
            .thenReturn(Optional.of(advertisementEntity));
        // When
        Optional<AdvertisementDto> actual = underTest
            .getAdvertisementById(advertisementEntity.getId(), Currency.getInstance(advertisementEntity.getCurrency()));
        // Then
        Assertions.assertEquals(Optional.of(advertisementDto), actual);
        Mockito.verify(advertisementRepository).findByIdWithCategoryAndType(advertisementEntity.getId());
        Mockito.verifyNoMoreInteractions(advertisementRepository);
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
        // When
        Slice<AdLabelDto> actual = underTest
            .getAdvertisements(advertisementQueryParams, pageable, currency);
        // Then
        Assertions.assertEquals(adLabelDtos.getContent().size(), actual.getContent().size());
        Mockito.verify(advertisementRepository).findByParams(advertisementQueryParams, pageable);
        Mockito.verifyNoMoreInteractions(advertisementRepository);
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
        // When
        Page<AdLabelDto> actual = underTest
            .getAdvertisementsByUsername(userEntity.getUsername(), pageable, advertisementEntity.getState(), currency);
        // Then
        Assertions.assertEquals(adLabelDtos.getContent().size(), actual.getContent().size());
        Mockito.verify(advertisementRepository)
            .findByCreator(userEntity.getUsername(), pageable, advertisementEntity.getState());
        Mockito.verifyNoMoreInteractions(advertisementRepository);
    }

    @Test
    public void testChangeStateShouldCallAdvertisementRepositoryIfInputIsValid()
        throws UnknownAdvertisementException {
        // Given
        Mockito.when(advertisementRepository
            .existsByIdAndAndCreator_Username(advertisementEntity.getId(), userEntity.getUsername()))
            .thenReturn(true);
        Mockito.when(adEntityBuilder.queryAdvertisementEntity(advertisementEntity.getId())).thenReturn(
            advertisementEntity);
        // When
        underTest.changeState(advertisementEntity.getId(), AdState.ACTIVE, userEntity.getUsername());
        // Then
        Assertions.assertEquals(advertisementEntity.getState(), AdState.ACTIVE);
        Mockito.verify(advertisementRepository)
            .existsByIdAndAndCreator_Username(advertisementEntity.getId(), userEntity.getUsername());
        Mockito.verify(adEntityBuilder).queryAdvertisementEntity(advertisementEntity.getId());
        Mockito.verify(advertisementRepository).save(advertisementEntity);
        Mockito.verifyNoMoreInteractions(advertisementRepository);
    }

    @Test
    public void testChangeStateShouldThrowUnknownAdvertisementExceptionIfAdNotExistsById()
        throws UnknownAdvertisementException {
        // Given
        Mockito.when(advertisementRepository
            .existsByIdAndAndCreator_Username(advertisementEntity.getId(), userEntity.getUsername()))
            .thenReturn(true);
        Mockito.when(adEntityBuilder.queryAdvertisementEntity(advertisementEntity.getId())).thenThrow(UnknownAdvertisementException.class);
        // When
        Assertions.assertThrows(UnknownAdvertisementException.class,
            () -> underTest.changeState(advertisementEntity.getId(), AdState.ACTIVE, userEntity.getUsername()));
        // Then
        Mockito.verify(advertisementRepository)
            .existsByIdAndAndCreator_Username(advertisementEntity.getId(), userEntity.getUsername());
        Mockito.verify(adEntityBuilder).queryAdvertisementEntity(advertisementEntity.getId());
        Mockito.verifyNoMoreInteractions(advertisementRepository);
    }

}
