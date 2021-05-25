package org.example.core.advertising.persistence.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.example.core.advertising.persistence.Drive;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.MapsId;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
@Data
@Table(name = "BASIC_AD_DETAILS")
public class BasicAdDetailsEntity {

    @Id
    private int adId;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "ad_id")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private AdvertisementEntity advertisement;
    @Column
    private int year;
    @Column(name = "battery_size")
    private int batterySize;
    @Column
    private int km;
    @Column(name = "charge_speed")
    private int chargeSpeed;
    @Column(name = "seat_number")
    private int seatNumber;
    @Column
    private int performance;
    @Column
    @Enumerated(EnumType.STRING)
    private Drive drive;

}
