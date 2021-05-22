package org.example.core.advertising;

import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.example.core.advertising.exception.UnknownAdvertisementException;
import org.example.core.advertising.exception.UnknownCategoryException;
import org.example.core.advertising.model.AdDetailsDto;
import org.example.core.advertising.model.AdvertisementDto;
import org.example.core.advertising.persistence.AdState;
import org.example.core.advertising.persistence.repository.AdvertisementQueryParams;
import org.example.core.user.exception.UnknownUserException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.web.multipart.MultipartFile;

import javax.security.auth.message.AuthException;
import java.util.List;

public interface AdvertisementService {

     void createAdvertisement(AdvertisementDto advertisementDto, AdDetailsDto adDetailsDto, MultipartFile[] images) throws
         UnknownUserException, UnknownCategoryException, UnknownAdvertisementException, FileUploadException;

    void updateAdvertisement(AdvertisementDto advertisementDto) throws UnknownCategoryException, UnknownAdvertisementException;
    void updateAllAdvertisement(AdvertisementDto advertisementDto, AdDetailsDto adDetailsDto, MultipartFile[] images) throws UnknownCategoryException, UnknownAdvertisementException, UnknownUserException, FileUploadException;

    AdvertisementDto getAdvertisementById(int id) throws UnknownAdvertisementException, UnknownUserException;

    AdDetailsDto getAdDetailsById(int id) throws UnknownAdvertisementException;

    void updateAdDetails(AdDetailsDto adDetailsDto) throws UnknownAdvertisementException;

    Slice<AdvertisementDto> getAdvertisements(AdvertisementQueryParams params, Pageable pageable);

    List<AdvertisementDto> getSavedAdvertisementsByUsername(String username) throws UnknownUserException;

    Page<AdvertisementDto> getAdvertisementsByUsername(String username, Pageable pageable, AdState state) throws UnknownUserException;

    String convertSortParamToValidForm(String sortParam);

    List<String> getBrandNamesByCategory(String category);

    List<String> getCarTypesByBrandName(String category,String brandName);

    List<String> getCategories();

    void changeState(int adId,AdState stateToChange,String creatorName) throws UnknownAdvertisementException;

}
