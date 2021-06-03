package org.example.core.rating.model;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.example.core.advertising.model.AdvertisementDto;
import org.example.core.rating.persistence.entity.RateState;
import org.example.core.rating.persistence.entity.RateStatus;
import org.example.core.rating.persistence.entity.UserRateState;


import java.sql.Timestamp;

@Builder
@EqualsAndHashCode
@RequiredArgsConstructor
@Getter
@ToString
public class UserRateDto {

    private final UserRateState ratedState;

    private final String description;

    private final String ratingUsername;

    private final int ratingUserProfileImageId;

    private final String ratedUsername;

    private final AdvertisementDto advertisement;

    private final RateState rateState;

    private final Timestamp created;

    private final RateStatus status;

    private final String activationCode;
}
