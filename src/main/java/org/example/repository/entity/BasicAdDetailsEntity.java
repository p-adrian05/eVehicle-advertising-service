package org.example.repository.entity;

import lombok.*;
import org.example.model.AdDetails;
import org.example.repository.util.Drive;
import org.example.repository.util.ProductState;

import javax.persistence.*;

@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
@Data
@Table(name = "basic_ad_details")
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
    private Drive drive;

    public static BasicAdDetailsEntity of(AdDetails adDetails){
        return  BasicAdDetailsEntity.builder()
                .adId(adDetails.getAdId())
                .performance(adDetails.getPerformance())
                .batterySize(adDetails.getBatterySize())
                .km(adDetails.getKm())
                .chargeSpeed(adDetails.getChargeSpeed())
                .drive(adDetails.getDrive())
                .seatNumber(adDetails.getSeatNumber())
                .year(adDetails.getYear())
                .build();
    }
}
