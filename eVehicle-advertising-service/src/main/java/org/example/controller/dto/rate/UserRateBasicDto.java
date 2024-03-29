package org.example.controller.dto.rate;

import lombok.Builder;
import lombok.Data;

import org.example.core.rating.persistence.entity.RateStatus;
import org.example.core.rating.persistence.entity.UserRateState;


import java.sql.Timestamp;

@Data
@Builder
public class UserRateBasicDto {

    private int id;

    private UserRateState ratedState;

    private String ratingUsername;

    private int ratingUserProfileImageId;

    private String ratedUsername;

    private RateAdvertisementDto advertisement;

    private Timestamp created;

    private RateStatus status;

    private String activationCode;
}
