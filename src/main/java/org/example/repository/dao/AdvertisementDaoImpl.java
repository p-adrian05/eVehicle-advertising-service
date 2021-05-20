package org.example.repository.dao;


import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.exceptions.AuthException;
import org.example.repository.util.AdvertisementQueryParams;
import org.example.exceptions.UnknownAdvertisementException;
import org.example.exceptions.UnknownCategoryException;
import org.example.exceptions.UnknownUserException;
import org.example.model.AdDetails;
import org.example.model.Image;
import org.example.model.Advertisement;
import org.example.repository.*;
import org.example.repository.entity.*;
import org.example.repository.util.AdState;
import org.example.repository.util.ModelEntityConverter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Slf4j
@RequiredArgsConstructor
@Repository
public class AdvertisementDaoImpl implements AdvertisementDao{

    private final AdvertisementRepository advertisementRepository;
    private final AdDetailsRepository adDetailsRepository;
    private final BasicAdDetailsRepository basicAdDetailsRepository;
    private final ImageRepository imageRepository;
    private final CategoryRepository categoryRepository;
    private final EntityQuery entityQuery;

    @Override
    @Transactional
    public int createAdvertisement(@NonNull Advertisement advertisement,@NonNull AdDetails adDetails) throws UnknownUserException, UnknownCategoryException {
        UserEntity userEntity = entityQuery.queryUserEntity(advertisement.getCreator());
        CategoryEntity categoryEntity = entityQuery.queryCategory(advertisement.getCategory());
        TypeEntity typeEntity = entityQuery.getTypeEntity(advertisement.getType());
        typeEntity.setBrandEntity(entityQuery.getBrandEntity(advertisement.getBrand()));

        log.info("Type entity for created advertisement: {}",typeEntity);
        AdvertisementEntity advertisementEntity = AdvertisementEntity.builder()
                .creator(userEntity)
                .category(categoryEntity)
                .type(typeEntity)
                .title(advertisement.getTitle())
                .condition(advertisement.getCondition())
                .state(AdState.ACTIVE)
                .created(new Timestamp(new Date().getTime()))
                .images(new HashSet<>(crateAndSaveImageEntities(advertisement.getImages())))
                .price(advertisement.getPrice()).build();

        AdvertisementEntity newAdvertisementEntity = advertisementRepository.save(advertisementEntity);
        log.info("New advertisementEntity: {}",newAdvertisementEntity);

        BasicAdDetailsEntity basicAdDetailsEntity = BasicAdDetailsEntity.of(adDetails);
        basicAdDetailsEntity.setAdvertisement(newAdvertisementEntity);
        basicAdDetailsRepository.save(basicAdDetailsEntity);

        AdDetailsEntity adDetailsEntity = AdDetailsEntity.of(adDetails);
        adDetailsEntity.setAdvertisement(newAdvertisementEntity);
        adDetailsRepository.save(adDetailsEntity);

        log.info("New AdDetailsEntity: {}",adDetailsEntity);
        return newAdvertisementEntity.getId();
    }


    @Override
    @Transactional
    public void deleteAdvertisement(int id) throws UnknownAdvertisementException {
        if(!advertisementRepository.existsByIdAndAndCreator_Username(id,SecurityContextHolder.getContext().getAuthentication().getName())){
            throw new AuthException("Access denied");
        }
        AdvertisementEntity advertisementEntity = entityQuery.queryAdvertisement(id);
       advertisementEntity.setState(AdState.ARCHIVED);
       advertisementRepository.save(advertisementEntity);
    }

