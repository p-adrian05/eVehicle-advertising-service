package org.example.core.advertising;

import org.example.core.advertising.exception.MaximumSavedAdsReachedException;
import org.example.core.advertising.exception.UnknownAdvertisementException;
import org.example.core.user.exception.UnknownUserException;

import java.util.List;
import java.util.Map;

public interface AdUtilService {


     List<String> getBrandNamesByCategory(String category);

     List<String> getCarTypesByBrandName(String category, String brandName);

     List<String> getCategories();

     String convertSortParamToValidForm(String sortParam);

     Map<Integer, String> getSavedAdvertisementTitlesByUsername(String username);

     void removeSaveAd(String username, int adId) throws UnknownUserException, UnknownAdvertisementException,
         MaximumSavedAdsReachedException;
     void addSaveAd(String username, int adId) throws UnknownUserException, UnknownAdvertisementException,
         MaximumSavedAdsReachedException;
}
