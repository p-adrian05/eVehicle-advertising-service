package org.example.controller.dto.advertisement;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.example.core.advertising.persistence.AdState;
import org.example.core.advertising.persistence.ProductState;


import java.sql.Timestamp;
import java.util.List;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class AdvertisementDto {

    private int id;

    private String type;

    private String brand;

    private String creator;

    private String category;

    private String title;

    private int price;

    private AdState state;

    private Timestamp created;

    private List<String> imagePaths;

    private ProductState condition;

    private AdvertisementBasicDetailsDto basicAdDetails;
}
