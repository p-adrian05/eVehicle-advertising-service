package org.example.model;

import lombok.Builder;
import lombok.Data;

import org.example.repository.util.AdState;
import org.example.repository.util.ProductState;


import java.sql.Timestamp;
import java.util.List;

@Data
@Builder
public class Advertisement {

        private int id;

        private String creator;

        private String category;

        private String type;

        private ProductState condition;

        private String brand;

        private String title;

        private String description;

        private int price;

        private AdState state;

        private Timestamp created;

        private List<Image> images;

        private AdDetails basicAdDetails;
}
