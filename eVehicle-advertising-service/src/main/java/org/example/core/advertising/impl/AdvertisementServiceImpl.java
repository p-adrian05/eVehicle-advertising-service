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
import org.example.core.image.ImageService;
import org.example.core.security.AuthException;
import org.example.core.storage.AdImageStorageService;
import org.example.core.storage.StorageService;
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
import java.nio.file.Path;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Slf4j
public class AdvertisementServiceImpl implements AdvertisementService {


    private final AdvertisementRepository advertisementRepository;
    private final ImageService imageService;
    private final UserRepository userRepository;
    private final StorageService storageService;
    private final AdImageStorageService adImageStorageService;
    private final AdUtil adUtil;
    private final AdDetailsService adDetailsService;

    @Override
    @Transactional
    public void createAdvertisement(CreateAdDto advertisementDto, AdDetailsDto adDetailsDto,
                                    MultipartFile[] imageFiles)
        throws UnknownUserException, UnknownCategoryException, FileUploadException {

        AdvertisementEntity advertisementEntity = createNewAdvertisement(advertisementDto);
        Set<Path> imagePaths = adImageStorageService.store(imageFiles);
        advertisementEntity.setImages(
            imageService.createImageEntities(imagePaths));
        AdvertisementEntity newAdvertisementEntity = advertisementRepository.save(advertisementEntity);
        log.info("New advertisementEntity: {}", newAdvertisementEntity);

        adDetailsService.createAdDetails(adDetailsDto, newAdvertisementEntity);
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


    private void updateAdvertisement(AdvertisementDto advertisementDto)
        throws UnknownCategoryException, UnknownAdvertisementException {
        AdvertisementEntity advertisementEntity = queryAdvertisementEntity(advertisementDto.getId());
        CategoryEntity categoryEntity = adUtil.queryCategory(advertisementDto.getCategory());
        TypeEntity typeEntity = adUtil.getTypeEntity(advertisementDto.getType());
        typeEntity.setBrandEntity(adUtil.getBrandEntity(advertisementDto.getBrand()));
        log.info("Type entity for updated advertisement: {}", typeEntity);
        advertisementEntity.setCategory(categoryEntity);
        advertisementEntity.setType(typeEntity);
        advertisementEntity.setProductCondition(advertisementDto.getCondition());
        advertisementEntity.setTitle(advertisementDto.getTitle());
        advertisementEntity.setPrice(advertisementDto.getPrice());
        if (advertisementDto.getImagePaths() != null) {
            advertisementEntity
                .setImages(
                    imageService.createImageEntities(advertisementDto.getImagePaths().stream().map(Path::of).collect(
                        Collectors.toSet())));
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

        Set<String> imagePaths = new HashSet<>();
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
        adDetailsService.updateAdDetails(adDetails);
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
        return Optional.of(adUtil.convertAdvertisementEntityToDto(advertisementEntity.get()));
    }

    @Override
    public Optional<AdDetailsDto> getAdDetailsById(int id) {
        return adDetailsService.getAdDetailsById(id);
    }


    @Override
    public Slice<AdLabelDto> getAdvertisements(AdvertisementQueryParams params, Pageable pageable) {
        return advertisementRepository.findByParams(params, pageable)
            .map(adUtil::convertAdvertisementEntityToLabelDto);
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
            .map(adUtil::convertAdvertisementEntityToLabelDto);
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
            .price(advertisementDto.getPrice()).build();
    }
}
