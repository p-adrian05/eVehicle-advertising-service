package org.example.core.rating.persistence.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.example.core.advertising.persistence.entity.AdvertisementEntity;
import org.example.core.user.persistence.entity.UserEntity;


import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
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
@Table(name = "USERS_RATES")
public class UserRateEntity {

    @EmbeddedId
    private UserRateId id;

    @OneToOne(fetch = FetchType.LAZY,cascade = CascadeType.ALL)
    @MapsId("rateId")
    @JoinColumn(name = "rate_id")
    private RateEntity rate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rating_user_id")
    @MapsId("ratingUserId")
    private UserEntity ratingUser;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("ratedUserId")
    @JoinColumn(name = "rated_user_id")
    private UserEntity ratedUser;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ad_id")
    private AdvertisementEntity advertisement;

    @Column
    @Enumerated(EnumType.STRING)
    private UserRateState state;

    @Column
    @Enumerated(EnumType.STRING)
    private RateStatus status;

    @Column(name = "activation_code")
    private String activationCode;
}
