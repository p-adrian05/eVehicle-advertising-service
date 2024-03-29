package org.example.core.advertising.persistence.repository;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.example.core.advertising.persistence.Drive;
import org.example.core.advertising.persistence.ProductState;

@Data
@Builder
@AllArgsConstructor
public class AdvertisementQueryParams {

    private String category;

    private String brand;

    private String type;

    private Drive drive;

    private ProductState productState;

    private Integer seatNumber;

    private Integer minYear;

    private Integer maxYear;

    private Integer minKm;

    private Integer maxKm;

    private Integer minPrice;

    private Integer maxPrice;

    private Integer minBatterySize;

    private Integer maxBatterySize;

    private Integer minPerformance;

    private Integer maxPerformance;

    private Integer minChargeSpeed;

    private Integer maxChargeSpeed;

}
