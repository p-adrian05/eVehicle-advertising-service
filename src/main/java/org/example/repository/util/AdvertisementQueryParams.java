package org.example.repository.util;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.example.repository.util.AdState;
import org.example.repository.util.Drive;
import org.example.repository.util.ProductState;

@Data
@Builder
@AllArgsConstructor
public class AdvertisementQueryParams {

    private String category;

    private String brand;

    private String type;

    private Drive drive;

    private ProductState condition;

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
