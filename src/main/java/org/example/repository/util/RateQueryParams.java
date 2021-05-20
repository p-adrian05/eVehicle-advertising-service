package org.example.repository.util;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@Builder
@Data
@ToString
public class RateQueryParams {

    private String ratedUsername;

    private String ratingUsername;

    private Integer advertisementId;

    private UserRateState ratedState;

    private RateStatus rateStatus;
}
