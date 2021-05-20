package org.example.controller.dto.rate;

import lombok.Builder;
import lombok.Data;
import org.example.repository.util.AdState;

import java.sql.Timestamp;

@Data
@Builder
public class RateAdvertisementDto {

    private int id;

    private String title;

    private int price;
}
