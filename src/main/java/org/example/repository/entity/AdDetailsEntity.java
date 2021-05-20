package org.example.repository.entity;

import lombok.*;
import org.example.model.AdDetails;
import org.example.repository.util.Drive;

import javax.persistence.*;

@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
@Data
@Table(name = "ad_details")
public class AdDetailsEntity {

    @Id
    private int adId;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "ad_id")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private AdvertisementEntity advertisement;
    @Column
    private String description;
    @Column
    private int range;
    @Column
    private int weight;
    @Column
    private double accelaration;
    @Column(name = "max_speed")
    private int maxSpeed;
    @Column
    private String color;

    public static AdDetailsEntity of(AdDetails adDetails){
        return AdDetailsEntity.builder()
                .adId(adDetails.getAdId())
                .maxSpeed(adDetails.getMaxSpeed())
                .range(adDetails.getRange())
                .weight(adDetails.getWeight())
                .accelaration(adDetails.getAccelaration())
                .color(adDetails.getColor())
                .description(adDetails.getDescription())
                .build();
    }
}

