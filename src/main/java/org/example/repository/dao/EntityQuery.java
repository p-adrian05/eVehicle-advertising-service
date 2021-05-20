package org.example.repository.dao;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.exceptions.*;
import org.example.repository.*;
import org.example.repository.entity.*;
import org.example.repository.util.UserMessageId;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.function.Function;


@Slf4j
@RequiredArgsConstructor
@Repository
@PropertySource("classpath:application.properties")
class EntityQuery {

    private final UserRepository userRepository;
    private final AdvertisementRepository advertisementRepository;
    private final RoleRepository roleRepository;
    private final UserDataRepository userDataRepository;
    private final UserMessageRepository userMessageRepository;
    private final TypeRepository typeRepository;
    private final BrandRepository brandRepository;
    private final CategoryRepository categoryRepository;
    private final AdDetailsRepository adDetailsRepository;
    private final ImageRepository imageRepository;

    @Value("${user.default-image_id:1}")
    private String DEFAULT_IMAGE_ID;

    public UserEntity queryUserEntity(String username) throws UnknownUserException {
       return queryUserEntityWithQuery(username,userRepository::findByUsername);
    }
    public int queryUserId(String username) throws UnknownUserException {
        Optional<Integer> userId = userRepository.getIdByUsername(username);
        if(userId.isEmpty()){
            throw new UnknownUserException(String.format("User not found: %s",username));
        }
        log.info("Queried user id: {}",userId.get());
        return userId.get();
    }
    public String queryUsername(int id) throws UnknownUserException {
        Optional<String> username = userRepository.getUsernameById(id);
        if(username.isEmpty()){
            throw new UnknownUserException(String.format("User not found: %s",username));
        }
        log.info("Queried username: {}",username.get());
        return username.get();
    }
    public ImageEntity queryDefaultProfileImage() throws UnknownImageException {
        assert DEFAULT_IMAGE_ID != null;
        Optional<ImageEntity> imageEntity = imageRepository.findById(Integer.valueOf(DEFAULT_IMAGE_ID));
        if(imageEntity.isEmpty()){
            throw new UnknownImageException(String.format("Image not found id: %s",DEFAULT_IMAGE_ID));
        }
        return imageEntity.get();
    }
    public UserEntity queryUserEntityWithImage(String username) throws UnknownUserException {
        return queryUserEntityWithQuery(username,userRepository::findUserEntityByUsernameWithImage);
    }
    public UserEntity queryUserEntityWithRoles(String username) throws UnknownUserException {
        return queryUserEntityWithQuery(username,userRepository::findUserEntityByUsernameWithRoles);
    }
    public UserEntity queryUserEntityWithSavesAds(String username) throws UnknownUserException {
        return queryUserEntityWithQuery(username,userRepository::findUserEntityByUsernameWithSavedAds);
    }
    public UserEntity queryUserEntityByCode(String code) throws UnknownUserException {
        return queryUserEntityWithQuery(code,userRepository::findByActivation);
    }
    public AdvertisementEntity queryAdvertisement(int id) throws UnknownAdvertisementException {
       return queryAdvertisementEntityWithQuery(id,advertisementRepository::findById);
    }
    public AdvertisementEntity queryAdvertisementWithImages(int id) throws UnknownAdvertisementException {
        return queryAdvertisementEntityWithQuery(id,advertisementRepository::findByIdWithImages);
    }
    public AdvertisementEntity queryAdvertisementWithAllData(int id) throws UnknownAdvertisementException {
        return queryAdvertisementEntityWithQuery(id,advertisementRepository::findByIdWithCategoryAndType);
    }
    public AdDetailsEntity queryAdDetails(int id) throws UnknownAdvertisementException {
        return queryAdDetailsWithQuery(id,adDetailsRepository::findById);
    }
    public RoleEntity queryRoleByRoleName(String roleName) throws UnknownRoleException {
        Optional<RoleEntity> roleEntity = roleRepository.findRoleEntityByRoleName(roleName);
        if(roleEntity.isEmpty()){
            throw new UnknownRoleException(String.format("Role not found: %s",roleName));
        }
        return roleEntity.get();
    }

