package org.example.core.advertising.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.core.advertising.AdUtilService;
import org.example.core.advertising.exception.MaximumSavedAdsReachedException;
import org.example.core.advertising.exception.UnknownAdvertisementException;
import org.example.core.advertising.model.BasicAdDetails;
import org.example.core.advertising.persistence.entity.AdvertisementEntity;
import org.example.core.advertising.persistence.entity.CategoryEntity;
import org.example.core.advertising.persistence.repository.AdvertisementRepository;
import org.example.core.advertising.persistence.repository.CategoryRepository;
import org.example.core.user.exception.UnknownUserException;
import org.example.core.user.persistence.entity.UserEntity;
import org.example.core.user.persistence.repository.UserRepository;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdUtilServiceImpl implements AdUtilService {

    private final AdvertisementRepository advertisementRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;

    @Override
    public List<String> getBrandNamesByCategory(String category) {
        Objects.requireNonNull(category, "Category name cannot be null");
        return advertisementRepository.findBrandNamesByCategory(category);
    }

    @Override
    public List<String> getCarTypesByBrandName(String category, String brandName) {
        Objects.requireNonNull(brandName, "Brand name cannot be null");
        Objects.requireNonNull(category, "Category name cannot be null");
        return advertisementRepository.findCarTypesByCategoryAndBrand(category, brandName);
    }

    @Override
    public List<String> getCategories() {
        return StreamSupport.stream(categoryRepository.findAll().spliterator(), false)
            .map(CategoryEntity::getName)
            .collect(Collectors.toList());
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
        return sortParam;
    }

    @Override
    public Map<Integer, String> getSavedAdvertisementTitlesByUsername(String username) {
        Objects.requireNonNull(username, "Username cannot be null");
        Optional<UserEntity> userEntity = userRepository.findByUsername(username);
        if (userEntity.isEmpty()) {
            return new HashMap<>();
        }
        return userEntity.get().getSavedAds().stream()
            .map(advertisementEntity -> Map.entry(advertisementEntity.getId(), advertisementEntity.getTitle()))
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    @Override
    @Transactional
    public void removeSaveAd(String username, int adId) throws UnknownUserException, UnknownAdvertisementException,
        MaximumSavedAdsReachedException {
        Objects.requireNonNull(username, "Username cannot be null");
        UserEntity userEntity = getUserByName(username);
        log.info("Removing saved Ad from user with username: {}", username);
        modifySaveAds(userEntity, adId, userEntity::removeSavedAd);
    }

    @Override
    @Transactional
    public void addSaveAd(String username, int adId)
        throws UnknownUserException, UnknownAdvertisementException, MaximumSavedAdsReachedException {
        Objects.requireNonNull(username, "Username cannot be null");
        UserEntity userEntity = getUserByName(username);
        log.info("Removing saved Ad from user with username: {}", username);
        modifySaveAds(userEntity, adId, userEntity::addSavedAd);
    }

    protected void modifySaveAds(UserEntity userEntity, int adId, Consumer<AdvertisementEntity> saveAdsModifier) throws
        UnknownAdvertisementException, MaximumSavedAdsReachedException {
        Objects.requireNonNull(userEntity, "Userentity cannot be null");
        Objects.requireNonNull(saveAdsModifier, "Saving ad consumer cannot be null");
        Optional<AdvertisementEntity> advertisementEntity = advertisementRepository.findById(adId);
        log.info("User saved ads: {}", userEntity.getSavedAds());
        if (userEntity.getSavedAds().size() == 15) {
            throw new MaximumSavedAdsReachedException(
                String.format("Maximum saved ads 15 reached for user: %s", userEntity.getUsername()));
        }
        if (advertisementEntity.isPresent()) {
            saveAdsModifier.accept(advertisementEntity.get());
            log.info("Modified advertisement: {}", advertisementEntity);
            log.info("User saved ads after modification : {}", userEntity.getSavedAds());
            userRepository.save(userEntity);
        } else {
            throw new UnknownAdvertisementException(String.format("No ad found %s", adId));
        }

    }

    private UserEntity getUserByName(String username) throws UnknownUserException {
        Objects.requireNonNull(username, "Username cannot be null");
        Optional<UserEntity> userEntity = userRepository.findByUsername(username);
        if (userEntity.isPresent()) {
            return userEntity.get();
        } else {
            throw new UnknownUserException(String.format("Unknown user: %s", username));
        }
    }
}
