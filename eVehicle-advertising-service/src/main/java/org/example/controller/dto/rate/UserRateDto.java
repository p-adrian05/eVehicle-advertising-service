package org.example.controller.dto.rate;

import lombok.Builder;
import lombok.Data;
import org.example.repository.util.RateState;
import org.example.repository.util.RateStatus;
import org.example.repository.util.UserRateState;

import java.sql.Timestamp;

@Data
@Builder
public class UserRateDto {

    private int id;

    private UserRateState ratedState;

    private String description;

    private String ratingUsername;

    private int ratingUserProfileImageId;

    private String ratedUsername;

    private RateAdvertisementDto advertisement;

    private RateState rateState;

    private Timestamp created;
}
