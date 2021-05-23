package org.example.controller.dto.advertisement;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.example.model.Image;
import org.example.repository.util.AdState;
import org.example.repository.util.ProductState;

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

    private String title;

    private int price;

    private AdState state;

    private Timestamp created;

    private List<String> imagePaths;

    private ProductState condition;

    private AdvertisementBasicDetailsDto basicAdDetails;
}
