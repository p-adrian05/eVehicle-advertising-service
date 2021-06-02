package org.example.core.advertising;

import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.example.core.advertising.exception.UnknownAdvertisementException;
import org.example.core.advertising.exception.UnknownCategoryException;
import org.example.core.advertising.model.AdDetailsDto;
import org.example.core.advertising.model.AdLabelDto;
import org.example.core.advertising.model.AdvertisementDto;
import org.example.core.advertising.model.CreateAdDto;
import org.example.core.advertising.model.UpdateAdvertisementDto;
import org.example.core.advertising.persistence.AdState;
import org.example.core.advertising.persistence.repository.AdvertisementQueryParams;
import org.example.core.user.exception.UnknownUserException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.web.multipart.MultipartFile;

import java.util.Currency;
import java.util.Optional;

public interface AdvertisementService {

    void createAdvertisement(CreateAdDto advertisementDto, AdDetailsDto adDetailsDto, MultipartFile[] images)
        throws
        UnknownUserException, UnknownCategoryException, UnknownAdvertisementException, FileUploadException;

    void updateAdvertisementWithDetails(UpdateAdvertisementDto advertisementDto, AdDetailsDto adDetailsDto,
                                        MultipartFile[] images)
        throws UnknownCategoryException, UnknownAdvertisementException, UnknownUserException, FileUploadException;

    Optional<AdvertisementDto> getAdvertisementById(int id);

    Optional<AdDetailsDto> getAdDetailsById(int id);

    Slice<AdLabelDto> getAdvertisements(AdvertisementQueryParams params, Pageable pageable, Currency currency);

    Page<AdLabelDto> getAdvertisementsByUsername(String username, Pageable pageable, AdState state);

    void changeState(int adId, AdState stateToChange, String creatorName) throws UnknownAdvertisementException;

}
