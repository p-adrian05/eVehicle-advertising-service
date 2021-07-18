package org.example.core.advertising.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.core.advertising.exception.UnknownCategoryException;
import org.example.core.advertising.persistence.entity.BrandEntity;
import org.example.core.advertising.persistence.entity.CategoryEntity;
import org.example.core.advertising.persistence.entity.TypeEntity;
import org.example.core.advertising.persistence.repository.BrandRepository;
import org.example.core.advertising.persistence.repository.CategoryRepository;
import org.example.core.advertising.persistence.repository.TypeRepository;
import org.example.core.user.exception.UnknownUserException;
import org.example.core.user.persistence.entity.UserEntity;
import org.example.core.user.persistence.repository.UserRepository;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.Optional;

@RequiredArgsConstructor
@Component
@Slf4j
class AdUtil {

    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final TypeRepository typeRepository;
    private final BrandRepository brandRepository;

    UserEntity queryUserEntity(String username) throws UnknownUserException {
        Objects.requireNonNull(username, "Username cannot be null for user query");
        Optional<UserEntity> userEntity = userRepository.findByUsername(username);
        if (userEntity.isEmpty()) {
            throw new UnknownUserException(String.format("User not found: %s", username));
        }
        log.info("Queried user : {}", userEntity.get());
        return userEntity.get();
    }

    CategoryEntity queryCategory(String categoryName) throws UnknownCategoryException {
        Objects.requireNonNull(categoryName, "Category name cannot be null for category query");
        Optional<CategoryEntity> categoryEntity = categoryRepository.findByName(categoryName);
        if (categoryEntity.isEmpty()) {
            throw new UnknownCategoryException(String.format("Category not found: %s", categoryName));
        }
        log.info("Queried category : {}", categoryEntity.get());
        return categoryEntity.get();
    }

    TypeEntity getTypeEntity(String type) {
        Objects.requireNonNull(type, "Type name cannot be null for type query");
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

    BrandEntity getBrandEntity(String brand) {
        Objects.requireNonNull(brand, "Brand name cannot be null for brand query");
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
}
