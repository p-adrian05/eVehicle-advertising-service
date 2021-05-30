package org.example.core.advertising;

import java.util.List;
import java.util.Map;

public interface AdVehicleService {


     List<String> getBrandNamesByCategory(String category);

     List<String> getCarTypesByBrandName(String category, String brandName);

     List<String> getCategories();

     String convertSortParamToValidForm(String sortParam);

     Map<Integer, String> getSavedAdvertisementTitlesByUsername(String username);
}
