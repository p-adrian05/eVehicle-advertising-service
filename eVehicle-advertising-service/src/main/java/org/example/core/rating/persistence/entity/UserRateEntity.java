package org.example.core.rating.persistence.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.example.core.advertising.persistence.entity.AdvertisementEntity;
import org.example.core.user.persistence.entity.UserEntity;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@Entity
@Table(name = "Users_rates")
public class UserRateEntity {

    @Id
    private int rateId;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "rate_id")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private RateEntity rate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rating_user_id")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private UserEntity ratingUser;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rated_user_id")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private UserEntity ratedUser;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ad_id")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private AdvertisementEntity advertisement;

    @Column
    private UserRateState state;

    @Column
    private RateStatus status;

    @Column(name = "activation_code")
    private String activationCode;
}
