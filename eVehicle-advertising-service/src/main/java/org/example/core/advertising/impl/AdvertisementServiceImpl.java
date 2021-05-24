package org.example.core.advertising.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.FileUploadException;
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
import org.example.core.image.StorageService;
import org.example.core.image.model.ImageDto;
import org.example.core.image.persistence.entity.ImageEntity;
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
import java.lang.reflect.Field;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

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
    private final StorageService storageService;

    @Override
    @Transactional
    public void createAdvertisement(CreateAdDto advertisementDto, AdDetailsDto adDetailsDto,
                                    MultipartFile[] imageFiles)
        throws UnknownUserException, UnknownCategoryException, FileUploadException {
        UserEntity userEntity = queryUserEntity(advertisementDto.getCreator());
        CategoryEntity categoryEntity = queryCategory(advertisementDto.getCategory());
        TypeEntity typeEntity = getTypeEntity(advertisementDto.getType());
        typeEntity.setBrandEntity(getBrandEntity(advertisementDto.getBrand()));

        String folderName = "";
        Map<String, MultipartFile> filesToSave = new HashMap<>();
        Set<String> imageModels = new HashSet<>();
        String[] pathValues;

        for (Map.Entry<MultipartFile, String> entry : createImagePaths(imageFiles).entrySet()) {
            pathValues = getFolderNameAndFilenameFromPath(entry.getValue());
            if (folderName.equals("")) {
                folderName = pathValues[0];
            }
            imageModels.add(entry.getValue());
            filesToSave.put(pathValues[1], entry.getKey());
        }

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
                    .createImageEntities(new ArrayList<>(imageModels))))
            .price(advertisementDto.getPrice()).build();

        AdvertisementEntity newAdvertisementEntity = advertisementRepository.save(advertisementEntity);
        log.info("New advertisementEntity: {}", newAdvertisementEntity);

        BasicAdDetailsEntity basicAdDetailsEntity = convertAdDetailsDtoToBasicEntity(adDetailsDto);
        basicAdDetailsEntity.setAdvertisement(newAdvertisementEntity);
        basicAdDetailsRepository.save(basicAdDetailsEntity);

        AdDetailsEntity adDetailsEntity = convertAdDetailsDtoToEntity(adDetailsDto);
        adDetailsEntity.setAdvertisement(newAdvertisementEntity);
        adDetailsRepository.save(adDetailsEntity);

        storageService.store(filesToSave, folderName);

        log.info("New AdDetailsEntity: {}", adDetailsEntity);
    }

    private String[] getFolderNameAndFilenameFromPath(String path) {
        String[] pathArray = path.split("/");
        if (pathArray.length >= 2) {
            return new String[] {pathArray[pathArray.length - 2], pathArray[pathArray.length - 1]};
        }
        return new String[] {"", ""};
    }

    private Map<MultipartFile, String> createImagePaths(MultipartFile[] imageFiles) {
        String folderName = storageService.generateFolderName();
        return Arrays.stream(imageFiles)
            .map(image -> Map.entry(image, "/" + folderName + "/" + generateAdImageName("jpg"))).collect(
                Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    private String generateAdImageName(String extension) {
        StringBuilder name = new StringBuilder();
        name.append("ad");
        name.append(UUID.randomUUID());
        name.append(".");
        name.append(extension);
        return name.toString();
    }

    @Override
    public String convertSortParamToValidForm(String sortParam) {
        Class<?> c = BasicAdDetails.class;
        Field[] fields = c.getDeclaredFields();
        String[] classNames = BasicAdDetails.class.getName().split("\\.");
        String className = classNames[classNames.length - 1];
        className = String.valueOf(className.charAt(0)).toLowerCase() + className.substring(1);
        List<String> fieldsName = new LinkedList<>();
        for (Field field : fields) {
            fieldsName.add(field.getName());
        }
        if (fieldsName.contains(sortParam)) {
            return className + "." + sortParam;
        }
        System.out.println(sortParam);
        return sortParam;
    }

    private void updateAdvertisement(AdvertisementDto advertisementDto)
        throws UnknownCategoryException, UnknownAdvertisementException {
        AdvertisementEntity advertisementEntity = queryAdvertisementEntity(advertisementDto.getId());
        CategoryEntity categoryEntity = queryCategory(advertisementDto.getCategory());
        TypeEntity typeEntity = getTypeEntity(advertisementDto.getType());
        typeEntity.setBrandEntity(getBrandEntity(advertisementDto.getBrand()));
        log.info("Type entity for updated advertisement: {}", typeEntity);
        advertisementEntity.setCategory(categoryEntity);
        advertisementEntity.setType(typeEntity);
        advertisementEntity.setCondition(advertisementDto.getCondition());
        advertisementEntity.setTitle(advertisementDto.getTitle());
        advertisementEntity.setPrice(advertisementDto.getPrice());
        if (advertisementDto.getImagePaths() != null) {
            advertisementEntity
                .setImages(new HashSet<>(imageService.createImageEntities(advertisementDto.getImagePaths())));
        }

        log.info("Updated Advertisement entity: {}", advertisementEntity);

        advertisementRepository.save(advertisementEntity);
    }

    @Override
    @Transactional
    public void updateAdvertisementWithDetails(UpdateAdvertisementDto advertisement, AdDetailsDto adDetails,
                                               MultipartFile[] imageFiles)
        throws UnknownAdvertisementException, UnknownCategoryException, FileUploadException {

        if (!advertisementRepository.existsByIdAndAndCreator_Username(advertisement.getId(),
            SecurityContextHolder.getContext().getAuthentication().getName())) {
            throw new AuthException("Access denied");
        }

        Set<String> imageFileNames =
            Arrays.stream(imageFiles).map(MultipartFile::getOriginalFilename).collect(Collectors.toSet());

        Optional<AdvertisementDto> advertisementDto = getAdvertisementById(advertisement.getId());
        if (advertisementDto.isEmpty()) {
            throw new UnknownAdvertisementException("Unkown ad");
        }
        Set<String> originalImagesPaths = new HashSet<>(advertisementDto.get().getImagePaths());

        Set<String> toDeleteImagePaths = originalImagesPaths.stream()
            .filter(image -> !imageFileNames.contains(image)).collect(Collectors.toSet());

        Set<String> newImageNames =
            imageFileNames.stream().filter(name -> !originalImagesPaths.contains(name)).collect(Collectors.toSet());

        List<String> imagePaths = new LinkedList<>();
        String folderName = "";
        String[] pathValues;
        Map<String, MultipartFile> filesToSave = new HashMap<>();
        Map<MultipartFile, String> newImageFilePathPairs = createImagePaths(Arrays.stream(imageFiles)
            .filter(imageFile -> newImageNames.contains(imageFile.getOriginalFilename()))
            .toArray(MultipartFile[]::new));

        for (Map.Entry<MultipartFile, String> entry : newImageFilePathPairs.entrySet()) {
            pathValues = getFolderNameAndFilenameFromPath(entry.getValue());
            if (folderName.equals("")) {
                folderName = pathValues[0];
            }
            imagePaths.add(entry.getValue());
            filesToSave.put(pathValues[1], entry.getKey());
        }
        Arrays.stream(imageFiles)
            .filter(imageFile -> !newImageNames.contains(imageFile.getOriginalFilename()))
            .forEach(imageFile -> imagePaths.add(imageFile.getOriginalFilename()));

        AdvertisementDto newAdvertisementDto = AdvertisementDto.builder()
            .brand(advertisement.getBrand())
            .category(advertisement.getCategory())
            .condition(advertisement.getCondition())
            .id(advertisement.getId())
            .imagePaths(imagePaths)
            .price(advertisement.getPrice())
            .title(advertisement.getTitle())
            .type(advertisement.getType())
            .build();

        storageService.store(filesToSave, folderName);
        deleteImageFiles(toDeleteImagePaths);
        this.updateAdDetails(adDetails);
        this.updateAdvertisement(newAdvertisementDto);
    }

    private void deleteImageFiles(Collection<String> toDeletedImageModels) {
        toDeletedImageModels.forEach(imagePath -> storageService.deleteByPath("images" + imagePath));
    }

    @Override
    public Optional<AdvertisementDto> getAdvertisementById(int id) {
        Optional<AdvertisementEntity> advertisementEntity = advertisementRepository.findByIdWithCategoryAndType(id);
        log.info("AdvertisementEntity entity by id: {},{}", id, advertisementEntity);
        if (advertisementEntity.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(convertAdvertisementEntityToDto(advertisementEntity.get()));
    }

    @Override
    public Optional<AdDetailsDto> getAdDetailsById(int id) {
        Optional<BasicAdDetailsEntity> basicAdDetailsEntity = basicAdDetailsRepository.findById(id);
        Optional<AdDetailsEntity> adDetailsEntity = adDetailsRepository.findById(id);
        if (basicAdDetailsEntity.isPresent() && adDetailsEntity.isPresent()) {
            return Optional.of(AdDetailsDto.builder()
                .weight(adDetailsEntity.get().getWeight())
                .maxSpeed(adDetailsEntity.get().getMaxSpeed())
                .range(adDetailsEntity.get().getRange())
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
    private void updateAdDetails(AdDetailsDto adDetailsDto) throws UnknownAdvertisementException {
        if (!adDetailsRepository.existsById(adDetailsDto.getAdId())) {
            throw new UnknownAdvertisementException(
                String.format("Advertisement not found by id: %s", adDetailsDto.getAdId()));
        }
        adDetailsRepository.save(convertAdDetailsToAdDetailsEntity(adDetailsDto));
        basicAdDetailsRepository.save(convertAdDetailsToBasicAdDetailsEntity(adDetailsDto));
        log.info("Updated AdDetails: {}", adDetailsDto);
    }

    @Override
    public Slice<AdLabelDto> getAdvertisements(AdvertisementQueryParams params, Pageable pageable) {
        return advertisementRepository.findByParams(params, pageable)
            .map(this::convertAdvertisementEntityToLabelDto);
    }

    @Override
    public Map<Integer, String> getSavedAdvertisementTitlesByUsername(String username) {
        Optional<UserEntity> userEntity = userRepository.findByUsername(username);
        if (userEntity.isEmpty()) {
            return new HashMap<>();
        }
        return userEntity.get().getSavedAds().stream()
            .map(advertisementEntity -> Map.entry(advertisementEntity.getId(), advertisementEntity.getTitle()))
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    @Override
    public Page<AdLabelDto> getAdvertisementsByUsername(String username, Pageable pageable, AdState state) {
        return advertisementRepository.findByCreator(username, pageable, state)
            .map(this::convertAdvertisementEntityToLabelDto);
    }

    @Override
    public List<String> getBrandNamesByCategory(String category) {
        return advertisementRepository.findBrandNamesByCategory(category);
    }

    @Override
    public List<String> getCarTypesByBrandName(String category, String brandName) {
        return advertisementRepository.findCarTypesByCategoryAndBrand(category, brandName);
    }

    @Override
    public List<String> getCategories() {
        return StreamSupport.stream(categoryRepository.findAll().spliterator(), false)
            .map(CategoryEntity::getName)
            .collect(Collectors.toList());
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

    private AdLabelDto convertAdvertisementEntityToLabelDto(AdvertisementEntity advertisementEntity) {
        return AdLabelDto.builder()
            .id(advertisementEntity.getId())
            .price(advertisementEntity.getPrice())
            .brand(advertisementEntity.getType().getBrandEntity().getBrandName())
            .condition(advertisementEntity.getCondition())
            .created(advertisementEntity.getCreated())
            .title(advertisementEntity.getTitle())
            .state(advertisementEntity.getState())
            .type(advertisementEntity.getType().getName())
            .imagePaths(advertisementEntity.getImages().stream()
                .map(ImageEntity::getPath).collect(Collectors.toList()))
            .basicAdDetails(convertBasicAdDetailsEntityToModel(advertisementEntity.getBasicAdDetails()))
            .build();
    }

    public BasicAdDetails convertBasicAdDetailsEntityToModel(BasicAdDetailsEntity basicAdDetailsEntity) {
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

    public static AdvertisementDto convertAdvertisementEntityToDto(AdvertisementEntity advertisementEntity) {
        return AdvertisementDto.builder()
            .id(advertisementEntity.getId())
            .price(advertisementEntity.getPrice())
            .creator(advertisementEntity.getCreator().getUsername())
            .category(advertisementEntity.getCategory().getName())
            .brand(advertisementEntity.getType().getBrandEntity().getBrandName())
            .condition(advertisementEntity.getCondition())
            .created(advertisementEntity.getCreated())
            .title(advertisementEntity.getTitle())
            .state(advertisementEntity.getState())
            .type(advertisementEntity.getType().getName())
            .imagePaths(advertisementEntity.getImages().stream()
                .map(ImageEntity::getPath).collect(Collectors.toList()))
            .build();
    }

    public ImageDto convertImageEntityToModel(ImageEntity imageEntity) {
        return ImageDto.builder()
            .id(imageEntity.getId())
            .path(imageEntity.getPath())
            .uploadedTime(imageEntity.getUploadedTime())
            .build();
    }

    public AdDetailsDto convertAdDetailsEntityToModel(AdDetailsEntity adDetailsEntity) {
        return AdDetailsDto.builder()
            .adId(adDetailsEntity.getAdId())
            .description(adDetailsEntity.getDescription())
            .color(adDetailsEntity.getColor())
            .accelaration(adDetailsEntity.getAccelaration())
            .range(adDetailsEntity.getRange())
            .maxSpeed(adDetailsEntity.getMaxSpeed())
            .weight(adDetailsEntity.getWeight())
            .build();
    }

    public static AdDetailsEntity convertAdDetailsToAdDetailsEntity(AdDetailsDto adDetails) {
        return AdDetailsEntity.builder()
            .adId(adDetails.getAdId())
            .maxSpeed(adDetails.getMaxSpeed())
            .range(adDetails.getRange())
            .weight(adDetails.getWeight())
            .accelaration(adDetails.getAccelaration())
            .color(adDetails.getColor())
            .description(adDetails.getDescription())
            .build();
    }

    public static BasicAdDetailsEntity convertAdDetailsToBasicAdDetailsEntity(AdDetailsDto adDetails) {
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

    private AdvertisementEntity queryAdvertisementEntity(int id) throws UnknownAdvertisementException {
        Optional<AdvertisementEntity> advertisementEntity = advertisementRepository.findById(id);
        if (advertisementEntity.isEmpty()) {
            throw new UnknownAdvertisementException(String.format("Advertisement not found by id: %s", id));
        }
        log.info("Queried advertisement : {}", advertisementEntity);
        return advertisementEntity.get();
    }
}
