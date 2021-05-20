package org.example.model;

import lombok.Builder;
import lombok.Data;
import org.example.repository.util.RateState;
import org.example.repository.util.RateStatus;
import org.example.repository.util.UserRateState;

import java.sql.Timestamp;

@Data
@Builder
public class UserRate {

    private int id;

    private UserRateState ratedState;

    private String description;

    private String ratingUsername;

    private int ratingUserProfileImageId;

    private String ratedUsername;

    private Advertisement advertisement;

    private RateState rateState;

    private Timestamp created;

    private RateStatus status;

    private String activationCode;
}
