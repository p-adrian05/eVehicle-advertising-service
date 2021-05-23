package org.example.controller.dto.rate;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RateAdvertisementDto {

    private int id;

    private String title;

    private int price;
}
