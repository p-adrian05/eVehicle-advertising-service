package org.example.core.rating.persistence.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserRateId implements Serializable {

    private Integer rateId;

    private Integer ratingUserId;

    private Integer ratedUserId;

}
