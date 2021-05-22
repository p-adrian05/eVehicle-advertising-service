package org.example.core.advertising.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.example.core.advertising.AdvertisementService;
import org.example.core.advertising.exception.UnknownAdvertisementException;
import org.example.core.advertising.exception.UnknownCategoryException;
import org.example.core.advertising.model.AdDetailsDto;
import org.example.core.advertising.model.AdvertisementDto;
import org.example.core.advertising.persistence.AdState;
import org.example.core.advertising.persistence.entity.AdDetailsEntity;
import org.example.core.advertising.persistence.entity.AdvertisementEntity;
import org.example.core.advertising.persistence.entity.BasicAdDetailsEntity;
import org.example.core.advertising.persistence.entity.BrandEntity;
import org.example.core.advertising.persistence.entity.CategoryEntity;
import org.example.core.advertising.persistence.entity.TypeEntity;
import org.example.core.advertising.persistence.repository.AdDetailsRepository;
import org.example.core.advertising.persistence.repository.AdvertisementQueryParams;
import org.example.core.advertising.persistence.repository.AdvertisementRepository;
import org.example.core.advertising.persistence.repository.BasicAdDetailsRepository;
import org.example.core.advertising.persistence.repository.BrandRepository;
import org.example.core.advertising.persistence.repository.CategoryRepository;
import org.example.core.advertising.persistence.repository.TypeRepository;
import org.example.core.image.ImageService;
import org.example.core.image.model.ImageDto;
import org.example.core.security.AuthException;
import org.example.core.user.exception.UnknownUserException;
import org.example.core.user.persistence.entity.UserEntity;
import org.example.core.user.persistence.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Slf4j
public class AdvertisementServiceImpl implements AdvertisementService {


    private final AdvertisementRepository advertisementRepository;
    private final AdDetailsRepository adDetailsRepository;
    private final BasicAdDetailsRepository basicAdDetailsRepository;
    private final ImageService imageService;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final TypeRepository typeRepository;
    private final BrandRepository brandRepository;

    @Override
    @Transactional
    public void createAdvertisement(AdvertisementDto advertisementDto, AdDetailsDto adDetailsDto,
                                    MultipartFile[] images)
        throws UnknownUserException, UnknownCategoryException {
        UserEntity userEntity = queryUserEntity(advertisementDto.getCreator());
        CategoryEntity categoryEntity = queryCategory(advertisementDto.getCategory());
        TypeEntity typeEntity = getTypeEntity(advertisementDto.getType());
        typeEntity.setBrandEntity(getBrandEntity(advertisementDto.getBrand()));

        log.info("Type entity for created advertisement: {}", typeEntity);
        AdvertisementEntity advertisementEntity = AdvertisementEntity.builder()
            .creator(userEntity)
            .category(categoryEntity)
            .type(typeEntity)
            .title(advertisementDto.getTitle())
            .condition(advertisementDto.getCondition())
            .state(AdState.ACTIVE)
            .created(new Timestamp(new Date().getTime()))
            .images(new HashSet<>(
                imageService
                    .createImageEntities(advertisementDto.getImages().stream().map(ImageDto::getPath).collect(
                        Collectors.toList()))))
            .price(advertisementDto.getPrice()).build();

        AdvertisementEntity newAdvertisementEntity = advertisementRepository.save(advertisementEntity);
        log.info("New advertisementEntity: {}", newAdvertisementEntity);

        BasicAdDetailsEntity basicAdDetailsEntity = convertAdDetailsDtoToBasicEntity(adDetailsDto);
        basicAdDetailsEntity.setAdvertisement(newAdvertisementEntity);
        basicAdDetailsRepository.save(basicAdDetailsEntity);

        AdDetailsEntity adDetailsEntity = convertAdDetailsDtoToEntity(adDetailsDto);
        adDetailsEntity.setAdvertisement(newAdvertisementEntity);
        adDetailsRepository.save(adDetailsEntity);

        log.info("New AdDetailsEntity: {}", adDetailsEntity);
    }