    @Override
    @Transactional
    public void updateAdvertisement(@NonNull Advertisement advertisement) throws UnknownCategoryException, UnknownAdvertisementException {
        if(!advertisementRepository.existsByIdAndAndCreator_Username(advertisement.getId(),SecurityContextHolder.getContext().getAuthentication().getName())){
            throw new AuthException("Access denied");
        }
        AdvertisementEntity advertisementEntity = entityQuery.queryAdvertisementWithAllData(advertisement.getId());
        CategoryEntity categoryEntity = entityQuery.queryCategory(advertisement.getCategory());
        TypeEntity typeEntity = entityQuery.getTypeEntity(advertisement.getType());
        typeEntity.setBrandEntity(entityQuery.getBrandEntity(advertisement.getBrand()));
        log.info("Type entity for updated advertisement: {}",typeEntity);
        advertisementEntity.setCategory(categoryEntity);
        advertisementEntity.setType(typeEntity);
        advertisementEntity.setState(advertisement.getState());
        advertisementEntity.setCondition(advertisement.getCondition());
        advertisementEntity.setTitle(advertisement.getTitle());
        advertisementEntity.setPrice(advertisement.getPrice());
        List<ImageEntity> imageEntities = new LinkedList<>();
        if(advertisement.getImages()!= null){
            advertisement.getImages().forEach(image -> {
                if(imageRepository.existsByPath(image.getPath())){
                    imageEntities.add(imageRepository.findByPath(image.getPath()));
                }else{
                    imageEntities.add(imageRepository.save(ImageEntity.builder().path(image.getPath())
                            .uploadedTime(image.getUploadedTime()).build()));
                }
            });
            advertisementEntity.setImages(new HashSet<>(imageEntities));
        }

        log.info("Updated Advertisement entity: {}",advertisementEntity);

        advertisementRepository.save(advertisementEntity);
    }
    protected List<ImageEntity> crateAndSaveImageEntities(List<Image> adImages){
        if(adImages!=null){
            List<ImageEntity> adImageEntities = adImages.stream()
                    .map(image ->{
                       ImageEntity newImageEntity = ModelEntityConverter.convertImageToImageEntity(image);
                       newImageEntity.setId(0);
                        return  imageRepository.save(newImageEntity);})
                    .collect(Collectors.toList());
            log.info("Created AdImage entities: {}",adImageEntities);
            return adImageEntities;
        }
        return new LinkedList<>();
    }
    @Override
    public Advertisement getAdvertisementById(int id) throws UnknownAdvertisementException, UnknownUserException {
        AdvertisementEntity advertisementEntity = entityQuery.queryAdvertisementWithAllData(id);
        log.info("AdvertisementEntity entity by id: {},{}",id,advertisementEntity);

        Advertisement advertisement = ModelEntityConverter.convertAdvertisementEntityToModel(advertisementEntity);
        advertisement.setCreator(entityQuery.queryUsername(advertisementEntity.getCreator().getId()));
        advertisement.setCategory(advertisementEntity.getCategory().getName());
        return advertisement;
    }
    @Override
    public Slice<Advertisement> getAdvertisements(AdvertisementQueryParams params, Pageable pageable) {
        return advertisementRepository.findByCategory(params,pageable)
                .map(ModelEntityConverter::convertAdvertisementEntityToModel);
    }

    @Override
    public AdDetails getAdDetailsById(int id) throws UnknownAdvertisementException {
        Optional<BasicAdDetailsEntity> basicAdDetailsEntity = basicAdDetailsRepository.findById(id);
        Optional<AdDetailsEntity> adDetailsEntity = adDetailsRepository.findById(id);
        if(basicAdDetailsEntity.isPresent() && adDetailsEntity.isPresent()){
           return AdDetails.builder()
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
                    .build();
        }
        throw new UnknownAdvertisementException(String.format("No advertisement found by id : %s",id));

    }

    @Override
    @Transactional
    public void updateAdDetails(@NonNull AdDetails adDetails) throws UnknownAdvertisementException {
        if(!advertisementRepository.existsByIdAndAndCreator_Username(adDetails.getAdId(),SecurityContextHolder.getContext().getAuthentication().getName())){
            throw new AuthException("Access denied");
        }
        if(!adDetailsRepository.existsById(adDetails.getAdId())){
            throw new UnknownAdvertisementException(String.format("Advertisement not found by id: %s",adDetails.getAdId()));
        }
        adDetailsRepository.save(ModelEntityConverter.convertAdDetailsToAdDetailsEntity(adDetails));
        basicAdDetailsRepository.save(ModelEntityConverter.convertAdDetailsToBasicAdDetailsEntity(adDetails));
        log.info("Updated AdDetails: {}",adDetails);
    }

