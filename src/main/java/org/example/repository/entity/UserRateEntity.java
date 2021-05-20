package org.example.repository.entity;

import lombok.*;
import org.example.repository.util.RateState;
import org.example.repository.util.RateStatus;
import org.example.repository.util.UserRateState;

import javax.persistence.*;

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
