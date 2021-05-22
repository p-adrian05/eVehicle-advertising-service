package org.example.core.advertising.persistence.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
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
}