    @Override
    public List<Advertisement> getSavedAdvertisementsByUsername(String username) throws UnknownUserException {
        return entityQuery.queryUserEntityWithSavesAds(username).getSavedAds().stream()
                .map(e->Advertisement.builder().id(e.getId()).title(e.getTitle()).build()).collect(Collectors.toList());
    }
    @Override
    public List<String> getBrandNamesByCategory(String category) {
        return advertisementRepository.findBrandNamesByCategory(category);
    }
    @Override
    public List<String> getTypesByBrandName(String category,String brandName) {
        return advertisementRepository.findCarTypesByCategoryAndBrand(category,brandName);
    }
    @Override
    public List<String> getCategories() {
        return StreamSupport.stream(categoryRepository.findAll().spliterator(),false)
                .map(CategoryEntity::getName)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void updateAllAdvertisement(Advertisement advertisement, AdDetails adDetails) throws UnknownAdvertisementException, UnknownCategoryException {
        this.updateAdDetails(adDetails);
        this.updateAdvertisement(advertisement);
    }

    @Override
    public void changeState(int adId, AdState stateToChange, String creatorName) throws UnknownAdvertisementException {
        if(!advertisementRepository.existsByIdAndAndCreator_Username(adId,creatorName)){
            throw new AuthException("Access denied");
        }
        AdvertisementEntity advertisementEntity = entityQuery.queryAdvertisement(adId);
        log.info(String.format("Changing advertisement id: %s  state from %s to %s",adId,advertisementEntity.getState(),stateToChange));
        advertisementEntity.setState(stateToChange);
        advertisementRepository.save(advertisementEntity);
    }

    @Override
    public Page<Advertisement> getAdvertisementsByUsername(String username,Pageable pageable,AdState state) throws UnknownUserException {
        int userId  = entityQuery.queryUserId(username);
        return advertisementRepository.findByCreator(userId,pageable,state)
                .map(ModelEntityConverter::convertAdvertisementEntityToModel);
    }
    @Override
    @Transactional
    public void addImages(int adId, List<Image> images) throws UnknownAdvertisementException {
        if(!advertisementRepository.existsByIdAndAndCreator_Username(adId,SecurityContextHolder.getContext().getAuthentication().getName())){
            throw new AuthException("Access denied");
        }
        AdvertisementEntity advertisementEntity = entityQuery.queryAdvertisementWithImages(adId);
        log.info("Adding new images to advertisementEntity: {}",advertisementEntity);
        log.info("Advertisement images: {}",advertisementEntity.getImages());
        for(ImageEntity imageEntity: crateAndSaveImageEntities(images)){
            advertisementEntity.addImage(imageEntity);
        }
        advertisementRepository.save(advertisementEntity);
        log.info("Advertisement images after modification : {}",advertisementEntity.getImages());
    }
    @Override
    @Transactional
    public void removeImages(int adId, List<String> imagePaths) throws UnknownAdvertisementException {
        if(!advertisementRepository.existsByIdAndAndCreator_Username(adId,SecurityContextHolder.getContext().getAuthentication().getName())){
            throw new AuthException("Access denied");
        }
        AdvertisementEntity advertisementEntity = entityQuery.queryAdvertisementWithImages(adId);
        log.info("Removing images from advertisementEntity: {}",advertisementEntity);
        log.info("Advertisement images: {}",advertisementEntity.getImages());
        List<ImageEntity> imageEntities = imageRepository.findImageEntitiesByPathIsIn(imagePaths);
        for(ImageEntity imageEntity: imageEntities){
            advertisementEntity.removeImage(imageEntity);
        }
        log.info("Removed images: {}",advertisementEntity.getImages());
        advertisementRepository.save(advertisementEntity);
        imageRepository.deleteAll(imageEntities);
        log.info("Advertisement images after modification : {}",advertisementEntity.getImages());
    }
}
