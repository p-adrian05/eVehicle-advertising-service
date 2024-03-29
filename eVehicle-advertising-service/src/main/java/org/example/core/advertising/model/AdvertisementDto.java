package org.example.core.advertising.model;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.example.core.advertising.persistence.AdState;
import org.example.core.advertising.persistence.ProductState;

import java.sql.Timestamp;
import java.util.List;
import java.util.Set;

@Builder
@EqualsAndHashCode
@RequiredArgsConstructor
@Getter
@ToString
public class AdvertisementDto {

        private final int id;

        private final String creator;

        private final String category;

        private final String type;

        private final ProductState condition;

        private final String brand;

        private final String title;

        private final double price;

        private final String currency;

        private final AdState state;

        private final Timestamp created;

        private final Set<String> imagePaths;
}
