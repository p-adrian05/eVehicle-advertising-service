package org.example.repository.dao;

import org.example.repository.util.AdState;
import org.example.repository.util.AdvertisementQueryParams;
import org.example.exceptions.UnknownAdvertisementException;
import org.example.exceptions.UnknownCategoryException;
import org.example.exceptions.UnknownUserException;
import org.example.model.AdDetails;
import org.example.model.Image;
import org.example.model.Advertisement;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import java.util.List;

public interface AdvertisementDao {

    int createAdvertisement(Advertisement advertisement, AdDetails adDetails) throws UnknownUserException, UnknownCategoryException;

    void deleteAdvertisement(int id) throws UnknownAdvertisementException;

    void updateAdvertisement(Advertisement advertisement) throws UnknownCategoryException, UnknownAdvertisementException;

    Advertisement getAdvertisementById(int id) throws UnknownAdvertisementException, UnknownUserException;

    AdDetails getAdDetailsById(int id) throws UnknownAdvertisementException;

    void updateAdDetails(AdDetails adDetails) throws UnknownAdvertisementException;

    Slice<Advertisement> getAdvertisements(AdvertisementQueryParams params, Pageable pageable);

    List<Advertisement> getSavedAdvertisementsByUsername(String username) throws UnknownUserException;

    Page<Advertisement> getAdvertisementsByUsername(String username, Pageable pageable, AdState state) throws UnknownUserException;

    void addImages(int adId, List<Image> images) throws UnknownAdvertisementException;

    void removeImages(int adId, List<String> imagePaths) throws UnknownAdvertisementException;

    List<String> getBrandNamesByCategory(String category);

    List<String> getTypesByBrandName(String category,String brandName);

    List<String> getCategories();

    void updateAllAdvertisement(Advertisement advertisement, AdDetails adDetails) throws UnknownAdvertisementException, UnknownCategoryException;

    void changeState(int adId, AdState stateToChange,String creatorName) throws UnknownAdvertisementException;
}
