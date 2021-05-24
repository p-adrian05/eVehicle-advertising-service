package org.example.core.advertising.model;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.example.core.advertising.persistence.AdState;
import org.example.core.advertising.persistence.ProductState;

import java.sql.Timestamp;
import java.util.List;

@Builder
@EqualsAndHashCode
@RequiredArgsConstructor
@Getter
public class UpdateAdvertisementDto {

        private final int id;

        private final String category;

        private final String type;

        private final ProductState condition;

        private final String brand;

        private final String title;

        private final int price;
}
