package org.example.core.advertising;

import java.util.List;

public interface AdVehicleService {


     List<String> getBrandNamesByCategory(String category);

     List<String> getCarTypesByBrandName(String category, String brandName);

     List<String> getCategories();

     String convertSortParamToValidForm(String sortParam);

}
