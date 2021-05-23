package org.example.core.rating.persistence.repository;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;
import org.example.core.rating.persistence.entity.RateStatus;
import org.example.core.rating.persistence.entity.UserRateState;

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