    @Override
    @Transactional
    public void updateAdvertisement(AdvertisementDto advertisementDto)
        throws UnknownCategoryException, UnknownAdvertisementException {

        if (!advertisementRepository.existsByIdAndAndCreator_Username(advertisementDto.getId(),
            SecurityContextHolder.getContext().getAuthentication().getName())) {
            throw new AuthException("Access denied");
        }
        AdvertisementEntity advertisementEntity = queryAdvertisementEntity(advertisementDto.getId());
        CategoryEntity categoryEntity = queryCategory(advertisementDto.getCategory());
        TypeEntity typeEntity = getTypeEntity(advertisementDto.getType());
        typeEntity.setBrandEntity(getBrandEntity(advertisementDto.getBrand()));
        log.info("Type entity for updated advertisement: {}", typeEntity);
        advertisementEntity.setCategory(categoryEntity);
        advertisementEntity.setType(typeEntity);
        advertisementEntity.setState(advertisementDto.getState());
        advertisementEntity.setCondition(advertisementDto.getCondition());
        advertisementEntity.setTitle(advertisementDto.getTitle());
        advertisementEntity.setPrice(advertisementDto.getPrice());
        if (advertisementDto.getImages() != null) {
            advertisementEntity
                .setImages(new HashSet<>(imageService.createImageEntities(advertisementDto.getImages().stream().map(
                    ImageDto::getPath).collect(
                    Collectors.toList()))));
        }

        log.info("Updated Advertisement entity: {}", advertisementEntity);

        advertisementRepository.save(advertisementEntity);
    }

    @Override
    public void updateAllAdvertisement(AdvertisementDto advertisementDto, AdDetailsDto adDetailsDto,
                                       MultipartFile[] images)
        throws UnknownCategoryException, UnknownAdvertisementException, UnknownUserException, FileUploadException {

    }

    @Override
    public AdvertisementDto getAdvertisementById(int id) throws UnknownAdvertisementException, UnknownUserException {
      return null;
    }

    @Override
    public AdDetailsDto getAdDetailsById(int id) throws UnknownAdvertisementException {
        return null;
    }

    @Override
    public void updateAdDetails(AdDetailsDto adDetailsDto) throws UnknownAdvertisementException {

    }

    @Override
    public Slice<AdvertisementDto> getAdvertisements(AdvertisementQueryParams params, Pageable pageable) {
        return null;
    }

    @Override
    public List<AdvertisementDto> getSavedAdvertisementsByUsername(String username) throws UnknownUserException {
        return null;
    }

    @Override
    public Page<AdvertisementDto> getAdvertisementsByUsername(String username, Pageable pageable, AdState state)
        throws UnknownUserException {
        return null;
    }

    @Override
    public String convertSortParamToValidForm(String sortParam) {
        return null;
    }

    @Override
    public List<String> getBrandNamesByCategory(String category) {
        return null;
    }

    @Override
    public List<String> getCarTypesByBrandName(String category, String brandName) {
        return null;
    }

    @Override
    public List<String> getCategories() {
        return null;
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

    private UserEntity queryUserEntity(String username) throws UnknownUserException {
        Optional<UserEntity> userEntity = userRepository.findByUsername(username);
        if (userEntity.isEmpty()) {
            throw new UnknownUserException(String.format("User not found: %s", username));
        }
        log.info("Queried user : {}", userEntity.get());
        return userEntity.get();
    }

    public CategoryEntity queryCategory(String categoryName) throws UnknownCategoryException {
        Optional<CategoryEntity> categoryEntity = categoryRepository.findByName(categoryName);
        if (categoryEntity.isEmpty()) {
            throw new UnknownCategoryException(String.format("Category not found: %s", categoryName));
        }
        log.info("Queried category : {}", categoryEntity.get());
        return categoryEntity.get();
    }

    public TypeEntity getTypeEntity(String type) {
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

    public BrandEntity getBrandEntity(String brand) {
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

    private AdDetailsEntity convertAdDetailsDtoToEntity(AdDetailsDto adDetailsDto) {
        return AdDetailsEntity.builder()
            .adId(adDetailsDto.getAdId())
            .maxSpeed(adDetailsDto.getMaxSpeed())
            .range(adDetailsDto.getRange())
            .weight(adDetailsDto.getWeight())
            .accelaration(adDetailsDto.getAccelaration())
            .color(adDetailsDto.getColor())
            .description(adDetailsDto.getDescription())
            .build();
    }

    private BasicAdDetailsEntity convertAdDetailsDtoToBasicEntity(AdDetailsDto adDetailsDto) {
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

    private AdvertisementEntity queryAdvertisementEntity(int id) throws UnknownAdvertisementException {
        Optional<AdvertisementEntity> advertisementEntity = advertisementRepository.findById(id);
        if (advertisementEntity.isEmpty()) {
            throw new UnknownAdvertisementException(String.format("Advertisement not found by id: %s", id));
        }
        log.info("Queried advertisement : {}", advertisementEntity);
        return advertisementEntity.get();
    }
}