    public UserDataEntity queryUserData(String username) throws UnknownUserException {
        Optional<UserDataEntity> userDataEntity = userDataRepository.findUserDataEntityByUserEntityUsername(username);
        if(userDataEntity.isEmpty()){
            throw new UnknownUserException(String.format("User not found: %s",username));
        }
        log.info("Queried userdata : {}",userDataEntity.get());
        return userDataEntity.get();
    }

    public UserMessageEntity queryUserMessage(UserMessageId userMessageId) throws UnknownMessageException {
        Optional<UserMessageEntity> userMessageEntity = userMessageRepository
                .findById(userMessageId);
        if(userMessageEntity.isEmpty()){
            throw new UnknownMessageException(String.format("Message not found %s", userMessageId));
        }
        log.info("Queried user message : {}",userMessageEntity.get());
        return userMessageEntity.get();
    }

    public BrandEntity getBrandEntity(String brand){
        Optional<BrandEntity> brandEntity = brandRepository.findById(brand);
        if(brandEntity.isPresent()){
            log.info("brandEntity by name : {},{}",brand,brandEntity.get());
            return brandEntity.get();
        }
        BrandEntity newBrandEntity = BrandEntity.builder().brandName(brand).build();
        newBrandEntity = brandRepository.save(newBrandEntity);
        log.info("Created brandEntity by name : {},{}",brand,newBrandEntity);
        return newBrandEntity;
    }
    public TypeEntity getTypeEntity(String type){
        Optional<TypeEntity> typeEntity = typeRepository.findByName(type);
        if(typeEntity.isPresent()){
            log.info("Created typeEntity by name : {},{}",type,typeEntity.get());
            return typeEntity.get();
        }
        TypeEntity newTypeEntity = TypeEntity.builder().name(type).build();
        newTypeEntity = typeRepository.save(newTypeEntity);
        log.info("Created typeEntity by name : {},{}",type,newTypeEntity);
        return newTypeEntity;
    }

    public CategoryEntity queryCategory(String categoryName) throws UnknownCategoryException {
        Optional<CategoryEntity> categoryEntity = categoryRepository.findByName(categoryName);
        if(categoryEntity.isEmpty()){
            throw new UnknownCategoryException(String.format("Category not found: %s",categoryName));
        }
        log.info("Queried category : {}",categoryEntity.get());
        return categoryEntity.get();
    }

    public AdDetailsEntity queryAdDetailsWithQuery(int id, Function<Integer,Optional<AdDetailsEntity>> query) throws UnknownAdvertisementException {
        Optional<AdDetailsEntity> adDetailsEntity = query.apply(id);
        if (adDetailsEntity.isEmpty()) {
            log.info("ProductDetails entity by id: {},{}", id, adDetailsEntity);
            throw new UnknownAdvertisementException(String.format("Advertisement not found by id: %s", id));
        }
        return adDetailsEntity.get();
    }
    private UserEntity queryUserEntityWithQuery(String username, Function<String,Optional<UserEntity>> query) throws UnknownUserException {
        Optional<UserEntity> userEntity = query.apply(username);
        if(userEntity.isEmpty()){
            throw new UnknownUserException(String.format("User not found: %s",username));
        }
        log.info("Queried user : {}",userEntity.get());
        return userEntity.get();
    }
    private AdvertisementEntity queryAdvertisementEntityWithQuery(int id, Function<Integer,Optional<AdvertisementEntity>> query) throws UnknownAdvertisementException {
        Optional<AdvertisementEntity> advertisementEntity = query.apply(id);
        if(advertisementEntity.isEmpty()){
            throw new UnknownAdvertisementException(String.format("Advertisement not found by id: %s",id));
        }
        log.info("Queried advertisement : {}",advertisementEntity);
        return advertisementEntity.get();
    }
}
