package org.example.core.advertising.impl;

import lombok.RequiredArgsConstructor;
import org.example.core.advertising.AdVehicleService;
import org.example.core.advertising.model.BasicAdDetails;
import org.example.core.advertising.persistence.entity.CategoryEntity;
import org.example.core.advertising.persistence.repository.AdvertisementRepository;
import org.example.core.advertising.persistence.repository.CategoryRepository;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
public class AdVehicleServiceImpl implements AdVehicleService {

    private final AdvertisementRepository advertisementRepository;
    private final CategoryRepository categoryRepository;

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

}
