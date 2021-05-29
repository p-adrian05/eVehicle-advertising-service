package org.example.core.advertising;

import org.example.core.advertising.exception.UnknownAdvertisementException;
import org.example.core.advertising.model.AdDetailsDto;
import org.example.core.advertising.persistence.entity.AdvertisementEntity;

import java.util.Optional;

public interface AdDetailsService {

    Optional<AdDetailsDto> getAdDetailsById(int id);
    void updateAdDetails(AdDetailsDto adDetailsDto) throws UnknownAdvertisementException;

    void createAdDetails(AdDetailsDto adDetailsDto, AdvertisementEntity advertisementEntity);
}
